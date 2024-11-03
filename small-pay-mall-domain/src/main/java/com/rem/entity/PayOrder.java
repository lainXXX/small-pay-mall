package com.rem.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 
 * @TableName pay_order
 */
@Data
@Builder
public class PayOrder implements Serializable {
    /**
     * 自增id
     */
    private Integer id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 商品id
     */
    private String itemId;

    /**
     * 商品名称
     */
    private String itemName;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 下单时间
     */
    private LocalDateTime orderTime;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单状态；create-创建完成、pay_wait-等待支付、pay_success-支付成功、deal_done-交易完成、close-订单关单
     */
    private String status;

    /**
     * 支付信息
     */
    private String payUrl;

    /**
     * 支付时间
     */
    private LocalTime payTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;
}