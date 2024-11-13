package com.rem.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CartDTO {
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("itemId")
    private String itemId;
    @JsonProperty("itemName")
    private String itemName;
    @JsonProperty("itemImage")
    private String itemImage;
    @JsonProperty("totalAmount")
    private String totalAmount;
}
