package com.rem.vo;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class WeixinTemplateMessageVO {

    private String touser;
    private String template_id;
    private String url = "http://www.javarem.top";
    private Map<String, Map<String, String>> data = new HashMap<>();

    public WeixinTemplateMessageVO(String touser, String template_id) {
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


    public static WeixinTemplateMessageVO createPayTemplate(String touser, String templateId, String itemName, String amount, String payTime) {
        WeixinTemplateMessageVO wxTemplateDTO = new WeixinTemplateMessageVO(touser, templateId);
        wxTemplateDTO.put("message", "感谢使用RemMall");
        wxTemplateDTO.put("itemName", itemName);
        wxTemplateDTO.put("amount", amount);
        wxTemplateDTO.put("payTime", payTime);
        return wxTemplateDTO;
    }

    public static WeixinTemplateMessageVO createLoginTemplate(String touser, String LoginTemplateId) {
        WeixinTemplateMessageVO wxTemplateDTO = new WeixinTemplateMessageVO(touser, LoginTemplateId);
        wxTemplateDTO.put("user_id", touser);
        wxTemplateDTO.put("login_time", LocalDateTime.now().toString());
        wxTemplateDTO.put("first", "登录成功");
        return wxTemplateDTO;
    }

    public static WeixinTemplateMessageVO createRefundTemplate(String touser, String refundTemplateId, String itemName, String amount, String refundTime) {
        WeixinTemplateMessageVO wxTemplateDTO = new WeixinTemplateMessageVO(touser, refundTemplateId);
        wxTemplateDTO.put("message", "您在RemMall上进行了一项退货操作");
        wxTemplateDTO.put("itemName", itemName);
        wxTemplateDTO.put("amount", amount);
        wxTemplateDTO.put("refundTime", refundTime);
        return wxTemplateDTO;
    }
}
