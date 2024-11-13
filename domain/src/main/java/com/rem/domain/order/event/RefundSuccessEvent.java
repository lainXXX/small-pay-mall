package com.rem.domain.order.event;

import com.rem.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RefundSuccessEvent extends BaseEvent<RefundSuccessEvent.RefundSuccessMessage> {
    @Override
    public EventMessage<RefundSuccessMessage> buildEventMessage(RefundSuccessMessage data) {
        return EventMessage.<RefundSuccessMessage>builder()
                .id(RandomStringUtils.randomNumeric(16))
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    @Override
    public String topic() {
        return "refund_success";
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RefundSuccessMessage {
        private String orderId;
        private String itemName;
        private String itemId;
        private String reason;
        private String userId;
        private String amount;
        private String messageTemplate;
    }
}
