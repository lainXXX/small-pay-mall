package com.rem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RefundOrderDTO {
    private String userId;
    private String orderId;
    private String itemId;
    private String itemName;
    @JsonProperty("totalAmount")
    private String amount;
    private String reason;
}
