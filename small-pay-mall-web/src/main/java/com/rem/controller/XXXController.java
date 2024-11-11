package com.rem.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping()
@CrossOrigin("*")
@Slf4j
public class XXXController {


    @GetMapping("/hi")
    public String hi() {
        return "hi";
    }
}
