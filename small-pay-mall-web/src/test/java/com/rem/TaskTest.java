package com.rem;

import com.rem.constants.Constants;
import com.rem.entity.PayOrder;
import com.rem.service.IPayOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Author: rem
 * @Date: 2024/11/24/21:36
 * @Description:
 */
@SpringBootTest
public class TaskTest {

    @Autowired
    private IPayOrderService payOrderService;

    @Test
    public void testTask() {
        List<PayOrder> closeOrderList = payOrderService.lambdaQuery()
                .select(PayOrder::getItemName)
                .eq(PayOrder::getStatus, Constants.OrderStatusEnum.PAY_WAIT.getCode())
                .apply("NOW() > create_time + INTERVAL 1 MINUTE")
                .list();
        System.out.println(closeOrderList);
    }

}
