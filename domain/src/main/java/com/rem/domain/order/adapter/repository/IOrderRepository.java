package com.rem.domain.order.adapter.repository;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rem.api.dto.CartDTO;
import com.rem.api.dto.PayOrderShowDTO;
import com.rem.api.dto.RefundOrderDTO;
import com.rem.domain.order.model.entity.OrderEntity;
import com.rem.domain.order.model.entity.PayOrderEntity;
import com.rem.types.sdk.weixin.message.SubscriberMessage;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

public interface IOrderRepository extends IService<OrderEntity> {

    PayOrderEntity createOrder(CartDTO cartDTO) throws AlipayApiException;

    String payNotify(HttpServletRequest request) throws AlipayApiException;


    boolean changeOrderStatus(String orderId, String status);

    List<PayOrderShowDTO> searchPayOrder(String userId);


    void remind(String orderId);

    void changeOrderRefundSuccess(SubscriberMessage message);

    List<OrderEntity> queryTimeoutCloseOrderList();

    List<OrderEntity> queryNoPayNotifyOrder();

    String getPayUrl(String orderId);
}
