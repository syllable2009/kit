package com.jxp.component.longpoll;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.jxp.response.Result;
import com.jxp.resultcode.CommonResultCode;
import com.jxp.resultcode.ResultCode;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-11 17:06
 */
@Slf4j
@RestController
public class LongPoolApi {

    // 存放监听某个Id的长轮询集合
    public static Multimap<String, DeferredResult<Result<String>>> watchRequests = Multimaps.synchronizedMultimap(HashMultimap.create());

    public static String OK_STRING = "{\"code\":0,\"data\":false}";

    @GetMapping("/getData")
    public DeferredResult<String> getData() {
        DeferredResult<String> deferredResult = new DeferredResult<>();

        // 模拟异步处理返回的JSON数据
        String jsonData = "{\"name\": \"John\", \"age\": 30}";

        // 设置返回的JSON数据
        deferredResult.setResult(jsonData);

        return deferredResult;
    }

    // 长轮询接口
    @ResponseBody
    @GetMapping("/long-poll")
    public DeferredResult<Result<String>> longPoll(@RequestParam("postId") String postId,
            HttpServletResponse response) throws InterruptedException {
        // 创建 DeferredResult 对象，设置超时时间为 30 秒
        DeferredResult<Result<String>> deferredResult = new DeferredResult<>(10000L, Result.ok(
                "init"));
        // 将 DeferredResult 存储在 Map 中
        watchRequests.put(postId, deferredResult);

        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        // 设置超时处理
        deferredResult.onTimeout(() -> {
            // 当请求完成时，从 Map 中移除
            watchRequests.remove(postId, deferredResult);
            deferredResult.setResult(Result.ok("timeout"));
        });
        deferredResult.onCompletion(() -> {
            watchRequests.remove(postId, deferredResult);
            deferredResult.setResult(Result.ok("completion"));
        });
        deferredResult.onError(e -> {
            watchRequests.remove(postId, deferredResult);
            deferredResult.setResult(Result.ok("error"));
        });

        return deferredResult;
    }

    @GetMapping("/send-notification")
    public void sendNotification(@RequestParam String postId,
            @RequestParam String message) {
        // 获取对应的 DeferredResult
        if (watchRequests.containsKey(postId)) {
            final Collection<DeferredResult<Result<String>>> deferredResults =
                    watchRequests.get(postId);
            if (CollUtil.isEmpty(deferredResults)) {
                return;
            }
            for (DeferredResult<Result<String>> deferredResult : deferredResults) {
                deferredResult.setResult(Result.ok("我更新了:" + LocalDateTime.now()));
            }
        }
    }

    @SneakyThrows
    private void setErrorResponse(HttpServletResponse response, CommonResultCode code) {
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.println(JSONUtil.parse(Result.error(ResultCode.builder()
                .code(code.getCode())
                .zhCn(code.getZhCn())
                .enUs(code.getEnUs())
                .build())).toString());
    }
}
