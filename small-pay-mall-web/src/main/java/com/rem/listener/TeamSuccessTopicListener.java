package com.rem.listener;

import com.google.gson.Gson;
import com.rem.dto.NotifyRequestDTO;
import com.rem.service.IPayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: rem
 * @Date: 2025/03/23/10:10
 * @Description: 结算完成消息监听
 */
@Slf4j
@Component
public class TeamSuccessTopicListener {

    @Autowired
    private IPayOrderService payOrderService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${spring.rabbitmq.config.consumer.topic_team_success.queue}"),
                    exchange = @Exchange(value = "${spring.rabbitmq.config.consumer.topic_team_success.exchange}", type = ExchangeTypes.TOPIC),
                    key = "${spring.rabbitmq.config.consumer.topic_team_success.routing_key}"
            )
    )
    public void listener(String message) {
        log.info("接收到消息 message:{}", message);
        try {
            if (StringUtils.isBlank(message)) {
                log.error("message is empty");
                return;
            }
            NotifyRequestDTO notifyRequestDTO = new Gson().fromJson(message, NotifyRequestDTO.class);
            payOrderService.changeOrderMarketSettlement(notifyRequestDTO.getOutTradeNoList());
            log.info("拼团组队完成- 结算完成 {}", new Gson().toJson(notifyRequestDTO));
        } catch (Exception e) {
            log.error("拼团回调，组队完成，结算失败 {}", message);
        }
    }

}
