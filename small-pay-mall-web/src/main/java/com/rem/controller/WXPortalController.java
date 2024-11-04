package com.rem.controller;


import com.rem.service.IWXLonginService;
import com.rem.weixin.utils.WXUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/wx/portal")
@CrossOrigin("*")
@Slf4j
public class WXPortalController {

    @Value("${wx.token}")
    private String token;

    @Autowired
    private IWXLonginService loginService;

    /**
     * @param signature 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @param echostr   随机字符串
     * @return
     */
    @GetMapping("/receive")
    public String receive(String signature, String timestamp, String nonce, String echostr) {
        boolean check = WXUtils.signatureCheck(signature, timestamp, nonce, echostr, token);
        log.info("微信公众号接口配置{}", check ? "成功" : "失败");
        return check ? echostr : null;
    }

    @PostMapping("/receive")
    public String post(@RequestBody String xmlString) {
        try {
            String message = loginService.post(xmlString);
            return message;
        } catch (IOException e) {
            log.error("接收微信公众号信息请求 失败 {}",  e);
            return "";
        }
    }
}
