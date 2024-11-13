package com.rem.app.config;

import com.rem.infrastructure.gateway.IWXApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Slf4j
@Configuration
public class Retrofit2Config {

    private static final String BASE_URL = "https://api.weixin.qq.com/";

    @Bean
    public Retrofit retrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create()).build();//addConverterFactory 用于指定 JSON 转换器的一个方法
    }

    @Bean
    public IWXApiService WXServiceConfig(Retrofit retrofit) {
        return retrofit.create(IWXApiService.class);
    }

}
