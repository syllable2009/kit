package com.jxp.observer;

import java.util.stream.Stream;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-16 11:04
 */
@Slf4j
public class StreamObserverDemo {
    public static void main(String[] args) {
        test1();
    }

    private static void test1() {
        String onCompleted = "Stream completed";

        // 通过StreamObserver接口来实现异步流处理。
        // 首先，我们需要定义一个StreamObserver对象，并实现其onNext、onError和onCompleted方法来处理数据流中的数据、错误和完成事件。
        StreamObserver<String> streamObserver = new StreamObserver<String>() {
            @Override
            public void onNext(String value) {
                log.info("streamObserver,onNext,value:{}", value);
            }

            @Override
            public void onError(Throwable t) {
                log.info("streamObserver,onError,Throwable:{}", t.getMessage());
            }

            @Override
            public void onCompleted() {
                log.info("streamObserver,onCompleted:{}", onCompleted);
            }
        };

        // 使用StreamObserver处理数据流
        // 创建一个数据流
        // 在服务端处理数据流
        Stream<String> dataStream = Stream.of("Data1", "Data2", "Data3");
        dataStream.forEach(value -> {
            streamObserver.onNext(value);
        });
        streamObserver.onError(new RuntimeException("错误拉"));
        streamObserver.onCompleted();
    }
}
