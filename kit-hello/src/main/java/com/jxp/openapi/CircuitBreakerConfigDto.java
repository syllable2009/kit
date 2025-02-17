package com.jxp.openapi;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import lombok.Data;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-17 14:57
 */
@Data
public class CircuitBreakerConfigDto {
    /**
     * 故障阈值百分比，超过这个阈值就会打开断路器
     */
    private float failureRateThreshold = 50;
    /**
     * 断路器打开的持续时间，到达这个时间后，断路器进入half open状态
     */
    private long waitDurationInOpenStateSeconds = 60;
    /**
     * 断路器半开放状态时允许调用的数量
     */
    private int permittedNumberOfCallsInHalfOpenState = 2;
    /**
     * 断路器关闭时滑动窗口的大小
     * 用于统计调用失败率
     */
    private int slidingWindowSize = 100;
    /**
     * 统计失败率之前最少被调用的此时
     */
    private int minimumNumberOfCalls = 100;
    /**
     * 滑动窗口类型
     */
    private SlidingWindowType slidingWindowType = SlidingWindowType.COUNT_BASED;

    public CircuitBreakerConfigDto(
            @JsonProperty("failureRateThreshold") float failureRateThreshold,
            @JsonProperty("waitDurationInOpenStateSeconds") long waitDurationInOpenStateSeconds,
            @JsonProperty("permittedNumberOfCallsInHalfOpenState") int permittedNumberOfCallsInHalfOpenState,
            @JsonProperty("slidingWindowSize") int slidingWindowSize,
            @JsonProperty("minimumNumberOfCalls") int minimumNumberOfCalls,
            @JsonProperty("slidingWindowType") String slidingWindowType) {
        if (failureRateThreshold > 0) {
            this.failureRateThreshold = failureRateThreshold;
        }
        if (waitDurationInOpenStateSeconds > 0) {
            this.waitDurationInOpenStateSeconds = waitDurationInOpenStateSeconds;
        }
        if (permittedNumberOfCallsInHalfOpenState > 0) {
            this.permittedNumberOfCallsInHalfOpenState = permittedNumberOfCallsInHalfOpenState;
        }
        if (slidingWindowSize > 0) {
            this.slidingWindowSize = slidingWindowSize;
        }
        if (minimumNumberOfCalls > 0) {
            this.minimumNumberOfCalls = minimumNumberOfCalls;
        }
        if (StringUtils.isNotBlank(slidingWindowType)) {
            this.slidingWindowType = SlidingWindowType.valueOf(slidingWindowType);
        }
    }

    public static CircuitBreakerConfigDto getDefault() {
        return new CircuitBreakerConfigDto(0, 0, 0, 0, 0, "");
    }
}
