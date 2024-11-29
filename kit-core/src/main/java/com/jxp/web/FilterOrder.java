package com.jxp.web;

import org.springframework.core.Ordered;

/**
 * @author jiaxiaopeng
 * Created on 2024-11-29 16:19
 */
public interface FilterOrder {
    int ONE = Ordered.HIGHEST_PRECEDENCE;
    int TWO = Ordered.HIGHEST_PRECEDENCE + 1;
    int THREE = Ordered.HIGHEST_PRECEDENCE + 2;
    int FOUR = Ordered.HIGHEST_PRECEDENCE + 3;
    int FIVE = Ordered.HIGHEST_PRECEDENCE + 4;
}
