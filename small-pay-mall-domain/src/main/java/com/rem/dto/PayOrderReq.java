package com.rem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PayOrderReq {
    @JsonProperty("out_trade_no")
    public String orderId;
    @JsonProperty("total_amount")
    public BigDecimal price;
    @JsonProperty("subject")
    public String itemName;
    @JsonProperty("body")
    public String description;

}
