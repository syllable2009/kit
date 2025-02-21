package com.jxp.component.ai.strategy.impl;

import com.jxp.component.ai.strategy.DiscountStrategy;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-21 16:55
 */
public class VIPUserDiscountStrategy implements DiscountStrategy {
    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public double applyDiscount(double amount) {
        // VIP用户有5%的优惠
        return amount * 0.95;
    }
}
