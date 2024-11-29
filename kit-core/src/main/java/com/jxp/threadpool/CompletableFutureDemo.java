package com.jxp.threadpool;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2024-07-12 11:50
 */

@Slf4j
public class CompletableFutureDemo {

    private static final ExecutorService COMMON_THREAD_POOL = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() * 2, // 核心线程数
            100, // 最大线程数
            3600L, // 空闲线程存活时间
            TimeUnit.SECONDS, // 时间单位
            new LinkedBlockingQueue<>(1024), // 任务队列
            new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
    );

    @SuppressWarnings("checkstyle:MagicNumber")
    public static void main(String[] args) throws Exception {
        final CompletableFuture<Integer> f1 =
                CompletableFuture.supplyAsync(() -> getUnClaimNum("aaa"));
        final CompletableFuture<Integer> f2 =
                CompletableFuture.supplyAsync(() -> getUnClaimNum("bbb"));

        final CompletableFuture<Object> objectCompletableFuture = CompletableFuture.anyOf(f1, f2);
        try {
            objectCompletableFuture.get(3, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.info("3s内没有出结果请耐心等待");
        } catch (Exception e) {
            log.info("结果出现异常", e);
            return;
        }
        objectCompletableFuture.join();
        if (f1.isDone()) {
            log.info("最终结果出来啦，1:{}", f1.get());
        } else if (f2.isDone()) {
            log.info("最终结果出来啦，2:{}", f2.get());
        } else {
            log.info("过去很久了，没有结果");
        }

    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public static Integer getUnClaimNum(String assistantId) {
        final Integer l = RandomUtil.randomInt(0, 10000);
        ThreadUtil.sleep(l);
        return l;
    }

}
