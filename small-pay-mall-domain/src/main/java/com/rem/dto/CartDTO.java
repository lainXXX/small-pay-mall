package com.rem.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartDTO {

    private String userId;

    private String itemId;

    private String teamId;

    private Long activityId;

    private String itemName;

    private String itemImage;

    private BigDecimal totalAmount;

    private Integer marketType = 0;
}
