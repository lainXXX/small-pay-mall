package com.rem.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.rem.config.AlipayConfig;
import com.rem.constants.Constants;
import com.rem.dto.CartDTO;
import com.rem.dto.PayOrderRes;
import com.rem.dto.RefundOrderDTO;
import com.rem.enums.MarketTypeEnums;
import com.rem.mapper.PayOrderMapper;
import com.rem.message.SubscriberMessage;
import com.rem.po.Item;
import com.rem.po.PayOrder;
import com.rem.port.IMarketPort;
import com.rem.service.IPayOrderService;
import com.rem.service.ItemService;
import com.rem.vo.PayOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.javarem.api.dto.LockMarketPayOrderRequestDTO;
import top.javarem.api.dto.LockMarketPayOrderResponseDTO;
import top.javarem.api.response.Response;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Resource
    private ItemService itemService;

    @Resource
    private IMarketPort marketPort;

    /**
     * 创建订单信息
     *
     * @param cartDTO 传入的购物信息
     * @return
     * @throws AlipayApiException
     */
    @Override
    public PayOrderRes createOrder(CartDTO cartDTO) throws AlipayApiException {

//        查询商品是否合法
        Item item = itemService.lambdaQuery()
                .select(Item::getItemId, Item::getItemName, Item::getItemImage, Item::getAmount, Item::getSource, Item::getChannel)
                .eq(Item::getItemId, cartDTO.getItemId())
                .one();
        if (item == null) return null;
//        利用时间戳和随机数生成一个16位的订单号
        String orderId = System.currentTimeMillis() + RandomStringUtils.randomNumeric(3);
        PayOrder order = PayOrder.builder().
                userId(cartDTO.getUserId()).
                orderId(orderId).
                itemId(item.getItemId()).
                itemName(item.getItemName()).
                itemImage(item.getItemImage()).
                totalAmount(item.getAmount()).
                payAmount(item.getAmount()).
                createTime(LocalDateTime.now()).
                updateTime(LocalDateTime.now()).
                status(Constants.OrderStatusEnum.CREATE.getCode()).
                build();
        this.save(order);
        Response<LockMarketPayOrderResponseDTO> lockOrderResponse = null;
        if (MarketTypeEnums.GROUP_BUYING.getCode().equals(cartDTO.getMarketType())) {
            //        对接拼团系统锁单
            LockMarketPayOrderRequestDTO requestDTO = new LockMarketPayOrderRequestDTO();
            requestDTO.setUserId(order.getUserId());
            requestDTO.setOutTradeNo(order.getOrderId());
            requestDTO.setTeamId(cartDTO.getTeamId());
            requestDTO.setGoodsId(item.getItemId());
            requestDTO.setSource(item.getSource());
            requestDTO.setChannel(item.getChannel());
            requestDTO.setActivityId(cartDTO.getActivityId());
            requestDTO.setNotifyUrl("http");
            lockOrderResponse = marketPort.lockMarketPayOrder(requestDTO);
            if ("0001".equals(lockOrderResponse.getCode())) return null;
            log.info("对接拼团系统成功 response:{}", lockOrderResponse);
        }


//        在创建订单 也就是save()后 执行doPay()方法可能失败 就会存在掉单 后续可以通过轮询支付状态为CREATE的订单重新发起支付
        AlipayTradePagePayResponse response = doPay(orderId, cartDTO.getTotalAmount(), lockOrderResponse.getData(), cartDTO.getItemName());
//        获取支付宝表单页面url
        String payUrl = response.getBody();
        this.lambdaUpdate().set(PayOrder::getOrderTime, LocalDateTime.now()).set(PayOrder::getPayUrl, payUrl).set(PayOrder::getUpdateTime, LocalDateTime.now()).set(PayOrder::getStatus, Constants.OrderStatusEnum.PAY_WAIT.getCode()).eq(PayOrder::getOrderId, orderId).update();
        return PayOrderRes.builder().userId(order.getUserId()).orderId(orderId).payUrl(payUrl).build();
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
        this.lambdaUpdate().set(PayOrder::getPayTime, LocalDateTime.now()).set(PayOrder::getStatus, Constants.OrderStatusEnum.PAY_SUCCESS.getCode()).set(PayOrder::getUpdateTime, LocalDateTime.now()).eq(PayOrder::getOrderId, tradeNo).update();

        redisTemplate.convertAndSend("pay_success", tradeNo); // 发布消息到 "myChannel"
        return "success";
    }

    /**
     * 创建支付请求
     *
     * @param orderId
     * @param totalAmount
     * @param responseDTO
     * @param itemName
     * @return
     * @throws AlipayApiException
     */
    public AlipayTradePagePayResponse doPay(String orderId, BigDecimal totalAmount, LockMarketPayOrderResponseDTO responseDTO, String itemName) throws AlipayApiException {
//        创建一个支付请求 设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(alipayConfig.return_url);
        alipayRequest.setNotifyUrl(alipayConfig.notify_url);

        BigDecimal payAmount = responseDTO == null ? totalAmount : responseDTO.getPayPrice();

//        创建支付表单页面
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        bizContent.put("total_amount", payAmount.toString());
        bizContent.put("subject", itemName);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        alipayRequest.setBizContent(bizContent.toString());
        //调用支付宝沙箱支付页面
        AlipayTradePagePayResponse response = alipayClient.pageExecute(alipayRequest);

        //        校验response参数
        Integer marketType = responseDTO == null ? MarketTypeEnums.NO_MARKET.getCode() : MarketTypeEnums.GROUP_BUYING.getCode();

        BigDecimal discountAmount = MarketTypeEnums.GROUP_BUYING.getCode().equals(marketType) ? responseDTO.getDiscountPrice() : BigDecimal.ZERO;

        this.lambdaUpdate()
                .set(PayOrder::getDiscountAmount, discountAmount)
                .set(PayOrder::getPayAmount, payAmount)
                .set(PayOrder::getMarketType, marketType)
                .set(PayOrder::getUpdateTime, LocalDateTime.now())
                .set(PayOrder::getStatus, Constants.OrderStatusEnum.PAY_WAIT.getCode())
                .set(PayOrder::getPayUrl, response.getBody())
                .eq(PayOrder::getOrderId, orderId)
                .update();

        return response;
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
        return this.lambdaUpdate().set(PayOrder::getStatus, status).set(PayOrder::getUpdateTime, LocalDateTime.now()).eq(PayOrder::getOrderId, orderId).update();
    }

    /**
     * 根据userId返回用户订单集合
     *
     * @param userId
     * @return
     */
    @Override
    public List<PayOrderVO> searchPayOrder(String userId) {
        List<PayOrder> list = this.lambdaQuery()
                .eq(PayOrder::getUserId, userId)
                .orderByDesc(PayOrder::getCreateTime)
                .list();
        List<PayOrderVO> PayOrderVOList = list.
                stream().
                map(order -> {
                    PayOrderVO payOrderVO = new PayOrderVO();
                    BeanUtils.copyProperties(order, payOrderVO);
                    return payOrderVO;
                }).
                collect(Collectors.toList());
        return PayOrderVOList;
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
                SubscriberMessage subscriberMessage = SubscriberMessage.builder()
                        .userId(userId)
                        .itemId(itemId)
                        .amount(amount)
                        .reason(reason)
                        .itemName(itemName)
                        .orderId(orderId)
                        .build();
                String message = new Gson().toJson(subscriberMessage);
                redisTemplate.convertAndSend("refund_success", message);
            }
            return code;
        } catch (AlipayApiException e) {
            log.info("退款操作异常 {}", e.getMessage());
            return "";
        }
    }

    //    提醒发货本应该不这样设计 但是本项目不涉及商家端 故简单实现 可以使用webSocket等方式实现
    @Override
    public void remind(String orderId) {
        this.lambdaUpdate()
                .set(PayOrder::getUpdateTime, LocalDateTime.now())
                .set(PayOrder::getStatus, Constants.OrderStatusEnum.DEAL_DONE.getCode())
                .eq(PayOrder::getOrderId, orderId)
                .update();
    }

    /**
     * 返回未支付订单部分信息
     *
     * @param unpaidOrder 未支付订单
     * @return
     */
    private PayOrderRes buildPayOrderRes(PayOrder unpaidOrder) {
        return PayOrderRes.builder().orderId(unpaidOrder.getOrderId()).userId(unpaidOrder.getUserId()).payUrl(unpaidOrder.getPayUrl()).build();
    }

}
