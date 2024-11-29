package com.jxp.web;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

/**
 * @author jiaxiaopeng
 * Created on 2024-11-29 10:14
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private IdentifyInterceptor identifyInterceptor;
    @Resource
    private AuthPermissionInterceptor authPermissionInterceptor;

    private static final List<String> EXCLUDE_PATH_PATTERNS = Arrays.asList(
            "/doc.html",
            "/swagger-ui.html",
            "/webjars/**",
            "/swagger-resources",
            "/favicon.ico",
            "/error"
    );

    // 跨域设置
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有请求路径
                .allowedOrigins("http://localhost:8080", "http://localhost") // 允许的来源，有端口一定要额外配
                .allowedMethods("*") // 允许的请求方法"GET", "POST", "PUT", "DELETE"
                .allowedHeaders("*") // 允许的请求头
                .allowCredentials(true)
                .exposedHeaders("*"); // 是否允许携带凭证
    }

    // 多语言设置
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(Locale.CHINA);  //默认地区
        cookieLocaleResolver.setCookieName("lang");   //cookie中取的地区的key
        return cookieLocaleResolver;
    }


    // 请求日志打印
//    @Bean
//    public CommonsRequestLoggingFilter logFilter() {
//        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
//        filter.setIncludeQueryString(true);
//        filter.setIncludePayload(true);
//        filter.setMaxPayloadLength(2000);
//        filter.setIncludeHeaders(false);
//        filter.setIncludeClientInfo(false);
//        filter.setAfterMessagePrefix("REQUEST DATA-");
//        return filter;
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(identifyInterceptor)
                .order(FilterOrder.TWO)
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_PATH_PATTERNS);

        registry.addInterceptor(authPermissionInterceptor)
                .order(FilterOrder.FOUR)
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_PATH_PATTERNS);
    }

    // 注册filter
//    @Bean
//    public FilterRegistrationBean<IdentifyFilter> loggingFilter() {
//        FilterRegistrationBean<IdentifyFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(new IdentifyFilter());
//        List<String> urlPatterns = Arrays.asList("/*"); // 过滤所有请求
//        urlPatterns.removeIf(url -> EXCLUDE_PATH_PATTERNS.contains(url));
//        registrationBean.setUrlPatterns(urlPatterns); // 过滤所有请求
//        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1); // 设置优先级
//        return registrationBean;
//    }

}
