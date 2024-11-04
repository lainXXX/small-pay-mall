package com.rem.service.impl;

import com.google.gson.Gson;
import com.rem.context.ThreadContext;
import com.rem.req.WXQrCodeReq;
import com.rem.res.AccessTokenRes;
import com.rem.res.WXQrCodeRes;
import com.rem.service.IWXLonginService;
import com.rem.service.IWXService;
import com.rem.vo.WeixinTemplateMessageVO;
import com.rem.weixin.message.TextMessage;
import com.rem.weixin.properties.WXProperties;
import com.rem.weixin.utils.WXUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WXLoginServiceImpl implements IWXLonginService {

    private String credential = "client_credential";

    @Value("${wx.template_id}")
    private String template_id;

    @Autowired
    private IWXService wxService;

    @Autowired
    private WXProperties wxProperties;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public String createQrCodeTicket() throws IOException {
//        TODO 1.先从缓存中取出accessToken 通过appid
        String accessToken = redisTemplate.opsForValue().get(wxProperties.getAppID());
        log.info("accessToken {}", accessToken);
//        判断缓存中的accessToken是否为空
        if (accessToken == null) {
//            如果为空 通过http请求获取accessToken
            Call<AccessTokenRes> call = wxService.getAccessToken(credential, wxProperties.getAppID(), wxProperties.getAppSecret());
            AccessTokenRes accessTokenRes = call.execute().body();
//            判断请求是否成功
            if (accessTokenRes == null) {
                throw new RuntimeException("accessToken为空");
            }
            accessToken = accessTokenRes.getAccessToken();
//            TODO 2，把获取的accessToken存入缓存
            redisTemplate.opsForValue().set(wxProperties.getAppID(), accessToken, 6900, TimeUnit.SECONDS);
        }
//        从redis缓存中获取ticket
        String ticket = redisTemplate.opsForValue().get(accessToken);
//        判断ticket是否为空 为空则重新生成
        if (ticket == null) {
            // 2. 生成 ticket
            WXQrCodeReq QrCodeReq = WXQrCodeReq.builder()
                    .expire_seconds(2592000)
                    .action_name(WXQrCodeReq.ActionNameTypeVO.QR_SCENE.getCode())
                    .action_info(WXQrCodeReq.ActionInfo.builder()
                            .scene(WXQrCodeReq.ActionInfo.Scene.builder()
                                    .scene_id(100601)
                                    .build())
                            .build())
                    .build();
            Call<WXQrCodeRes> qrCodeCall = wxService.getQrCode(accessToken, QrCodeReq);
            WXQrCodeRes qrCodeRes = qrCodeCall.execute().body();
            if (qrCodeRes == null) {
                throw new RuntimeException("qrCodeRes为空");
            }
            ticket = qrCodeRes.getTicket();
            redisTemplate.opsForValue().set(accessToken, ticket, 6900, TimeUnit.SECONDS);
        }
        return ticket;
    }

    @Override
    public String checkLogin(String ticket) {
//        TODO 查询缓存
        String openid = redisTemplate.opsForValue().get(ticket);
        ThreadContext.setCurrentThread(openid);
        return openid;
    }

    @Override
    public String getLoginStatus(String ticket, String openid) {
//        扫描二维码后用户会通过post请求推送一个事件 包括ticket和 FromUserName	发送方账号 也就是openid
//        把这个信息存入缓存 用户登录验证
        redisTemplate.opsForValue().set(ticket, openid, 30, TimeUnit.SECONDS);
        String accessToken = redisTemplate.opsForValue().get(wxProperties.getAppID());
        if (accessToken == null) {
            throw new RuntimeException("token为空");
        }
        return accessToken;
    }

    @Override
    public String post(String xmlString) throws IOException {
            TextMessage message = (TextMessage) WXUtils.XMLToBean(xmlString, TextMessage.class);
            String ticket = message.getTicket();
            if (ticket != null) {
                String accessToken = getLoginStatus(ticket, message.getFromUserName());
                WeixinTemplateMessageVO wxTemplateDTO = new WeixinTemplateMessageVO(message.getFromUserName(), template_id);
                wxTemplateDTO.put("user_id", message.getFromUserName());
                wxTemplateDTO.put("login_time", LocalDateTime.now().toString());
                wxTemplateDTO.put("first", "登录成功");
                Gson gson = new Gson();
                String json = gson.toJson(wxTemplateDTO, wxTemplateDTO.getClass());
                log.info("{}}", json);
                Call<Void> call = wxService.sendMessage(accessToken, wxTemplateDTO);
                call.execute();
                return "";
        }
        return WXUtils.buildMessage(message.getToUserName(), message.getFromUserName(), "欢迎来到本公众号");
    }
}
