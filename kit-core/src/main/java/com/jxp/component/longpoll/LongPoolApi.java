package com.jxp.component.longpoll;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.jxp.response.Result;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-11 17:06
 */
@Slf4j
@RestController
public class LongPoolApi {

    // 存放监听某个Id的长轮询集合
    public static Map<String, CopyOnWriteArrayList<DeferredResult<Result<String>>>> watchRequests = new ConcurrentHashMap<>();

    @GetMapping("/getData")
    public ResponseEntity<?> getData(@RequestParam("postId") String postId) {
        return ResponseEntity.ok(getDeferredResults(postId));
    }

    // 长轮询接口
    @ResponseBody
    @GetMapping("/long-poll")
    public DeferredResult<Result<String>> longPoll(@RequestParam("postId") String postId,
            HttpServletResponse response) throws InterruptedException {
        // 创建 DeferredResult 对象，设置超时时间为 30 秒
        DeferredResult<Result<String>> deferredResult = new DeferredResult<>(30000L);
        // 将 DeferredResult 存储在 Map 中，避免在并发环境中出现多个线程尝试同时创建相同键的情况
        addDeferredResult(postId, deferredResult);
        // 设置超时处理
        deferredResult.onTimeout(() -> {
            // 当请求完成时，从 Map 中移除
            removeDeferredResult(postId, deferredResult);
            deferredResult.setResult(Result.ok("timeout"));
        });
        deferredResult.onCompletion(() -> {
            removeDeferredResult(postId, deferredResult);
            deferredResult.setResult(Result.ok("completion"));
        });
        deferredResult.onError(e -> {
            removeDeferredResult(postId, deferredResult);
            deferredResult.setResult(Result.ok("error"));
        });

        return deferredResult;
    }

    @GetMapping("/send-notification")
    public ResponseEntity<Boolean> sendNotification(@RequestParam String postId,
            @RequestParam String message) {
        // 获取对应的 DeferredResult
        if (watchRequests.containsKey(postId)) {
            final Collection<DeferredResult<Result<String>>> deferredResults =
                    watchRequests.get(postId);
            if (CollUtil.isEmpty(deferredResults)) {
                return ResponseEntity.ok(false);
            }
            for (DeferredResult<Result<String>> deferredResult : deferredResults) {
                removeDeferredResult(postId, deferredResult);
                deferredResult.setResult(Result.ok("我更新了:" + LocalDateTime.now()));
            }
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }

    // 从 CopyOnWriteArrayList 中删除特定的 DeferredResult
    public static void removeDeferredResult(String key, DeferredResult<Result<String>> result) {
        // 获取对应的 CopyOnWriteArrayList
        CopyOnWriteArrayList<DeferredResult<Result<String>>> results = watchRequests.get(key);
        if (results != null) {
            results.remove(result); // 从列表中删除
            // 如果需要，可以在此处检查列表是否为空，如果为空则可以选择删除整个键
            if (results.isEmpty()) {
                watchRequests.remove(key); // 删除整个键
            }
        }
    }

    // 添加 DeferredResult
    public static void addDeferredResult(String key, DeferredResult<Result<String>> result) {
        watchRequests.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(result);
    }

    // 获取 DeferredResult 列表
    public static CopyOnWriteArrayList<DeferredResult<Result<String>>> getDeferredResults(String key) {
        return watchRequests.get(key);
    }
}
