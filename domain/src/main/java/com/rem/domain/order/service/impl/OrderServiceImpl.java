package com.rem.domain.order.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.rem.api.dto.CartDTO;
import com.rem.api.dto.PayOrderShowDTO;
import com.rem.api.dto.RefundOrderDTO;
import com.rem.domain.order.adapter.port.IPayPort;
import com.rem.domain.order.adapter.repository.IOrderRepository;
import com.rem.domain.order.model.entity.OrderEntity;
import com.rem.domain.order.model.entity.PayOrderEntity;
import com.rem.domain.order.model.vo.OrderStatusVO;
import com.rem.domain.order.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * @author aaa
 * @description 针对表【pay_order】的数据库操作Service实现
 * @createDate 2024-10-29 00:45:46
 */
@Service
@Slf4j
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private IPayPort payPort;

    /**
     * 创建订单信息
     *
     * @param cartDTO 传入的购物信息
     * @return
     * @throws AlipayApiException
     */


    @Override
    public PayOrderEntity createOrder(CartDTO cartDTO) throws AlipayApiException {
        return orderRepository.createOrder(cartDTO);
    }

    /**
     * 支付成功后的通知及后续处理
     *
     * @param request http请求
     * @return
     * @throws AlipayApiException
     */
    @Override
    public String payNotify(HttpServletRequest request) throws AlipayApiException {
        return orderRepository.payNotify(request);
    }

    /**
     * 创建支付请求
     *
     * @param orderId
     * @param totalAmount
     * @param itemName
     * @return
     * @throws AlipayApiException
     */
    public AlipayTradePagePayResponse doPay(String orderId, BigDecimal totalAmount, String itemName) throws AlipayApiException {
        return payPort.doPay(orderId, totalAmount, itemName);
    }

    /**
     * 订单状态修改
     *
     * @param orderId
     * @param status
     * @return
     */
    @Override
    public boolean changeOrderStatus(String orderId, String status) {
        return orderRepository.changeOrderStatus(orderId, status);
    }

    /**
     * 根据userId返回用户订单集合
     *
     * @param userId
     * @return
     */
    @Override
    public List<PayOrderShowDTO> searchPayOrder(String userId) {
        return orderRepository.searchPayOrder(userId);
    }

    @Override
    public String refund(RefundOrderDTO refundOrderDTO) {
        return payPort.refund(refundOrderDTO);
    }

    //    提醒发货本应该不这样设计 但是本项目不涉及商家端 故简单实现 可以使用webSocket等方式实现
    @Override
    public void remind(String orderId) {
        orderRepository.remind(orderId);
    }

    /**
     * 返回未支付订单部分信息
     *
     * @param unpaidOrder 未支付订单
     * @return
     */
    private PayOrderEntity buildPayOrderRes(OrderEntity unpaidOrder) {
        return PayOrderEntity.builder().orderId(unpaidOrder.getOrderId()).userId(unpaidOrder.getUserId()).payUrl(unpaidOrder.getPayUrl()).build();
    }

    @Override
    public void handlePaySuccess(String msg, String accessToken) throws IOException {
        payPort.handlePaySuccess(msg, accessToken);
    }

    @Override
    public void handleRefundSuccess(String msg, String accessToken) throws IOException {
        payPort.handleRefundSuccess(msg, accessToken);
    }

    @Override
    public List<OrderEntity> queryTimeoutCloseOrderList() {
        return orderRepository.queryTimeoutCloseOrderList();
    }

    @Override
    public List<OrderEntity> queryNoPayNotifyOrder() {
        return orderRepository.queryNoPayNotifyOrder();
    }

    @Override
    public String getPayUrl(String orderId) {
        return orderRepository.getPayUrl(orderId);
    }
}
