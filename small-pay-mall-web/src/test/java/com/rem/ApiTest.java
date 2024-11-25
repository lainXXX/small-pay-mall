package com.rem;

import com.alipay.api.AlipayApiException;
import com.rem.constants.Constants;
import com.rem.dto.LoginDTO;
import com.rem.entity.PayOrder;
import com.rem.service.IPayOrderService;
import com.rem.service.IWXLoginService;
import com.rem.service.impl.PayOrderServiceImpl;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class ApiTest {

    @Autowired
    private IPayOrderService payOrderService;

    @Autowired
    private PayOrderServiceImpl payOrderServiceImpl;

    @Autowired
    private IWXLoginService wxLoginService;
/*    @Test
    public void test() throws AlipayApiException {
        CartDTO dto = CartDTO.builder()
                .userId("userId123")
                .itemId("123456")
                .itemName("测试商品")
                .totalAmount(new BigDecimal("100.00"))
                .build();
        PayOrderRes orderRes = payOrderService.createOrder(dto);
        System.out.println(orderRes);
    }*/


    @Test
    public void testOrderId() {
        String orderId = RandomStringUtils.randomNumeric(3) + System.currentTimeMillis();
        System.out.println(orderId);
    }

    @Test
    public void testPayOrder() throws AlipayApiException {
        payOrderServiceImpl.lambdaUpdate()
                .set(PayOrder::getPayUrl, "form")
                .set(PayOrder::getUpdateTime, LocalDateTime.now())
                .eq(PayOrder::getOrderId, "12345")
                .update();
    }

    @Test
    public void testStatusList() {
        /*List<PayOrder> list = payOrderService.lambdaQuery()
                .apply("status = 'PAY_SUCCESS'")
                .list();*/
        List<PayOrder> closeOrder = payOrderService.lambdaQuery()
                .eq(PayOrder::getStatus, Constants.OrderStatusEnum.PAY_WAIT.getCode())
                .apply("NOW() > create_time + INTERVAL 1 MINUTE")
                .list();
        System.out.println(closeOrder);
    }

    @Test
    public void testCreateQrCodeTicket() throws IOException {
        LoginDTO qrCodeTicket = wxLoginService.createQrCodeTicket();
        System.out.println(qrCodeTicket);
    }


}
