package com.jxp.disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import lombok.extern.slf4j.Slf4j;

/**
 * 1.创建一个 POJO 类来表示事件，确保它是不可变的（immutable）。
 * 2.public class DisruptorConfig
 *
 * @author jiaxiaopeng
 * Created on 2024-12-02 10:26
 */
@Slf4j
@Configuration
public class DisruptorConfig {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    // 定义 RingBuffer 大小（必须是 2 的幂次方）
    private static int bufferSize = 1024;

    @Bean
    public Disruptor<DemoEvent> orderDisruptor() {

        // 定义事件工厂，预分配事件对象，减少GC压力
        EventFactory<DemoEvent> eventFactory = () -> new DemoEvent();
        final Disruptor<DemoEvent> orderDisruptor = new Disruptor<>(
                eventFactory,
                bufferSize,  // 缓冲区大小（需为2^n）
                Executors.defaultThreadFactory(),
                ProducerType.MULTI,  // 多生产者模式
                new YieldingWaitStrategy()  // 低延迟策略  new BlockingWaitStrategy() //等待模式
        );
        // 注册消费者
        final Consumer first = new Consumer("first");
        final Consumer second = new Consumer("second");
        final Consumer third = new Consumer("third");
        // 分组消费：每个生产者生产的数据只能被一个消费者消费
        orderDisruptor.handleEventsWithWorkerPool(first, second);
        // 测试重复消费：每个消费者重复消费生产者生产的数据
//        orderDisruptor.handleEventsWith(first, second);
        // 测试链路模式
//        orderDisruptor.handleEventsWith(third).then(first).then(second);
        // 测试钻石模式
//        orderDisruptor.handleEventsWithWorkerPool(third, second).then(first);
        // 启动
        orderDisruptor.start();
        return orderDisruptor;
    }
}
