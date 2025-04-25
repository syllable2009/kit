package com.jxp.ct;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

/**
 * @author jiaxiaopeng
 * Created on 2025-04-25 09:44
 */

@SuppressWarnings("checkstyle:VisibilityModifier")
public class ThreadPool {

    private static final Logger log = LoggerFactory.getLogger(ThreadPool.class);


    public static ThreadPoolExecutor rpcExecutors = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2,
            600,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1024),
            new CustomizableThreadFactory("rpc-thread-pool-"),
            (r, executor) -> {
                log.warn("rpcExecutors threadpool is full");
                r.run();
            });
    public static ThreadPoolExecutor httpExecutors = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2,
            600,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1024),
            new CustomizableThreadFactory("http-thread-pool-"),
            (r, executor) -> {
                log.warn("httpExecutors threadpool is full");
                r.run();
            });

    public static <T> CompletableFuture<T> withPool(Supplier<CompletableFuture<T>> supplier) {
        return supplier.get().exceptionally(ex -> {
            log.error("withPool error,", ex);
            return null;
        });
    }

    public static void main(String[] args) {
        /**
         * 有异常捕获了后续也会执行
         */
//        withPool(() ->
//                CompletableFuture.runAsync(() -> exceptionMethod(),
//                        ThreadPool.rpcExecutors)
//
//        ).thenRunAsync(() -> {
//            printMethod();
//        }, ThreadPool.httpExecutors);


        // 没有捕获异常，一旦异常就停止执行后面逻辑了
        final CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> exceptionMethod(),
                        ThreadPool.rpcExecutors)
                .thenRunAsync(() -> {
                    printMethod();
                }, ThreadPool.httpExecutors);

        voidCompletableFuture.join();
    }


    public static Integer exceptionMethod() {
        final int i = 1 / 0;
        return i;
    }

    public static void printMethod() {
        log.info("printMethod exec");
    }
}
