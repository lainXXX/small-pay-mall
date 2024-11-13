package com.rem.domain.order.adapter.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rem.api.dto.ItemShowDTO;
import com.rem.domain.order.model.entity.ItemEntity;

import java.util.List;

public interface IItemRepository extends IService<ItemEntity> {

    List<ItemShowDTO> showItem();

}
