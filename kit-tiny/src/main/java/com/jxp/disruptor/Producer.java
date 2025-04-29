package com.jxp.disruptor;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

/**
 * @author jiaxiaopeng
 * Created on 2025-04-28 21:13
 */

@Component
public class Producer {

    @Resource
    private Disruptor orderDisruptor;

    public void send(String data) {
        RingBuffer<DemoEvent> ringBuffer = orderDisruptor.getRingBuffer();
        // 获取可以生成的位置
        long next = ringBuffer.next();
        try {
            final DemoEvent demoEvent = ringBuffer.get(next);
            demoEvent.setValue(data);
        } finally {
            ringBuffer.publish(next);
        }
    }

    public void sendBatch(List<String> dataList) {
        RingBuffer<DemoEvent> ringBuffer = orderDisruptor.getRingBuffer();
        // 批量获取n个序列号
        long last = ringBuffer.next(dataList.size());
        // 计算起始序列号
        final long first = last - dataList.size() + 1;
        AtomicLong lo = new AtomicLong(first);

        dataList.forEach(e -> {
            DemoEvent event = ringBuffer.get(lo.get());
            event.setValue(e);
            lo.getAndIncrement();
        });
        ringBuffer.publish(first, last);
    }
}
