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
public class PaySuccessEvent extends BaseEvent<PaySuccessEvent.PaySuccessMessage> {
    @Override
    public EventMessage<PaySuccessMessage> buildEventMessage(PaySuccessMessage data) {
        return EventMessage.<PaySuccessMessage>builder()
                .id(RandomStringUtils.randomNumeric(16))
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    @Override
    public String topic() {
        return "pay_success";
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaySuccessMessage {
        private String orderId;
        private String messageTemplate;
    }

}
