package com.rem.infrastructure.adapter.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rem.api.dto.ItemShowDTO;
import com.rem.domain.order.adapter.repository.IItemRepository;
import com.rem.domain.order.model.entity.ItemEntity;
import com.rem.infrastructure.adapter.repository.mapper.ItemMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ItemRepository extends ServiceImpl<ItemMapper, ItemEntity> implements IItemRepository{
    @Override
    public List<ItemShowDTO> showItem() {
        List<ItemEntity> itemList = lambdaQuery().eq(ItemEntity::getItemStatus, 1).orderByDesc(ItemEntity::getAmount).list();
        List<ItemShowDTO> itemVOList = itemList.
                stream().
                map(item -> {
                    ItemShowDTO itemVO = new ItemShowDTO();
                    BeanUtils.copyProperties(item, itemVO);
                    return itemVO;
                }).
                collect(Collectors.toList());
        return itemVOList;
    }

}
