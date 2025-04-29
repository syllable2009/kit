package com.jxp.utils;

import java.util.concurrent.TimeUnit;

import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.StopStrategy;
import com.github.rholder.retry.WaitStrategies;
import com.github.rholder.retry.WaitStrategy;
import com.google.common.base.Predicate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-04-29 11:01
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuppressWarnings("checkstyle:VisibilityModifier")
public class RetryPolicy<T> {

    // 需要重试的异常类型
    private Predicate<Throwable> retryIfException;

    // 需要重试的返回结果
    private Predicate<T> retryIfResult;


    // 等待策略（默认固定间隔）
    public WaitStrategy waitStrategy = WaitStrategies.fixedWait(500, TimeUnit.MILLISECONDS);

    // 停止策略（默认3次）
    public StopStrategy stopStrategy = StopStrategies.stopAfterAttempt(3);

}
