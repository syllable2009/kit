package com.jxp.component.disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jxp.component.disruptor.DemoEventHandler.DemoEvent;
import com.lmax.disruptor.dsl.Disruptor;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-02 10:26
 */
@Configuration
public class DisruptorConfig {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Bean("demoEventDisruptor")
    public Disruptor<DemoEvent> demoEventDisruptor() {
        // 环形队列的大小，注意要是2的幂
        int bufferSize = 1024;

        // 创建Disruptor
        Disruptor<DemoEventHandler.DemoEvent> disruptor = new Disruptor<>(DemoEventHandler.DemoEvent::new
                , bufferSize, executor);

        // 连接事件处理器
        disruptor.handleEventsWith(new DemoEventHandler());

        // 开始Disruptor
        disruptor.start();

        return disruptor;
    }

}
