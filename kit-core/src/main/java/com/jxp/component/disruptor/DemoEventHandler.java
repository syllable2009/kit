package com.jxp.component.disruptor;

import com.jxp.component.disruptor.DemoEventHandler.DemoEvent;
import com.lmax.disruptor.EventHandler;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-02 10:24
 */
@Slf4j
public class DemoEventHandler implements EventHandler<DemoEvent> {


    @Override
    public void onEvent(DemoEvent demoEvent, long l, boolean b) throws Exception {
        log.info("接收消息：{}", demoEvent.getValue());
    }

    @Data
    public static class DemoEvent {
        private String value;
    }

}
