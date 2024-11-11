package com.rem.vo;

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
public class PayOrderVO implements Serializable {

        /**
         * 商品Id
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
         * 订单id
         */
        private String orderId;

        /**
         * 订单总金额
         */
        private BigDecimal totalAmount;

        /**
         * 订单状态；create-创建完成、pay_wait-等待支付、pay_success-支付成功、deal_done-交易完成、close-订单关单
         */
        private String status;


        /**
         * 支付时间
         */
        private LocalTime payTime;

        private static final long serialVersionUID = 1L;
}
