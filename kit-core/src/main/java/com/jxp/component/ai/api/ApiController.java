package com.jxp.component.ai.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * 责任链模式、过滤器模式和策略模式
 * @author jiaxiaopeng
 * Created on 2025-02-21 16:34
 */

@Slf4j
@RestController
public class ApiController {

    @GetMapping("/chat")
    public ResponseEntity<String> chat(@RequestParam String message) {
        return ResponseEntity.ok("get");
    }
}
