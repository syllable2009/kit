package com.jxp.component.ai.service.impl;

import org.springframework.stereotype.Service;

import com.jxp.component.ai.dto.Request;
import com.jxp.component.ai.dto.RequestFilterChain;
import com.jxp.component.ai.handler.impl.AuthenticationHandler;
import com.jxp.component.ai.handler.impl.LoggingHandler;
import com.jxp.component.ai.handler.impl.PermissionHandler;
import com.jxp.component.ai.service.AiControllerService;
import com.jxp.component.ai.strategy.DiscountStrategy;
import com.jxp.component.ai.strategy.impl.AdminUserDiscountStrategy;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-21 16:44
 */
@Slf4j
@Service
public class AiControllerServiceImpl implements AiControllerService {
    @Override
    public String chat(Request request) {
        // 创建处理器的链
        RequestFilterChain filterChain = new RequestFilterChain();
        // 根据配置创建处理器，注意顺序
        filterChain.addHandler(new AuthenticationHandler());
        filterChain.addHandler(new LoggingHandler());
        filterChain.addHandler(new PermissionHandler());

        // 处理请求
        System.out.println("Processing request 1:");
        filterChain.doFilter(request);
        System.out.println("\nProcessing request 2:");
        // 选择策略
        applyDiscount(request, new AdminUserDiscountStrategy());
        return "success";
    }

    private static void applyDiscount(Request request, DiscountStrategy discountStrategy) {
        double discountedAmount = discountStrategy.applyDiscount(request.getAmount());
        System.out.println("Discounted amount for user " + request.getUsername() + ": " + discountedAmount);
    }

    public static void main(String[] args) {
        final AiControllerServiceImpl aiControllerService = new AiControllerServiceImpl();
        aiControllerService.chat(Request.builder()
                .username("admin")
                .amount(100)
                .build());
    }
}
