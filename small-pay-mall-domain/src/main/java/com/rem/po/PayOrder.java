package com.rem.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 
 * @TableName pay_order
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    private String source;

    private String channel;

    /**
     * 商品名称
     */
    private String itemName;

    /**
     * 商品图片url
     */
    private String itemImage;

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

    private BigDecimal discountAmount;

    private BigDecimal payAmount;

    private Integer marketType;

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

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 退款原因
     */
    private String refundReason;

    private static final long serialVersionUID = 1L;
}