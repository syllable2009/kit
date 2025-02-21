package com.jxp.component.ai.strategy.impl;

import com.jxp.component.ai.strategy.DiscountStrategy;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-21 16:55
 */
public class AdminUserDiscountStrategy implements DiscountStrategy {
    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public double applyDiscount(double amount) {
        // 管理员有10%的优惠
        return amount * 0.9;
    }
}
