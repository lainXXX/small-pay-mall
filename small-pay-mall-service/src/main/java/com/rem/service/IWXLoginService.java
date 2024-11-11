package com.rem.service;

import com.rem.dto.LoginDTO;

import java.io.IOException;

public interface IWXLoginService {


    LoginDTO createQrCodeTicket() throws IOException;

    String checkLogin(String ticket);

    String getLoginStatus(String ticket, String openid);

    String post(String xmlString) throws IOException;
}
