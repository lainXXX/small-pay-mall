package com.rem.infrastructure.adapter.repository;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.rem.api.dto.CartDTO;
import com.rem.api.dto.PayOrderShowDTO;
import com.rem.app.config.AlipayConfig;
import com.rem.domain.order.adapter.repository.IOrderRepository;
import com.rem.domain.order.event.PaySuccessEvent;
import com.rem.domain.order.event.RefundSuccessEvent;
import com.rem.domain.order.model.entity.OrderEntity;
import com.rem.domain.order.model.entity.PayOrderEntity;
import com.rem.domain.order.model.vo.OrderStatusVO;
import com.rem.infrastructure.adapter.port.PayPort;
import com.rem.infrastructure.adapter.repository.mapper.PayOrderMapper;
import com.rem.types.sdk.weixin.message.SubscriberMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class OrderRepository extends ServiceImpl<PayOrderMapper, OrderEntity> implements IOrderRepository {

    @Value("${wx.pay_template_id}")
    private String pay_template_id;

    @Value("${wx.refund_template_id}")
    private String refund_template_id;

    @Autowired
    private AlipayConfig alipayConfig;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private PaySuccessEvent paySuccessEvent;

    @Autowired
    private PayPort payPort;

    @Override
    public PayOrderEntity createOrder(CartDTO cartDTO) throws AlipayApiException {
        if (cartDTO == null) {
            log.info("购物车为空");
            return null;
        }
        OrderEntity unpaidOrder = lambdaQuery().eq(OrderEntity::getUserId, cartDTO.getUserId()).eq(OrderEntity::getItemId, cartDTO.getItemId()).apply("status = 'CREATE' or 'PAY_WAIT'").one();
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(cartDTO.getTotalAmount()));
/*  废案
      if (unpaidOrder != null && Constants.OrderStatusEnum.PAY_WAIT.getCode().equals(unpaidOrder.getStatus())) {
            log.info("存在未支付订单, 订单支付id{}", unpaidOrder.getItemId());
            return buildPayOrderRes(unpaidOrder);
        } else if ((unpaidOrder != null && Constants.OrderStatusEnum.CREATE.getCode().equals(unpaidOrder.getStatus()))) {
            log.info("存在掉单订单, 订单id{}", unpaidOrder.getUserId());
            return buildPayOrderRes(unpaidOrder);
        }*/
//        利用时间戳和随机数生成一个16位的订单号
        String orderId = System.currentTimeMillis() + RandomStringUtils.randomNumeric(3);
        OrderEntity order = OrderEntity.builder().
                userId(cartDTO.getUserId()).
                orderId(orderId).
                itemId(cartDTO.getItemId()).
                itemName(cartDTO.getItemName()).
                itemImage(cartDTO.getItemImage()).
                totalAmount(amount).
                createTime(LocalDateTime.now()).
                updateTime(LocalDateTime.now()).
                status(OrderStatusVO.CREATE.getCode()).
                build();
        this.save(order);
//        在创建订单 也就是save()后 执行doPay()方法可能失败 就会存在掉单 后续可以通过轮询支付状态为CREATE的订单重新发起支付
        AlipayTradePagePayResponse response = payPort.doPay(orderId, amount, cartDTO.getItemName());
//        获取支付宝表单页面url
        String payUrl = response.getBody();
        this.lambdaUpdate().set(OrderEntity::getOrderTime, LocalDateTime.now()).set(OrderEntity::getPayUrl, payUrl).set(OrderEntity::getUpdateTime, LocalDateTime.now()).set(OrderEntity::getStatus, OrderStatusVO.PAY_WAIT.getCode()).eq(OrderEntity::getOrderId, orderId).update();
        return PayOrderEntity.builder().userId(order.getUserId()).orderId(orderId).payUrl(payUrl).build();
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
        this.lambdaUpdate().set(OrderEntity::getPayTime, LocalDateTime.now()).set(OrderEntity::getStatus, OrderStatusVO.PAY_SUCCESS.getCode()).set(OrderEntity::getUpdateTime, LocalDateTime.now()).eq(OrderEntity::getOrderId, tradeNo).update();

        PaySuccessEvent.PaySuccessMessage message = paySuccessEvent.buildEventMessage(PaySuccessEvent.PaySuccessMessage.builder().orderId(tradeNo).messageTemplate(pay_template_id).build()).getData();
        redisTemplate.convertAndSend("pay_success", new Gson().toJson(message)); // 发布消息到 "myChannel"
        return "success";
    }


    @Override
    public boolean changeOrderStatus(String orderId, String status) {
        return this.lambdaUpdate().set(OrderEntity::getStatus, status).set(OrderEntity::getUpdateTime, LocalDateTime.now()).eq(OrderEntity::getOrderId, orderId).update();
    }

    @Override
    public List<PayOrderShowDTO> searchPayOrder(String userId) {
        List<OrderEntity> list = this.lambdaQuery()
                .eq(OrderEntity::getUserId, userId)
                .orderByDesc(OrderEntity::getCreateTime)
                .list();
        return list.
                stream().
                map(order -> {
                    PayOrderShowDTO payOrderVO = new PayOrderShowDTO();
                    BeanUtils.copyProperties(order, payOrderVO);
                    return payOrderVO;
                }).
                collect(Collectors.toList());
    }

    @Override
    public void remind(String orderId) {
        this.lambdaUpdate()
                .set(OrderEntity::getUpdateTime, LocalDateTime.now())
                .set(OrderEntity::getStatus, OrderStatusVO.DEAL_DONE.getCode())
                .eq(OrderEntity::getOrderId, orderId)
                .update();
    }

    @Override
    public void changeOrderRefundSuccess(SubscriberMessage message) {
        this.lambdaUpdate()
                .set(OrderEntity::getUpdateTime, LocalDateTime.now())
                .set(OrderEntity::getStatus, OrderStatusVO.REFUND.getCode())
                .set(OrderEntity::getRefundTime, LocalDateTime.now())
                .set(OrderEntity::getRefundReason, message.getReason())
                .eq(OrderEntity::getOrderId, message.getOrderId())
                .update();
    }

    @Override
    public List<OrderEntity> queryTimeoutCloseOrderList() {
        return this.lambdaQuery()
                .select(OrderEntity::getOrderId)
                .eq(OrderEntity::getStatus, OrderStatusVO.PAY_WAIT.getCode())
                .apply("NOW() > create_time + INTERVAL 30 MINUTE")
                .list();
    }

    @Override
    public List<OrderEntity> queryNoPayNotifyOrder() {
        return this.lambdaQuery()
                .eq(OrderEntity::getStatus, OrderStatusVO.CREATE.getCode())
                .apply("NOW() > create_time + INTERVAL 10 MINUTE")
                .list();
    }

    @Override
    public String getPayUrl(String orderId) {
        return this.lambdaQuery()
                .eq(OrderEntity::getOrderId, orderId)
                .one()
                .getPayUrl();

    }
}
