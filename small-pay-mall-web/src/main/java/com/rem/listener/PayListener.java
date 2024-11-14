package com.rem.listener;

import com.google.gson.Gson;
import com.rem.constants.Constants;
import com.rem.entity.Item;
import com.rem.entity.PayOrder;
import com.rem.message.SubscriberMessage;
import com.rem.service.IPayOrderService;
import com.rem.service.IWXApiService;
import com.rem.service.ItemService;
import com.rem.vo.WeixinTemplateMessageVO;
import com.rem.weixin.properties.WXProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Call;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@Component
public class PayListener implements MessageListener {

    @Value("${wx.pay_template_id}")
    private String pay_template_id;

    @Value("${wx.refund_template_id}")
    private String refund_template_id;

    @Autowired
    private IWXApiService wxService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private WXProperties wxProperties;

    @Autowired
    private IPayOrderService payOrderService;

    @Autowired
    private ItemService itemService;

    @SneakyThrows
    @Override
    public void onMessage(Message message, byte[] pattern) {

        String channel = new String(message.getChannel());
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        if (StringUtils.isBlank(msg)) {
            return;
        }
        String accessToken = redisTemplate.opsForValue().get(wxProperties.getAppID());
        if (accessToken == null) {
            return;
        }
        // 根据不同的频道来决定不同的处理逻辑
        if ("pay_success".equals(channel)) {
            handlePaySuccess(msg, accessToken);
        } else if ("refund_success".equals(channel)) {
            handleRefundSuccess(msg, accessToken);
        }
        //        可以进行充会员提示 返利提示 提示充值 发货提示 减少库存
    }

    @Transactional
    public void handleRefundSuccess(String msg, String accessToken) throws IOException {
        SubscriberMessage message = new Gson().fromJson(msg, SubscriberMessage.class);
        LocalDateTime refundTime = LocalDateTime.now();
        payOrderService.lambdaUpdate()
                .set(PayOrder::getUpdateTime, LocalDateTime.now())
                .set(PayOrder::getStatus, Constants.OrderStatusEnum.REFUND.getCode())
                .set(PayOrder::getRefundTime, refundTime)
                .set(PayOrder::getRefundReason, message.getReason())
                .eq(PayOrder::getOrderId, message.getOrderId())
                .update();

//                 TODO 退货操作
        itemService.lambdaUpdate()
                .setSql("item_quantity = item_quantity - 1")
                .eq(Item::getItemId, message.getOrderId())
                .update();
        WeixinTemplateMessageVO refundTemplate = WeixinTemplateMessageVO.createRefundTemplate(
                message.getUserId(),
                refund_template_id,
                message.getItemName(),
                message.getAmount().toString(),
                refundTime.toString());
        Call<Void> call = wxService.sendMessage(accessToken, refundTemplate);
        call.execute();
        log.info("退货成功 订单id : {}", message.getOrderId());
    }

    @Transactional
    public void handlePaySuccess(String orderId, String accessToken) throws IOException {

        PayOrder order = payOrderService.lambdaQuery()
                .eq(PayOrder::getOrderId, orderId)
                .one();
        if (order == null) {
            log.error("支付订单为空");
            return;
        }
        itemService.lambdaUpdate()
                .setSql("item_quantity = item_quantity - 1")
                .eq(Item::getItemId, order.getItemId())
                .update();
        WeixinTemplateMessageVO wxTemplateDTO = WeixinTemplateMessageVO.createPayTemplate(
                order.getUserId(),
                pay_template_id,
                order.getItemName(),
                order.getTotalAmount().toString(),
                order.getPayTime().toString()
        );
        Call<Void> call = wxService.sendMessage(accessToken, wxTemplateDTO);
        call.execute();
        log.info("支付成功 订单id : {}", orderId);
    }


}
