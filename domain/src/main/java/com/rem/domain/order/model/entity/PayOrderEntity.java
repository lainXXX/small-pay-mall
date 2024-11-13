package com.rem.domain.order.model.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayOrderEntity {
    private String userId;
    private String orderId;
    private String payUrl;
}
