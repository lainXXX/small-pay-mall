package com.rem.weixin.properties;

import lombok.Data;

@Data
public class AccessToken {

    private String accessToken;

    private long expiredTime;
    //    错误码
    private Integer errCode;
    //    错误信息
    private String errMsg;

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = System.currentTimeMillis() + expiredTime * 1000;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > this.expiredTime;
    }
}
