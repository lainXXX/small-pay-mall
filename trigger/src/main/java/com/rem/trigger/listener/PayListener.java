package com.rem.trigger.listener;

import com.rem.domain.order.service.IOrderService;
import com.rem.types.sdk.weixin.properties.WXProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class PayListener implements MessageListener {


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private WXProperties wxProperties;

    @Autowired
    private IOrderService orderService;

    @SneakyThrows
    @Override
    public void onMessage(Message message, byte[] pattern) {

        String channel = new String(message.getChannel());
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        if (StringUtils.isBlank(msg)) {
            return;
        }
        String accessToken = redisTemplate.opsForValue().get(wxProperties.getAppID());
        if (accessToken == null) {
            return;
        }
        // 根据不同的频道来决定不同的处理逻辑
        if ("pay_success".equals(channel)) {
            handlePaySuccess(msg, accessToken);
        } else if ("refund_success".equals(channel)) {
            handleRefundSuccess(msg, accessToken);
        }
        //        可以进行充会员提示 返利提示 提示充值 发货提示 减少库存
    }


    public void handleRefundSuccess(String msg, String accessToken) throws IOException {
        orderService.handleRefundSuccess(msg, accessToken);
    }

    public void handlePaySuccess(String msg, String accessToken) throws IOException {
        orderService.handlePaySuccess(msg, accessToken);
    }


}
