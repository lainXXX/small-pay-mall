package com.rem.trigger.http;

import com.rem.api.dto.LoginDTO;
import com.rem.api.response.Response;
import com.rem.domain.login.adapter.port.ILoginPort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/wx/login")
@CrossOrigin("*")
@Slf4j
public class WXLoginController {

    @Autowired
    private ILoginPort loginPort;

    @GetMapping("/qrcode/create")
    public Response<LoginDTO> createQrCodeTicket() throws IOException {
        LoginDTO loginDTO = loginPort.createQrCodeTicket();
        if (loginDTO != null) {
            log.info("生成微信扫码登录ticket:{}", loginDTO);
            return Response.success(loginDTO);
        } else {
            return Response.error();
        }
    }

    @GetMapping("/check")
    public Response<String> checkLogin(String loginKey) {
        String openid = loginPort.checkLogin(loginKey);
        if (StringUtils.isNotBlank(openid)) {
            log.info("扫码检测登录结果 loginKey:{} openidToken:{}", loginKey, openid);
            return Response.success(openid);
        } else {
            return Response.noLoginError();
        }
    }

}
