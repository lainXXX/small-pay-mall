package com.rem;

import com.alipay.api.AlipayApiException;
import com.rem.controller.PayOrderManageController;
import com.rem.dto.CartDTO;
import com.rem.listener.PayListener;
import com.rem.po.PayOrder;
import com.rem.response.Response;
import com.rem.service.IPayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: rem
 * @Date: 2025/03/24/11:13
 * @Description:
 */
@SpringBootTest
@Slf4j
public class PayTest {

    @Resource
    private PayOrderManageController payOrderManageController;
    @Resource
    private IPayOrderService payOrderService;
    @Resource
    private PayListener payListener;

    @Test
    public void create_order() throws AlipayApiException, InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(1);
        CartDTO cartDTO = new CartDTO();
        cartDTO.setUserId("rem");
        cartDTO.setItemId("100002");
        cartDTO.setActivityId(100123L);
        cartDTO.setMarketType(1);
        Response<String> order = payOrderManageController.createOrder(cartDTO);
        log.info(order.toString());
        // 等待，消息消费。测试后，可主动关闭。
        countDownLatch.await();
    }

    @Test
    public void test() {
        List<String> list = new ArrayList<>();
        list.add("707142066493");
        payOrderService.changeOrderMarketSettlement(list);
    }

    @Test
    public void test2() {
        PayOrder order = payOrderService.lambdaQuery()
                .eq(PayOrder::getOrderId, "082768682847")
                .one();
        log.info(order.toString());

    }

    @Test
    public void test3() throws IOException {
        payListener.handlePaySuccess("608758521887", null);
    }


}
