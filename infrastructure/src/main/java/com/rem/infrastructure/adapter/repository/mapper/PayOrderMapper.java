package com.rem.infrastructure.adapter.repository.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rem.domain.order.model.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* @author aaa
* @description 针对表【pay_order】的数据库操作Mapper
* @createDate 2024-10-29 00:45:46
* @Entity com.rem.entity.PayOrder
*/
@Mapper
public interface PayOrderMapper extends BaseMapper<OrderEntity> {

}
