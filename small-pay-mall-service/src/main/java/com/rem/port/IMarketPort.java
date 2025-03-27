package com.rem.port;

import com.rem.entity.MarketDiscountEntity;
import top.javarem.api.dto.LockMarketPayOrderRequestDTO;
import top.javarem.api.dto.SettleMarketPayOrderRequestDTO;

/**
 * @Author: rem
 * @Date: 2025/03/24/10:12
 * @Description:
 */
public interface IMarketPort {

    MarketDiscountEntity lockMarketPayOrder(LockMarketPayOrderRequestDTO requestDTO);

    void settlePayOrder(SettleMarketPayOrderRequestDTO requestDTO);

}
