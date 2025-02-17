package com.jxp.component.disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jxp.component.disruptor.DemoEventHandler.DemoEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * 1.创建一个 POJO 类来表示事件，确保它是不可变的（immutable）。
 * 2.public class DisruptorConfig
 *
 * @author jiaxiaopeng
 * Created on 2024-12-02 10:26
 */
@Configuration
public class DisruptorConfig {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Bean("demoEventDisruptor")
    public Disruptor<DemoEvent> demoEventDisruptor() {
//        DaemonThreadFactory.INSTANCE, // 使用守护线程
        // 定义事件工厂
        EventFactory<DemoEvent> eventFactory = DemoEventHandler.DemoEvent::new;

        // 定义 RingBuffer 大小（必须是 2 的幂次方）
        int bufferSize = 1024;

        // 创建Disruptor
        Disruptor<DemoEventHandler.DemoEvent> disruptor = new Disruptor<>(eventFactory
                , bufferSize, executor
                , ProducerType.MULTI // 多生产者模式
                , new BlockingWaitStrategy() // 等待模式
        );

        // 注册事件处理器
        disruptor.handleEventsWith(new DemoEventHandler());

        // 启动 Disruptor
        disruptor.start();

        return disruptor;
    }

}
