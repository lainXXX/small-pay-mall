package com.rem.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.google.gson.Gson;
import com.rem.dto.*;
import com.rem.po.PayOrder;
import com.rem.response.Response;
import com.rem.service.IPayOrderService;
import com.rem.vo.PayOrderVO;
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
    private IPayOrderService payOrderService;

    @PostMapping("group_buying_notify")
    public String groupBuyingNotify(@RequestBody NotifyRequestDTO notifyRequestDTO) {

        log.info("拼团组队完成- 结算开始 {}", new Gson().toJson(notifyRequestDTO));

        try {
            // 营销结算
            payOrderService.changeOrderMarketSettlement(notifyRequestDTO.getOutTradeNoList());
            log.info("拼团组队完成- 结算完成 {}", new Gson().toJson(notifyRequestDTO));
            return "success";
        } catch (Exception e) {
            log.error("拼团回调，组队完成，结算失败 {}", JSON.toJSONString(notifyRequestDTO));
            return "error";
        }

    }

/*     TODO 为什么要使用@RequestBody String dto而不是直接CartDTO cartDTO呢 因为打成jar包会报错没有构造器
    我试了很多种改造类都没有成功 所以迫不得已使用String 以后有机会研究出来再改*/
    @PostMapping("/order/create")
    public Response<String> createOrder(@RequestBody CartDTO cartDTO) throws AlipayApiException {
        log.info("创建订单 {}", cartDTO);
        try {
            PayOrderRes orderRes = payOrderService.createOrder(cartDTO);
            if (orderRes == null) {
                log.error("商品不合法");
                return Response.error();
            }
            log.info("商品下单，根据商品ID创建支付单完成 userId:{} orderId:{}", orderRes.getUserId(), orderRes.getOrderId());
            return Response.success(orderRes.getPayUrl());
        } catch (Exception e) {
            log.error("订单创建失败 {}", JSON.toJSONString(cartDTO));
            return Response.error();
        }
    }

    @PostMapping("/order")
    public Response<String> pay(@RequestBody PayOrderDTO orderDTO) {
        String orderId = orderDTO.getOrderId();
        PayOrder one = payOrderService.lambdaQuery()
                .eq(PayOrder::getOrderId, orderId)
                .one();
        log.info("用户支付操作，订单id {} 用户id:{}", orderId, one.getUserId());
        return Response.success(one.getPayUrl());
    }

    @PostMapping("/notify")
    public String payNotify(HttpServletRequest request) throws AlipayApiException {
        log.info("支付宝回调 {}", request);
        String message = payOrderService.payNotify(request);
        return message;
    }

    @GetMapping("order/search")
    public Response<List<PayOrderVO>> searchPayOrder(@RequestHeader("token") String userId) {
        List<PayOrderVO> payOrderVOList = payOrderService.searchPayOrder(userId);
        log.info("查询用户订单 userId:{}", userId);
        return Response.success(payOrderVOList);
    }

    @PostMapping("/order/refund")
    public Response<String> refund(@RequestBody RefundOrderDTO refundOrderDTO) {
        String code = payOrderService.refund(refundOrderDTO);
        if ("10000".equals(code)) {
            log.info("退款成功 订单id {}", refundOrderDTO.getOrderId());
            return Response.success(code);
        }
        return Response.error();
    }

    @GetMapping("/order/remind/{orderId}")
    public Response<String> remind(@PathVariable("orderId") String orderId) {
        payOrderService.remind(orderId);
        log.info("提醒发货成功 订单id {}", orderId);
        return Response.success();
    }
}
