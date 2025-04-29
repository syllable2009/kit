package com.jxp.utils;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.RetryListener;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-04-29 11:05
 */
@Slf4j
public class RetryLogListener implements RetryListener {

    @Override
    public <T> void onRetry(Attempt<T> attempt) {
        long retryCount = attempt.getAttemptNumber();
        if (retryCount > 1) {
            if (attempt.hasException()) {
                log.warn("[Retry] 第{}次重试失败，异常类型: {}",
                        retryCount,
                        attempt.getExceptionCause().getClass().getName());
            } else {
                log.info("[Retry] 第{}次重试", retryCount);
            }
        }
    }
}
