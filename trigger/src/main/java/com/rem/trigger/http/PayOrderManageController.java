package com.rem.trigger.http;

import com.alipay.api.AlipayApiException;
import com.google.gson.Gson;
import com.rem.api.dto.CartDTO;
import com.rem.api.dto.PayOrderDTO;
import com.rem.api.dto.PayOrderShowDTO;
import com.rem.api.dto.RefundOrderDTO;
import com.rem.api.response.Response;
import com.rem.domain.order.model.entity.PayOrderEntity;
import com.rem.domain.order.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/pay")
@CrossOrigin("*")
@Slf4j
public class PayOrderManageController {

    @Autowired
    private IOrderService orderService;

/*     TODO 为什么要使用@RequestBody String dto而不是直接CartDTO cartDTO呢 因为打成jar包会报错没有构造器
    我试了很多种改造类都没有成功 所以迫不得已使用String 以后有机会研究出来再改*/
    @PostMapping("/order/create")
    public Response<String> createOrder(@RequestBody String dto) throws AlipayApiException {
        CartDTO cartDTO = new Gson().fromJson(dto, CartDTO.class);
        log.info("创建订单 {}", cartDTO);
        PayOrderEntity orderRes = orderService.createOrder(cartDTO);
        log.info("商品下单，根据商品ID创建支付单完成 userId:{} orderId:{}", orderRes.getUserId(), orderRes.getOrderId());
        return Response.success(orderRes.getPayUrl());
    }

    @PostMapping("/order")
    public Response<String> getPayUrl(@RequestBody PayOrderDTO orderDTO) {
        String orderId = orderDTO.getOrderId();
        String payUrl = orderService.getPayUrl(orderId);
        log.info("用户支付操作，订单id {} 用户id:{}", orderId, payUrl);
        return Response.success(payUrl);
    }

    @PostMapping("/notify")
    public String payNotify(HttpServletRequest request) throws AlipayApiException {
        return orderService.payNotify(request);
    }

    @GetMapping("order/search")
    public Response<List<PayOrderShowDTO>> searchPayOrder(@RequestHeader("token") String userId) {
        List<PayOrderShowDTO> payOrderVOList = orderService.searchPayOrder(userId);
        log.info("查询用户订单 userId:{}", userId);
        return Response.success(payOrderVOList);
    }

    @PostMapping("/order/refund")
    public Response<String> refund(@RequestBody RefundOrderDTO refundOrderDTO) {
        String code = orderService.refund(refundOrderDTO);
        if ("10000".equals(code)) {
            log.info("退款成功 订单id {}", refundOrderDTO.getOrderId());
            return Response.success(code);
        }
        return Response.error();
    }

    @GetMapping("/order/remind/{orderId}")
    public Response<String> remind(@PathVariable("orderId") String orderId) {
        orderService.remind(orderId);
        log.info("提醒发货成功 订单id {}", orderId);
        return Response.success();
    }
}
