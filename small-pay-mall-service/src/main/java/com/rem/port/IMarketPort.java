package com.rem.port;

import top.javarem.api.dto.LockMarketPayOrderRequestDTO;
import top.javarem.api.dto.LockMarketPayOrderResponseDTO;
import top.javarem.api.response.Response;

/**
 * @Author: rem
 * @Date: 2025/03/24/10:12
 * @Description:
 */
public interface IMarketPort {

    Response<LockMarketPayOrderResponseDTO> lockMarketPayOrder(LockMarketPayOrderRequestDTO requestDTO);

}
