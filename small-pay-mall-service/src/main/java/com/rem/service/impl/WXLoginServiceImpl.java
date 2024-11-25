package com.rem.service.impl;

import com.rem.dto.AccessTokenRes;
import com.rem.dto.LoginDTO;
import com.rem.dto.WXQrCodeReq;
import com.rem.dto.WXQrCodeRes;
import com.rem.service.IWXApiService;
import com.rem.service.IWXLoginService;
import com.rem.vo.WeixinTemplateMessageVO;
import com.rem.weixin.message.TextMessage;
import com.rem.weixin.properties.WXProperties;
import com.rem.weixin.utils.WXUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WXLoginServiceImpl implements IWXLoginService {

    private String credential = "client_credential";

    @Value("${wx.login_template_id}")
    private String template_id;

    @Autowired
    private IWXApiService wxService;

    @Autowired
    private WXProperties wxProperties;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public LoginDTO createQrCodeTicket() throws IOException {
//        TODO 1.先从缓存中取出accessToken 通过appid
        String accessToken = redisTemplate.opsForValue().get(wxProperties.getAppID());
//        判断缓存中的accessToken是否为空
        if (StringUtils.isBlank(accessToken)) {
//            如果为空 通过http请求获取accessToken
            Call<AccessTokenRes> call = wxService.getAccessToken(credential, wxProperties.getAppID(), wxProperties.getAppSecret());
            AccessTokenRes accessTokenRes = call.execute().body();
//            判断请求是否成功
            if (accessTokenRes == null) {
                log.error("accessTokenRes为空");
                return null;
            }
            if (StringUtils.isBlank(accessTokenRes.getAccessToken())) {
                log.error("accessToken为空");
                return null;
            }
            accessToken = accessTokenRes.getAccessToken();
//             2，把获取的accessToken存入缓存  accessToken的真实有限期为2小时
            redisTemplate.opsForValue().set(wxProperties.getAppID(), accessToken, 6900, TimeUnit.SECONDS);
        }
        log.info("accessToken {}", accessToken);
        log.info("accessToken {}", accessToken);
        log.info("accessToken {}", accessToken);
//        利用随机数生成一个scene_id 确保用户登录二维码的唯一性
        int scene_id = new SecureRandom().nextInt(1000000000);
        // 2. 生成 ticket
            WXQrCodeReq QrCodeReq = WXQrCodeReq.builder()
                    .expire_seconds(2592000)
                    .action_name(WXQrCodeReq.ActionNameTypeVO.QR_SCENE.getCode())
                    .action_info(WXQrCodeReq.ActionInfo.builder()
                            .scene(WXQrCodeReq.ActionInfo.Scene.builder()
                                    .scene_id(scene_id)
                                    .build())
                            .build())
                    .build();
            Call<WXQrCodeRes> qrCodeCall = wxService.getQrCode(accessToken, QrCodeReq);
            WXQrCodeRes qrCodeRes = qrCodeCall.execute().body();
            if (qrCodeRes == null) {
                log.error("qrCodeRes为空");
                return null;
        }
        String loginKey = String.valueOf(scene_id);
        String eventKey = "scene_id_".concat(loginKey);
        redisTemplate.opsForValue().set(eventKey, loginKey, 300, TimeUnit.SECONDS);
        return LoginDTO.builder()
                .loginKey(loginKey)
                .ticket(qrCodeRes.getTicket())
                .build();
    }

    @Override
    public String checkLogin(String loginKey) {
//        TODO 查询缓存
        String openid = redisTemplate.opsForValue().get(loginKey);
//        ThreadContext.setCurrentThread(openid);
        if (StringUtils.isBlank(openid)) {
            return null;
        }
        redisTemplate.opsForValue().set(openid, openid, 1, TimeUnit.DAYS);
        return openid;
    }

    @Override
    public String getLoginStatus(String eventKey, String openid) {
//        扫描二维码后用户会通过post请求推送一个事件 包括ticket和 FromUserName	发送方账号 也就是openid
//        把这个信息存入缓存 用户登录验证
        String loginKey = redisTemplate.opsForValue().get("scene_id_" + eventKey);
        if (StringUtils.isBlank(loginKey)) {
            log.info("loginKey为空");
            return null;
        }
//        TODO 因为前端只有loginKey来保证用户登录id的唯一性 所以需要再使用loginKey取出ticket 这个逻辑我目前认为是有问题的 但是后续如果有机会再思考更好的处理a
        redisTemplate.opsForValue().set(loginKey, openid, 300, TimeUnit.SECONDS);
        String accessToken = redisTemplate.opsForValue().get(wxProperties.getAppID());
        if (StringUtils.isBlank(accessToken)) {
            log.info("token为空");
            return null;
        }
        return accessToken;
    }

    @Override
    public String post(String xmlString) throws IOException {

        TextMessage message = (TextMessage) WXUtils.XMLToBean(xmlString, TextMessage.class);
        String eventKey = message.getEventKey();
        if (StringUtils.isNotBlank(eventKey)) {
            String accessToken = getLoginStatus(eventKey, message.getFromUserName());
            WeixinTemplateMessageVO wxTemplateDTO = WeixinTemplateMessageVO.createLoginTemplate(message.getFromUserName(), template_id);
            Call<Void> call = wxService.sendMessage(accessToken, wxTemplateDTO);
            call.execute();
            return null;
        }
        return WXUtils.buildMessage(message.getToUserName(), message.getFromUserName(), "欢迎来到本公众号");

    }
}
