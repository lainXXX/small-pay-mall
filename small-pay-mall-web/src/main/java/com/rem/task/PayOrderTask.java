package com.rem.task;


import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.rem.constants.Constants;
import com.rem.controller.XXXController;
import com.rem.entity.PayOrder;
import com.rem.service.IPayOrderService;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component()
public class PayOrderTask {

    @Autowired
    private IPayOrderService payOrderService;
    @Autowired
    private XXXController xXXController;


    @Scheduled(cron = "0 0/15 * * * ?")
    public void orderCloseTask() {
        log.info("任务: 订单30分钟未支付自动关闭");
//        查询需要关闭的订单集合
        List<PayOrder> closeOrderList = payOrderService.lambdaQuery()
                .eq(PayOrder::getStatus, Constants.OrderStatusEnum.PAY_WAIT.getCode())
                .apply("NOW() > create_time + INTERVAL 30 MINUTE")
                .list();
        if (Collections.isEmpty(closeOrderList)) {
            log.info("执行完毕 没有未处理订单");
            return;
        }
//        使用stream流实现list集合订单关闭

        closeOrderList.stream().forEach(order -> {
            boolean b = payOrderService.changeOrderStatus(order.getOrderId(), Constants.OrderStatusEnum.CLOSE.getCode());
            log.info("orderId {} result : {}", order.getOrderId(), b);
        });
    }

    @Scheduled(cron = "0 0/30 * * * ?")
    public void OrderWaitTask() {
        log.info("任务 处理掉单订单");
//        查询掉单的订单 也就是创建订单但是却没有进入支付页面的订单
        List<PayOrder> failOrderList = payOrderService.lambdaQuery()
                .eq(PayOrder::getStatus, Constants.OrderStatusEnum.CREATE.getCode())
                .apply("NOW() > create_time + INTERVAL 10 MINUTE")
                .list();
        if (Collections.isEmpty(failOrderList)) {
            log.info("执行完毕 没有掉单订单");
            return;
        }
        failOrderList.stream().forEach(order -> {
            AlipayTradePagePayResponse response = null;
            try {
                response = payOrderService.doPay(order.getOrderId(), order.getTotalAmount(), order.getItemName());
            } catch (AlipayApiException e) {
                log.info("获取支付单失败 {}", e.getMessage());
            }
            String code = response.getCode();
            // 判断状态码 判断支付是否成功
            if ("10000".equals(code)) {
                payOrderService.changeOrderStatus(order.getOrderId(), Constants.OrderStatusEnum.PAY_SUCCESS.getCode());
            }
        });
    }
}
