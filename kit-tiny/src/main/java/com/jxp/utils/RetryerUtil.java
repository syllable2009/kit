package com.jxp.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.StopStrategy;
import com.github.rholder.retry.WaitStrategies;
import com.github.rholder.retry.WaitStrategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 利用Guava Retryer实现的自动重试机制
 * @author jiaxiaopeng
 * Created on 2025-04-29 10:09
 */
@Slf4j
@SuppressWarnings("checkstyle:VisibilityModifier")
public class RetryerUtil {


    // 等待策略（默认固定间隔）
    public static WaitStrategy waitStrategy = WaitStrategies.fixedWait(500, TimeUnit.MILLISECONDS);

    // 停止策略（默认3次）
    public static StopStrategy stopStrategy = StopStrategies.stopAfterAttempt(3);

    /**
     * 通用重试执行器
     * @param callable 业务逻辑
     * @param retryPolicy 重试策略配置
     */
    public static <T> T execute(Callable<T> callable, RetryPolicy retryPolicy) throws Exception {
        final RetryerBuilder<T> retryerBuilder = RetryerBuilder.<T>newBuilder()
                .withRetryListener(new RetryLogListener());

        if (null == retryPolicy.getWaitStrategy()) {
            retryerBuilder.withWaitStrategy(RetryerUtil.waitStrategy);
        } else {
            retryerBuilder.withWaitStrategy(retryPolicy.getWaitStrategy());
        }

        if (null == retryPolicy.getStopStrategy()) {
            retryerBuilder.withStopStrategy(RetryerUtil.stopStrategy);
        } else {
            retryerBuilder.withStopStrategy(retryPolicy.getStopStrategy());
        }

        if (null != retryPolicy.getRetryIfResult()) {
            retryerBuilder.retryIfResult(retryPolicy.getRetryIfResult());
        }
        if (null != retryPolicy.getRetryIfResult()) {
            retryerBuilder.retryIfException(retryPolicy.getRetryIfResult());
        }
        T call = null;
        try {
            call = retryerBuilder.build().call(callable);
        } catch (Exception e) {
            // do nothing
        }
        return call;
    }

    public static void main(String[] args) throws Exception {
        final Callable<String> p1 = () -> {
            return null;
        };
        final String execute = RetryerUtil.execute(p1, RetryPolicy.builder()
                        .retryIfException(e -> e instanceof Exception)
                .retryIfResult(r -> null == r)
                .build());
        log.info("result:{}", execute);
    }
}
