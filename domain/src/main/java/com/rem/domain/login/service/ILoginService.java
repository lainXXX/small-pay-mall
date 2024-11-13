package com.rem.domain.login.service;


import com.rem.api.dto.LoginDTO;

import java.io.IOException;

public interface ILoginService {


    LoginDTO createQrCodeTicket() throws IOException;

    String checkLogin(String ticket);

    String getLoginStatus(String ticket, String openid);

    String post(String xmlString) throws IOException;

}
