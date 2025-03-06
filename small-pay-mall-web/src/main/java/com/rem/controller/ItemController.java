package com.rem.controller;

import com.rem.response.Response;
import com.rem.service.ItemService;
import com.rem.vo.ItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/item")
@CrossOrigin("*")
@Slf4j
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/show")
    public Response<List<ItemVO>> showItem() {
        List<ItemVO> itemVOList = itemService.showItem();
        return Response.success(itemVOList);
    }

    @GetMapping("/show/special")
    public Response<List<ItemVO>> showSpecialItem() {
        List<ItemVO> itemVOList = itemService.showSpecialItem();
        return Response.success(itemVOList);
    }

}
