package com.rem.vo;


import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class WeixinTemplateMessageVO {

    private String touser = "or0Ab6ivwmypESVp_bYuk92T6SvU";
    private String template_id = "BKrK_9YWf3fqrDb0I4Vycu3YUOLEiICQGqr6a0GTNw";
    private String url = "https://weixin.qq.com";
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


}
