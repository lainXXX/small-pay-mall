package com.rem.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rem.dto.CartDTO;
import com.rem.entity.PayOrder;
import com.rem.res.PayOrderRes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
* @author aaa
* @description 针对表【pay_order】的数据库操作Service
* @createDate 2024-10-29 00:45:46
*/
public interface IPayOrderService extends IService<PayOrder> {

    PayOrderRes createOrder(CartDTO cartDTO) throws AlipayApiException;

    String payNotify(HttpServletRequest request) throws AlipayApiException;

    AlipayTradePagePayResponse doPay(String orderId, BigDecimal totalAmount, String itemName) throws AlipayApiException;

    boolean changeOrderStatus(String orderId, String code);
}
