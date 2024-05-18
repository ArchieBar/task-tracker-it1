package ru.itone.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.itone.filter.HttpLogonCheck;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final HttpLogonCheck httpLogonCheck;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(httpLogonCheck)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/register", "/user/login");
    }
}
