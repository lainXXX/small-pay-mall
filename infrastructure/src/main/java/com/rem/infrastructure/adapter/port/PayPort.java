package com.rem.infrastructure.adapter.port;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.google.gson.Gson;
import com.rem.api.dto.RefundOrderDTO;
import com.rem.app.config.AlipayConfig;
import com.rem.domain.order.adapter.port.IPayPort;
import com.rem.domain.order.event.RefundSuccessEvent;
import com.rem.domain.order.model.entity.ItemEntity;
import com.rem.domain.order.model.entity.OrderEntity;
import com.rem.domain.order.model.vo.OrderStatusVO;
import com.rem.infrastructure.adapter.repository.ItemRepository;
import com.rem.infrastructure.adapter.repository.OrderRepository;
import com.rem.infrastructure.gateway.IWXApiService;
import com.rem.types.sdk.weixin.message.SubscriberMessage;
import com.rem.types.sdk.weixin.message.WeixinTemplateMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Call;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
public class PayPort implements IPayPort {

    @Value("${wx.refund_template_id}")
    private String refund_template_id;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private IWXApiService wxApiService;

    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RefundSuccessEvent refundSuccessEvent;

    @Override
    @Transactional
    public void handleRefundSuccess(String msg, String accessToken) throws IOException {
        SubscriberMessage message = new Gson().fromJson(msg, SubscriberMessage.class);
        LocalDateTime refundTime = LocalDateTime.now();
        orderRepository.lambdaUpdate()
                .set(OrderEntity::getUpdateTime, LocalDateTime.now())
                .set(OrderEntity::getStatus, OrderStatusVO.REFUND.getCode())
                .set(OrderEntity::getRefundTime, refundTime)
                .set(OrderEntity::getRefundReason, message.getReason())
                .eq(OrderEntity::getOrderId, message.getOrderId())
                .update();

//                 TODO 退货操作
        itemRepository.lambdaUpdate()
                .setSql("item_quantity = item_quantity - 1")
                .eq(ItemEntity::getItemId, message.getOrderId())
                .update();
        WeixinTemplateMessage refundTemplate = WeixinTemplateMessage.createRefundTemplate(
                message.getUserId(),
                message.getMessageTemplate(),
                message.getItemName(),
                message.getAmount(),
                refundTime.toString());
        Call<Void> call = wxApiService.sendMessage(accessToken, refundTemplate);
        call.execute();
        log.info("退货成功 订单id : {}", message.getOrderId());
    }

    @Override
    @Transactional
    public void handlePaySuccess(String msg, String accessToken) throws IOException {
        SubscriberMessage message = new Gson().fromJson(msg, SubscriberMessage.class);
        String orderId = message.getOrderId();
        String messageTemplate = message.getMessageTemplate();
        OrderEntity order = orderRepository.lambdaQuery()
                .eq(OrderEntity::getOrderId, orderId)
                .one();
        if (order == null) {
            log.error("支付订单为空");
            return;
        }
        itemRepository.lambdaUpdate()
                .setSql("item_quantity = item_quantity - 1")
                .eq(ItemEntity::getItemId, order.getItemId())
                .update();
        WeixinTemplateMessage wxTemplateDTO = WeixinTemplateMessage.createPayTemplate(
                order.getUserId(),
                messageTemplate,
                order.getItemName(),
                order.getTotalAmount().toString(),
                order.getPayTime().toString()
        );
        Call<Void> call = wxApiService.sendMessage(accessToken, wxTemplateDTO);
        call.execute();
        log.info("支付成功 订单id : {}", orderId);
    }

    @Override
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
        return alipayClient.pageExecute(alipayRequest);
    }

    @Override
    public String refund(RefundOrderDTO refundOrderDTO) {
        if (refundOrderDTO == null) {
            return null;
        }
        String userId = refundOrderDTO.getUserId();
        String orderId = refundOrderDTO.getOrderId();
        String itemId = refundOrderDTO.getItemId();
        String itemName = refundOrderDTO.getItemName();
        String amount = refundOrderDTO.getAmount();
        String reason = refundOrderDTO.getReason();
        // 创建退款请求的参数
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo(orderId); // 商户订单号
//        model.setTradeNo("alipay-trade-id-12345"); // 支付宝交易号
        model.setRefundAmount(amount); // 退款金额（单位：元）
        model.setRefundReason(reason); // 退款原因
//        model.setOutRequestNo("refund-001"); // 退款请求号，部分退款时使用
        request.setBizModel(model);

        // 发起退款请求
        try {
//            获取退款操作后的响应对象 方便获取退款返回的信息
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            String code = response.getCode();
            if ("10000".equals(code)) {
                RefundSuccessEvent.RefundSuccessMessage eventMessage = refundSuccessEvent.buildEventMessage(
                        RefundSuccessEvent.RefundSuccessMessage.builder()
                                .userId(userId)
                                .itemId(itemId)
                                .amount(amount)
                                .reason(reason)
                                .itemName(itemName)
                                .orderId(orderId)
                                .messageTemplate(refund_template_id)
                                .build()).getData();
                String message = new Gson().toJson(eventMessage);
                redisTemplate.convertAndSend("refund_success", message);
            }
            return code;
        } catch (AlipayApiException e) {
            log.info("退款操作异常 {}", e.getMessage());
            return "";
        }
    }
}
