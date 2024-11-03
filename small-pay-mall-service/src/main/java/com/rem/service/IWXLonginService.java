package com.rem.service;

import java.io.IOException;

public interface IWXLonginService {


    String createQrCodeTicket() throws IOException;

    String checkLogin(String ticket);

    void getLoginStatus(String ticket, String openid);

    String post(String xmlString);
}
