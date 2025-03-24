package com.rem;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.javarem.api.DCCService;
import top.javarem.api.response.Response;

/**
 * @Author: rem
 * @Date: 2025/03/23/20:31
 * @Description:
 */
@SpringBootTest
@Slf4j
public class RPCTest {

    @DubboReference(interfaceClass = DCCService.class, version = "1.0")
    private DCCService dccService;

    @Test
    public void test() {

        Response<Boolean> degradeSwitch = dccService.updateConfig("degradeSwitch", "1");
        log.info(degradeSwitch.getData().toString());

    }

}
