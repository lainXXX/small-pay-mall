package com.rem.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @TableName item
 */
@Data
public class ItemShowDTO implements Serializable {

    /**
     * 商品id
     */
    private String itemId;

    /**
     * 商品名称
     */
    private String itemName;

    /**
     * 商品图片url
     */
    private String itemImage;

    /**
     * 商品库存
     */
    private Integer itemQuantity;

    /**
     * 商品价格
     */
    private BigDecimal amount;

    private static final long serialVersionUID = 1L;
}