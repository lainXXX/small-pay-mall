package com.rem.trigger.http;

import com.rem.api.dto.ItemShowDTO;
import com.rem.api.response.Response;
import com.rem.domain.order.adapter.repository.IItemRepository;
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
    private IItemRepository iItemRepository;

    @GetMapping("/show")
    public Response<List<ItemShowDTO>> showItem() {
        List<ItemShowDTO> itemVOList = iItemRepository.showItem();
        return Response.success(itemVOList);
    }

}
