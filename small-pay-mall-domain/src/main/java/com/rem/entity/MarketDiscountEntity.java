package com.rem.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author: rem
 * @Date: 2025/03/24/15:59
 * @Description: 拼团优惠实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketDiscountEntity {

    private BigDecimal originalPrice;
    private BigDecimal discountPrice;
    private BigDecimal payPrice;

}
