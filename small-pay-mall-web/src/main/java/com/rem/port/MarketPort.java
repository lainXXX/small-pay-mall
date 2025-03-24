package com.rem.port;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import top.javarem.api.IMarketTradeService;
import top.javarem.api.dto.LockMarketPayOrderRequestDTO;
import top.javarem.api.dto.LockMarketPayOrderResponseDTO;
import top.javarem.api.response.Response;

/**
 * @Author: rem
 * @Date: 2025/03/24/10:23
 * @Description:
 */
@Service
public class MarketPort implements IMarketPort {

    @DubboReference(interfaceClass = IMarketTradeService.class, version = "1.0")
    private IMarketTradeService marketTradeService;

    @Override
    public Response<LockMarketPayOrderResponseDTO> lockMarketPayOrder(LockMarketPayOrderRequestDTO requestDTO) {
        return marketTradeService.lockPayOrder(requestDTO);
    }
}
