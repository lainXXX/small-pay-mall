package com.rem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rem.entity.Item;
import com.rem.vo.ItemVO;

import java.util.List;

/**
* @author aaa
* @description 针对表【item】的数据库操作Service
* @createDate 2024-11-07 14:59:33
*/
public interface ItemService extends IService<Item> {

    List<ItemVO> showItem();

}
