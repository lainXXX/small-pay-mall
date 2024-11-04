package com.rem.controller;

import com.alipay.api.AlipayApiException;
import com.rem.constants.Constants;
import com.rem.dto.CartDTO;
import com.rem.res.PayOrderRes;
import com.rem.response.Response;
import com.rem.service.IPayOrderService;
import com.rem.config.AlipayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/pay")
@CrossOrigin("*")
@Slf4j
public class PayOrderController {

    @Autowired
    private IPayOrderService payOrderService;

    @PostMapping("/order/create")
    public Response<String> createOrder(@RequestBody CartDTO cartDTO) {
        log.info("创建订单 {}", cartDTO);
        try {
            PayOrderRes orderRes = payOrderService.createOrder(cartDTO);
            log.info("商品下单，根据商品ID创建支付单完成 userId:{} orderId:{}", orderRes.getUserId(), orderRes.getOrderId());
            return Response.<String>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(orderRes.getPayUrl())
                    .build();

        } catch (AlipayApiException e) {
            log.error("商品下单，根据商品ID创建支付单失败 userId:{} productId:{}", cartDTO.getUserId(), cartDTO.getItemId());
            return Response.<String>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @PostMapping("/notify")
    public String payNotify(HttpServletRequest request) throws AlipayApiException {
        String message = payOrderService.payNotify(request);
        log.info("支付结果 {}", message);
        return message;
    }

}
