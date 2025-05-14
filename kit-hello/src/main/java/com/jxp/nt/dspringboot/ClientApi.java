package com.jxp.nt.dspringboot;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-13 11:28
 */

@Slf4j
@RestController
public class ClientApi {

//    @Resource
//    private NettyClientConfig nettyClientConfig;

    @GetMapping("/login")
    public ResponseEntity<Boolean> messageCallback(@RequestParam String userId) throws InterruptedException {
//        nettyClientConfig.channel.writeAndFlush(LoginReqBean.builder()
//                .userId(userId)
//                .username("匿名用户")
//                .build());
        return ResponseEntity.ok(true);
    }

}
