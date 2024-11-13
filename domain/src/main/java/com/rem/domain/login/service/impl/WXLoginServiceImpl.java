package com.rem.domain.login.service.impl;

import com.rem.api.dto.LoginDTO;
import com.rem.domain.login.adapter.port.ILoginPort;
import com.rem.domain.login.service.ILoginService;

import com.rem.types.exception.AppException;
import com.rem.types.sdk.weixin.properties.WXProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WXLoginServiceImpl implements ILoginService {

    private ILoginPort loginPort;


    private String credential = "client_credential";

    @Value("${wx.login_template_id}")
    private String template_id;



    @Autowired
    private WXProperties wxProperties;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public LoginDTO createQrCodeTicket() throws IOException {
        try {
            return loginPort.createQrCodeTicket();
        } catch (AppException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String checkLogin(String loginKey) {
        try {
            return loginPort.checkLogin(loginKey);
        } catch (AppException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String getLoginStatus(String ticket, String openid) {
        try {
            return loginPort.getLoginStatus(ticket, openid);
        } catch (AppException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String post(String xmlString) throws IOException {
        try {
            return loginPort.post(xmlString);
        } catch (IOException e) {
            throw new AppException(e.getMessage());
        }
    }
}
