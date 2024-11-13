package com.rem.domain.order.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.rem.api.dto.CartDTO;
import com.rem.api.dto.PayOrderShowDTO;
import com.rem.api.dto.RefundOrderDTO;
import com.rem.domain.order.model.entity.OrderEntity;
import com.rem.domain.order.model.entity.PayOrderEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author aaa
 * @description 针对表【pay_order】的数据库操作Service
 * @createDate 2024-10-29 00:45:46
 */
public interface IOrderService {

    PayOrderEntity createOrder(CartDTO cartDTO) throws AlipayApiException;

    String payNotify(HttpServletRequest request) throws AlipayApiException;

    AlipayTradePagePayResponse doPay(String orderId, BigDecimal totalAmount, String itemName) throws AlipayApiException;

    boolean changeOrderStatus(String orderId, String status);

    List<PayOrderShowDTO> searchPayOrder(String userId);

    String refund(RefundOrderDTO refundOrderDTO);

    void remind(String orderId);

    void handlePaySuccess(String msg, String accessToken) throws IOException;

    void handleRefundSuccess(String msg, String accessToken) throws IOException;

    List<OrderEntity> queryTimeoutCloseOrderList();

    List<OrderEntity> queryNoPayNotifyOrder();

    String getPayUrl(String orderId);
}
