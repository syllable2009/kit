package com.jxp.hotline.api;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.handler.EventHandler;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 接收事件回调，用于和三方im系统的对接
 * @author jiaxiaopeng
 * Created on 2025-03-20 10:26
 */

@RequestMapping("/callback")
@Slf4j
@RestController
public class CallbackApi {

    @Resource
    private Map<String, EventHandler> eventHandlerMap;

    @PostMapping("/message")
    public ResponseEntity<Boolean> kimMessageCallback(@RequestBody MessageEvent event) {
        log.info("kimMessageCallback,event:{}", JSONUtil.toJsonStr(event));
        // 消息去重
        // 落库如果有必要，顺序入队列：削峰
        // 模拟消费
        queueConsumer(event);
        return ResponseEntity.ok(true);
    }

    // 顺序消费
    private void queueConsumer(MessageEvent event) {
        // 查找对应的处理器
        final EventHandler eventHandler = eventHandlerMap.get(event.getEventType());
        if (null == eventHandler) {
            log.error("queueConsumer return,not found eventHandler,event:{}", JSONUtil.toJsonStr(event));
            return;
        }
        eventHandler.handle(event);
    }
}
