package com.rem.port;

import com.alibaba.fastjson.JSON;
import com.rem.entity.MarketDiscountEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import top.javarem.api.IMarketTradeService;
import top.javarem.api.dto.LockMarketPayOrderRequestDTO;
import top.javarem.api.dto.LockMarketPayOrderResponseDTO;
import top.javarem.api.dto.SettleMarketPayOrderRequestDTO;
import top.javarem.api.dto.SettleMarketPayOrderResponseDTO;
import top.javarem.api.response.Response;

/**
 * @Author: rem
 * @Date: 2025/03/24/10:23
 * @Description:
 */
@Service
@Slf4j
public class MarketPort implements IMarketPort {

    @DubboReference(interfaceClass = IMarketTradeService.class, version = "1.0")
    private IMarketTradeService marketTradeService;

    @Override
    public MarketDiscountEntity lockMarketPayOrder(LockMarketPayOrderRequestDTO requestDTO) {

        Response<LockMarketPayOrderResponseDTO> response = marketTradeService.lockPayOrder(requestDTO);
        log.info("营销结算{} requestDTO:{} responseDTO:{}", requestDTO.getUserId(), JSON.toJSONString(requestDTO), JSON.toJSONString(response));
        if (response == null) return null;

        if ("0000".equals(response.getCode())) {
            throw new RuntimeException("营销锁单失败");
        }
        return MarketDiscountEntity.builder()
                .originalPrice(response.getData().getOriginalPrice())
                .discountPrice(response.getData().getDiscountPrice())
                .payPrice(response.getData().getPayPrice())
                .build();


    }

    @Override
    public void settlePayOrder(SettleMarketPayOrderRequestDTO requestDTO) {

        try {
            Response<SettleMarketPayOrderResponseDTO> response = marketTradeService.settlePayOrder(requestDTO);
            if (response == null) return;

            if ("0000".equals(response.getCode())) {
                throw new RuntimeException(response.getCode());
            }
        } catch (Exception e) {
            log.error("营销结算失败{}", requestDTO.getUserId(), e);
        }

    }
}
