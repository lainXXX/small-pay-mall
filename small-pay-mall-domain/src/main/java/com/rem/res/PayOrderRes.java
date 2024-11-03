package com.rem.res;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PayOrderRes {
    private String userId;
    private String orderId;
    private String payUrl;
}
