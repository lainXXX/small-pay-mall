package com.rem.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: rem
 * @Date: 2025/03/24/16:17
 * @Description: 拼团组队完成回调dto
 */
@Data
public class NotifyRequestDTO {

    private String teamId;

    private List<String> outTradeNoList;

}
