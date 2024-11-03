package com.rem.weixin.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "wx")
@Data
public class WXProperties {
    private String appID;
    private String appSecret;
    private String token;
    private String aesKeyUrl;
}
