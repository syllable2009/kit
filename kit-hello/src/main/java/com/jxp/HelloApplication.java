package com.jxp;

import javax.annotation.Resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jxp.service.DemoService;

import lombok.extern.slf4j.Slf4j;

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
        final String s = demoService.sayHello();
        return ResponseEntity.ok(s);
    }
}
