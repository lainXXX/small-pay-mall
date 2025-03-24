package com.rem.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: rem
 * @Date: 2025/03/24/10:51
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum MarketTypeEnums {

    NO_MARKET(0, "无营销配置"),
    GROUP_BUYING(1, "拼团营销"),
    ;

    private Integer code;

    private String info;

}
