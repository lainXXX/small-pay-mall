package com.rem.types.sdk.weixin.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class SubscriberMessage {
    private String orderId;
    private String itemName;
    private String itemId;
    private String reason;
    private String userId;
    private String amount;
    private String messageTemplate;
    // 默认构造函数
    public SubscriberMessage() {
        // 如果需要，可以在这里初始化字段
    }
}
