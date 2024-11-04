package com.rem.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rem.config.AlipayConfig;
import com.rem.constants.Constants;
import com.rem.dto.CartDTO;
import com.rem.entity.PayOrder;
import com.rem.mapper.PayOrderMapper;
import com.rem.res.PayOrderRes;
import com.rem.service.IPayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author aaa
 * @description 针对表【pay_order】的数据库操作Service实现
 * @createDate 2024-10-29 00:45:46
 */
@Service
@Slf4j
public class PayOrderServiceImpl extends ServiceImpl<PayOrderMapper, PayOrder> implements IPayOrderService {

    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public PayOrderRes createOrder(CartDTO cartDTO) throws AlipayApiException {
        if (cartDTO == null) {
            throw new AlipayApiException("购物车为空");
        }
        PayOrder unpaidOrder = lambdaQuery().eq(PayOrder::getUserId, cartDTO.getUserId())
                .eq(PayOrder::getItemId, cartDTO.getItemId())
                .one();
        if (unpaidOrder != null && Constants.OrderStatusEnum.PAY_WAIT.getCode().equals(unpaidOrder.getStatus())) {
            log.info("存在未支付订单, 订单支付id{}", unpaidOrder.getItemId());
            return buildPayOrderRes(unpaidOrder);
        } else if ((unpaidOrder != null && Constants.OrderStatusEnum.CREATE.getCode().equals(unpaidOrder.getStatus()))) {
            log.info("存在掉单订单, 订单id{}", unpaidOrder.getUserId());
            return buildPayOrderRes(unpaidOrder);
        }
        String orderId = System.currentTimeMillis() + RandomStringUtils.randomNumeric(3);
        PayOrder order = PayOrder.builder()
                .userId(cartDTO.getUserId())
                .orderId(orderId)
                .itemId(cartDTO.getItemId())
                .itemName(cartDTO.getItemName())
                .totalAmount(cartDTO.getTotalAmount())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .status(Constants.OrderStatusEnum.CREATE.getCode())
                .build();
        this.save(order);
//        在创建订单 也就是save()后 执行doPay()方法可能失败 就会存在掉单 后续可以通过轮询支付状态为CREATE的订单重新发起支付
        AlipayTradePagePayResponse response = doPay(orderId, cartDTO.getTotalAmount(), cartDTO.getItemName());
//        获取支付宝表单页面url
        String payUrl = response.getBody();
        this.lambdaUpdate()
                .set(PayOrder::getOrderTime, LocalDateTime.now())
                .set(PayOrder::getPayUrl, payUrl)
                .set(PayOrder::getUpdateTime, LocalDateTime.now())
                .set(PayOrder::getStatus, Constants.OrderStatusEnum.PAY_WAIT.getCode())
                .eq(PayOrder::getOrderId, orderId)
                .update();
        return PayOrderRes.builder()
                .userId(order.getUserId())
                .orderId(orderId)
                .payUrl(payUrl)
                .build();
    }

    @Override
    public String payNotify(HttpServletRequest request) throws AlipayApiException {
        if (!request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            return "false";
        }

        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            params.put(name, request.getParameter(name));
        }

        String tradeNo = params.get("out_trade_no");
        String gmtPayment = params.get("gmt_payment");
        String alipayTradeNo = params.get("trade_no");

        String sign = params.get("sign");
        String content = AlipaySignature.getSignCheckContentV1(params);
        boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, alipayConfig.alipay_public_key, "UTF-8"); // 验证签名
        // 支付宝验签
        if (!checkSignature) {
            return "false";
        }
//        修改状态
        this.lambdaUpdate()
                .set(PayOrder::getPayTime, LocalDateTime.now())
                .set(PayOrder::getStatus, Constants.OrderStatusEnum.PAY_SUCCESS.getCode())
                .set(PayOrder::getUpdateTime, LocalDateTime.now())
                .eq(PayOrder::getOrderId, tradeNo)
                .update();

        redisTemplate.convertAndSend("pay_success", tradeNo); // 发布消息到 "myChannel"
        return "success";
    }

    public AlipayTradePagePayResponse doPay(String orderId, BigDecimal totalAmount, String itemName) throws AlipayApiException {
//        创建一个支付请求 设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(alipayConfig.return_url);
        alipayRequest.setNotifyUrl(alipayConfig.notify_url);
//        创建支付表单页面
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        bizContent.put("total_amount", totalAmount.toString());
        bizContent.put("subject", itemName);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        alipayRequest.setBizContent(bizContent.toString());
        //调用支付宝沙箱支付页面
        AlipayTradePagePayResponse response = alipayClient.pageExecute(alipayRequest);
        return response;
    }

    @Override
    public boolean changeOrderStatus(String orderId, String status) {
            return this.lambdaUpdate()
                    .set(PayOrder::getStatus, status)
                    .set(PayOrder::getUpdateTime, LocalDateTime.now())
                    .eq(PayOrder::getOrderId, orderId)
                    .update();
    }

    private PayOrderRes buildPayOrderRes(PayOrder unpaidOrder) {
        return PayOrderRes.builder()
                .orderId(unpaidOrder.getOrderId())
                .userId(unpaidOrder.getUserId())
                .payUrl(unpaidOrder.getPayUrl())
                .build();
    }

}
