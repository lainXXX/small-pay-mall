package com.rem.domain.login.adapter.port;

import com.rem.api.dto.LoginDTO;

import java.io.IOException;

public interface ILoginPort {

    LoginDTO createQrCodeTicket() throws IOException;

    String checkLogin(String loginKey);

    String getLoginStatus(String ticket, String openid);

    String post(String xmlString) throws IOException;

}
