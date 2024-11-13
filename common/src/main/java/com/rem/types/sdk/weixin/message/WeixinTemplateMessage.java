package com.rem.types.sdk.weixin.message;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class WeixinTemplateMessage {

    private String touser;
    private String template_id;
    private String url = "http://www.javarem.top";
    private Map<String, Map<String, String>> data = new HashMap<>();

    public WeixinTemplateMessage(String touser, String template_id) {
        this.touser = touser;
        this.template_id = template_id;
    }

    public static class Data {
        private String value;
        private String color;
    }

    public void put(String key, String value) {
        data.put(key, new HashMap<String, String>() {
            private static final long serialVersionUID = 7092338402387318563L;
            {
                put("value", value);
            }
        });
    }


    public static WeixinTemplateMessage createPayTemplate(String touser, String templateId, String itemName, String amount, String payTime) {
        WeixinTemplateMessage wxTemplateDTO = new WeixinTemplateMessage(touser, templateId);
        wxTemplateDTO.put("message", "感谢使用RemMall");
        wxTemplateDTO.put("itemName", itemName);
        wxTemplateDTO.put("amount", amount);
        wxTemplateDTO.put("payTime", payTime);
        return wxTemplateDTO;
    }

    public static WeixinTemplateMessage createLoginTemplate(String touser, String LoginTemplateId) {
        WeixinTemplateMessage wxTemplateDTO = new WeixinTemplateMessage(touser, LoginTemplateId);
        wxTemplateDTO.put("user_id", touser);
        wxTemplateDTO.put("login_time", LocalDateTime.now().toString());
        wxTemplateDTO.put("first", "登录成功");
        return wxTemplateDTO;
    }

    public static WeixinTemplateMessage createRefundTemplate(String touser, String refundTemplateId, String itemName, String amount, String refundTime) {
        WeixinTemplateMessage wxTemplateDTO = new WeixinTemplateMessage(touser, refundTemplateId);
        wxTemplateDTO.put("message", "您在RemMall上进行了一项退货操作");
        wxTemplateDTO.put("itemName", itemName);
        wxTemplateDTO.put("amount", amount);
        wxTemplateDTO.put("refundTime", refundTime);
        return wxTemplateDTO;
    }
}
