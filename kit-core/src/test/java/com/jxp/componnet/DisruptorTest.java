package com.jxp.componnet;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.jxp.Application;
import com.jxp.component.disruptor.DemoEventHandler.DemoEvent;
import com.lmax.disruptor.dsl.Disruptor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-02 10:29
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class DisruptorTest {

    @Resource
    private Disruptor<DemoEvent> demoEventDisruptor;

    @Test
    public void testPublishEvent() throws InterruptedException {

//        for (int i = 0; i < 10; i++) {
//            int finalI = i;
//            demoEventDisruptor.publishEvent((event, sequence) -> event.setValue("你好，我是 Disruptor " +
//                    "Message" + Integer.toString(finalI)));
//        }

        // 暂停 - 测试完手动关闭程序
//        new CountDownLatch(1).await();
    }

}
