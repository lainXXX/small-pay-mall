package com.rem.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rem.dto.CartDTO;
import com.rem.dto.PayOrderRes;
import com.rem.dto.RefundOrderDTO;
import com.rem.po.PayOrder;
import com.rem.vo.PayOrderVO;
import top.javarem.api.dto.LockMarketPayOrderResponseDTO;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author aaa
 * @description 针对表【pay_order】的数据库操作Service
 * @createDate 2024-10-29 00:45:46
 */
public interface IPayOrderService extends IService<PayOrder> {

    PayOrderRes createOrder(CartDTO cartDTO) throws AlipayApiException;

    String payNotify(HttpServletRequest request) throws AlipayApiException;

    AlipayTradePagePayResponse doPay(String orderId, BigDecimal totalAmount, LockMarketPayOrderResponseDTO responseDTO, String itemName) throws AlipayApiException;

    boolean changeOrderStatus(String orderId, String code);

    List<PayOrderVO> searchPayOrder(String userId);

    String refund(RefundOrderDTO refundOrderDTO);

    void remind(String orderId);
}
