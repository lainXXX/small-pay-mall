package com.rem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccessTokenRes {
    @JsonProperty("access_token") // 指定 JSON 中的字段名
    private String accessToken;

    @JsonProperty("expires_in") // 指定 JSON 中的字段名
    private long expiredTime;

    @JsonProperty("err_code") // 指定 JSON 中的字段名
    private Integer errCode;

    @JsonProperty("err_msg") // 指定 JSON 中的字段名
    private String errMsg;
}
