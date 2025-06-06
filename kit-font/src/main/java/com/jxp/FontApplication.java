package com.jxp;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-30 11:27
 */
@Slf4j
@RestController
@SpringBootApplication(scanBasePackages = {"com.jxp"}, exclude = {MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class, SecurityAutoConfiguration.class})
public class FontApplication {

    public static void main(String[] args) {
        SpringApplication.run(FontApplication.class, args);
    }

    @GetMapping(value = {"/", "/health"})
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ok");
    }

    // 对Callable的封装
    @SuppressWarnings("checkstyle:MagicNumber")
    @GetMapping("/webAsyncTask")
    public WebAsyncTask<String> webAsyncTask() {
        WebAsyncTask<String> result = new WebAsyncTask<>(30000L, () -> {
            return "success";
        });
        result.onTimeout(() -> {
            log.info("timeout callback");
            return "timeout callback";
        });
        result.onCompletion(() -> log.info("finish callback"));
        return result;
    }

    //定义一个全局的变量，用来存储DeferredResult对象
    private Map<String, DeferredResult<String>> deferredResultMap = new ConcurrentHashMap<>();

    @GetMapping("/testDeferredResult")
    public DeferredResult<String> testDeferredResult() {
        DeferredResult<String> deferredResult = new DeferredResult<>();
        deferredResultMap.put("test", deferredResult);
        return deferredResult;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @GetMapping("/testCallAble")
    public Callable<String> testCallAble() {
        return () -> {
            Thread.sleep(40000L);
            return "hello";
        };
    }

}
