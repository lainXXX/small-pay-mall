package com.rem.dto;

import lombok.Data;

@Data
public class CartDTO {

    private String userId;

    private String itemId;

    private String teamId;

    private Long activityId;

    private Integer marketType = 0;
}
