package com.rem.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rem.entity.Item;
import com.rem.mapper.ItemMapper;
import com.rem.service.ItemService;
import com.rem.vo.ItemVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author aaa
 * @description 针对表【item】的数据库操作Service实现
 * @createDate 2024-11-07 14:59:33
 */
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {

    @Override
    public List<ItemVO> showItem() {
        List<Item> itemList = lambdaQuery().eq(Item::getItemStatus, 1).orderByDesc(Item::getAmount).list();
        List<ItemVO> itemVOList = itemList.
                stream().
                map(item -> {
                    ItemVO itemVO = new ItemVO();
                    BeanUtils.copyProperties(item, itemVO);
                    return itemVO;
                }).
                collect(Collectors.toList());
        return itemVOList;
    }
}
