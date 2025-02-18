package com.jxp;

import java.time.Duration;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.jxp.openapi.CircuitBreakerConfigDto;
import com.jxp.openapi.DemoClient;
import com.jxp.openapi.HttpClientConfigDto;
import com.jxp.openapi.OpenApiClientConfig;
import com.jxp.service.DemoService;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-30 11:27
 */
@Slf4j
@RestController
@SpringBootApplication
public class HelloApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloApplication.class, args);
    }

    @Resource
    private DemoService demoService;

    @GetMapping(value = {"/", "/health"})
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ok");
    }

    @GetMapping(value = {"/demoService", "/demo"})
    public ResponseEntity<String> demo() {
        final String s = demoService.sayHello();
        return ResponseEntity.ok(s);
    }

    @GetMapping("/posts")
    public ResponseEntity<JsonNode> posts() {
        String baseUrl = "https://jsonplaceholder.typicode.com";
        final HttpClientConfigDto httpClientConfigDto = HttpClientConfigDto.getDefault(baseUrl);
        final DemoClient openApiClient = OpenApiClientConfig.createOpenApiClient(httpClientConfigDto, null, DemoClient.class);
        final JsonNode call = call("open-service-A", "/posts", () -> openApiClient.posts(), null,
                "");
        return ResponseEntity.ok(call);
    }

    @SneakyThrows
    public static <T> T call(String methodProvider,
            String uri,
            Supplier<Call<T>> callSupplier,
            Function<T, String> toPerfResult,
            Object... requestParamForLog) throws RuntimeException {
        //断路器包装，在OpenApi服务端出现问题时，快速失败
        Response<T> response = CircuitBreaker
                .decorateCheckedSupplier(createCircuitBreaker(CircuitBreakerConfigDto.getDefault(),
                                DemoClient.class.getSimpleName()),
                        () -> callSupplier.get().execute())
                .apply();
        if (!response.isSuccessful()) {
            log.warn("openapi invoke error, http code is not ok, uri: {}, httpCode: {}, response: "
                            + "{}，requestParam: {}",
                    uri, response.code(), response, Arrays.toString(requestParamForLog));
        }
        final T body = response.body();
        if (body == null) {
            log.warn("openapi invoke error, returns null, uri: {}, requestParam: {}",
                    uri, Arrays.toString(requestParamForLog));
        }
        return body;
    }

    /**
     * 创建断路器
     */
    public static CircuitBreaker createCircuitBreaker(CircuitBreakerConfigDto breakerConfig, String name) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                //故障阈值百分比，超过这个阈值就会打开断路器
                .failureRateThreshold(breakerConfig.getFailureRateThreshold())
                //断路器打开的持续时间，到达这个时间后，断路器进入half open状态
                .waitDurationInOpenState(Duration.ofSeconds(breakerConfig.getWaitDurationInOpenStateSeconds()))
                .permittedNumberOfCallsInHalfOpenState(breakerConfig.getPermittedNumberOfCallsInHalfOpenState())
                //断路器关闭时，环形缓冲区大小
                .slidingWindow(breakerConfig.getSlidingWindowSize(), breakerConfig.getMinimumNumberOfCalls(),
                        breakerConfig.getSlidingWindowType())
                .build();
        return CircuitBreakerRegistry.of(config).circuitBreaker(name);
    }
}
