package com.jxp.observer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * @author jiaxiaopeng
 * Created on 2025-06-12 21:55
 */
@Slf4j
public class FluxTest {

    @SuppressWarnings({"checkstyle:WhitespaceAfter", "checkstyle:WhitespaceAround", "checkstyle:RegexpSingleline"})
    public static void main(String[] args) {

        Flux<String> flux1 = Flux.just("A", "B", "C")
                // 在每个元素被发布时执行指定操作（如日志记录、调试或状态检查），但不改变原始数据流
                .doOnNext(e -> {
                    log.info("send:{}", e);
                });

        // 真正的消费处理
        flux1.subscribe(
                line -> System.out.println("收到行: " + line),
                error -> System.err.println("读取失败: " + error),
                () -> System.out.println("文件读取完成")
        );


//        final Result result = stringFlux();
//        final Result result1 = str2(result);
//
//        result1.streamFlux.doOnComplete(() -> {
//                    System.out.println("******");
//                })
//                .doOnNext(e -> System.out.println(e))
//                .subscribe();
    }


    public static Result stringFlux() {
        Flux<String> objectFlux = Flux.create(sink -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    sink.error(e);
                    return;
                }
                sink.next("数据:" + i);

            }
            sink.complete();
        });
        return Result.builder().streamFlux(objectFlux).build();
    }

    //Flux的不可变性‌:Reactor中的Flux每次操作都会返回新实例，原Flux不会被修改。
//    public static Result str2(Result result) {
//        // 虽然被调用，但未形成有效操作链,操作未保存,未接收返回值，导致操作丢失
//        result.streamFlux.doOnNext(a -> System.out.println("2-1:" + a));
//        return result;
//    }

    public static Result str2(Result result) {
        // 重复创建流,保持冷流特性，单次订阅触发
        return Result.builder()
                .streamFlux(result.streamFlux
                        .doOnNext(a -> System.out.println("2-1:" + a)))
                .build();
    }


    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Result {
        private Flux<String> streamFlux;
    }
}
