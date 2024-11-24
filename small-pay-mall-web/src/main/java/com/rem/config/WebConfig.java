package com.rem.config;

import com.rem.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 替换为你的具体允许的域名
        config.addAllowedOrigin("https://www.javarem.top"); // 确保使用了正确的协议和格式
        config.setAllowCredentials(true); // 当设置为true时，allowedOrigins不能为*
        config.addAllowedMethod("*"); // 允许所有HTTP方法
        config.addAllowedHeader("*"); // 允许所有HTTP头部
        config.addExposedHeader("token"); // 暴露特定的头部给外部调用者

        // 如果您需要限制特定的域名，请替换下面的*为具体域名
//         config.addAllowedOrigin("http://localhost:19090");

        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config); // 应用CORS配置到所有路径
        return new CorsFilter(configSource);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/pay/order/**");
    }
}
