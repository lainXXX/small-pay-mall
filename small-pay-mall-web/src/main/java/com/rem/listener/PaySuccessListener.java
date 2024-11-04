package com.rem.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaySuccessListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
//        可以进行充会员提示 返利提示 提示充值 发货提示 减少库存
        log.info("支付成功后的处理逻辑 {待开发}");
    }
}
