package com.jxp.observer;

import java.util.function.Consumer;
import java.util.stream.Stream;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-16 11:04
 */
@Slf4j
public class StreamObserverDemo {
    @SuppressWarnings("checkstyle:WhitespaceAfter")
    public static void main(String[] args) {
//        test1();
        // 基于java8的reactor，手动生成数据流,是一个publisher生产者，处理一定延迟的才能够返回的io操作时，不会阻塞，而是立刻返回一个流
        // 并且订阅这个流，并且订阅这个流，当这个流上产生了返回数据，可以立刻得到通知并调用回调函数处理数据
        final Flux<Object> objectFlux = Flux.create(new Consumer<FluxSink<Object>>() {
            @Override
            public void accept(FluxSink<Object> sink) {
                // 阻塞耗时的io操作在这里
                sink.next("ddddd");
                sink.complete();
                // 资源清理回调
                sink.onDispose(() -> System.out.println("资源已释放"));
            }
        });

        // 进行consumer消费，订阅后才回触发数据流，不订阅就什么都不会发生。
        objectFlux.subscribe(new Consumer<Object>() {
                                 @Override
                                 public void accept(Object data) {
                                     System.out.println("接收数据: " + data);
                                 }
                             },  // 处理元素
                error -> {
                    System.err.println("发生异常: " + error);
                }, // 处理错误
                () -> System.out.println("流已结束"));
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
