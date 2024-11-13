package com.rem.domain.order.adapter.port;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.rem.api.dto.RefundOrderDTO;

import java.io.IOException;
import java.math.BigDecimal;

public interface IPayPort {

    void handleRefundSuccess(String msg, String accessToken) throws IOException;

    void handlePaySuccess(String orderId, String accessToken) throws IOException;

    AlipayTradePagePayResponse doPay(String orderId, BigDecimal totalAmount, String itemName) throws AlipayApiException;

    String refund(RefundOrderDTO refundOrderDTO);
}
