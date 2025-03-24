package com.rem.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rem.po.PayOrder;
import org.apache.ibatis.annotations.Mapper;

/**
* @author aaa
* @description 针对表【pay_order】的数据库操作Mapper
* @createDate 2024-10-29 00:45:46
* @Entity com.rem.po.PayOrder
*/
@Mapper
public interface PayOrderMapper extends BaseMapper<PayOrder> {

}
