package com.jxp.component.ai.strategy.impl;

import com.jxp.component.ai.strategy.DiscountStrategy;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-21 16:54
 */
public class RegularUserDiscountStrategy implements DiscountStrategy {
    @Override
    public double applyDiscount(double amount) {
        // 普通用户没有优惠
        return amount;
    }
}
