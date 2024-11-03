package com.rem.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Builder
public class CartDTO {

    public String userId;
    public String itemId;
    public String itemName;
    public BigDecimal totalAmount;

}
