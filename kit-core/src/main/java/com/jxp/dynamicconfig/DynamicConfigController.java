package com.jxp.dynamicconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-26 17:27
 */
@RequestMapping("/config")
@RestController
@Slf4j
public class DynamicConfigController {

    @Value(value = "${oh:}")
    private String textStr;

    @GetMapping(value = "/all")
    public ResponseEntity<?> all() {
        return ResponseEntity.ok(textStr);
    }



}
