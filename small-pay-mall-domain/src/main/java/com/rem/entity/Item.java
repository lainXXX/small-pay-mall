package com.rem.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;


@Data
public class Item implements Serializable {
    /**
     * 自增id
     */
    private Integer id;

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
     * 商品描述
     */
    private String itemDesc;

    /**
     * 商品库存
     */
    private Integer itemQuantity;

    /**
     * 商品状态
     */
    private Integer itemStatus;

    /**
     * 商品价格
     */
    private BigDecimal amount;

    private static final long serialVersionUID = 1L;

}