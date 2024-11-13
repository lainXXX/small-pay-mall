package com.rem.infrastructure.adapter.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rem.domain.order.model.entity.ItemEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* @author aaa
* @description 针对表【item】的数据库操作Mapper
* @createDate 2024-11-07 14:59:33
* @Entity com.rem.entity.Item
*/
@Mapper
public interface ItemMapper extends BaseMapper<ItemEntity> {

}
