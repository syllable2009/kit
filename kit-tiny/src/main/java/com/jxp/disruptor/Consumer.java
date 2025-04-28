package com.jxp.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-04-28 21:16
 */

@AllArgsConstructor
@Slf4j
public class Consumer implements WorkHandler<DemoEvent>, EventHandler<DemoEvent> {

    private String name;

    @Override
    public void onEvent(DemoEvent demoEvent, long l, boolean b) throws Exception {
        this.onEvent(demoEvent);
    }

    @Override
    public void onEvent(DemoEvent demoEvent) throws Exception {
        log.info("Consumer,name:{},content:{}", name, JSONUtil.toJsonStr(demoEvent));
    }
}
