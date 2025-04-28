package com.jxp.disruptor;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-04-28 21:06
 */
@Slf4j
@RestController("/d")
public class DisruptorApi {

    @Resource
    private Producer producer;

    @GetMapping("/producer1")
    public ResponseEntity<String> producer1(@RequestParam String value) {
        // 单个发送
        final String s = value.replaceAll("，", ",");
        final List<String> split = StrUtil.split(s, ",");
        split.forEach(e -> producer.send(e));
        return ResponseEntity.ok("ok");
    }
}
