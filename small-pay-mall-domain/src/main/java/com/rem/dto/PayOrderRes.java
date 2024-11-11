package com.rem.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class PayOrderRes {
    private String userId;
    private String orderId;
    private String payUrl;
}
