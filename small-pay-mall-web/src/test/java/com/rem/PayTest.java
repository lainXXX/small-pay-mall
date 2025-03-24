package com.rem;

import com.alipay.api.AlipayApiException;
import com.rem.controller.PayOrderManageController;
import com.rem.dto.CartDTO;
import com.rem.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;

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

    @Test
    public void create_order() throws AlipayApiException {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setUserId("rem01");
        cartDTO.setItemId("100002");
        cartDTO.setActivityId(100123L);
        cartDTO.setItemName("无双飞将小八");
        cartDTO.setItemImage("http://javarem.top/images/chiikawa2.jpeg");
        cartDTO.setTotalAmount(new BigDecimal("299.00"));
        cartDTO.setMarketType(1);
        Response<String> order = payOrderManageController.createOrder(cartDTO);
        log.info(order.toString());
    }

}
