package com.jxp.nt.dspringboot;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jxp.nt.done.bean.LoginReqBean;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-13 11:28
 */

@Slf4j
@RestController
public class ClientApi {

    @Resource
    private NettyClientService nettyClientService;

    @GetMapping("/login")
    public ResponseEntity<Boolean> messageCallback(@RequestParam String userId) throws InterruptedException {
        nettyClientService.sendMessage(LoginReqBean.builder()
                .userId(userId)
                .username("匿名用户")
                .build());
        return ResponseEntity.ok(true);
    }

}
