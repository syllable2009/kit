package com.jxp;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import lombok.extern.slf4j.Slf4j;


/**
 * Hello world!
 *
 */

@Slf4j
@RestController
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    // 依赖的core方法中也有此定义
//    @GetMapping(value = {"/", "/health"})
//    public ResponseEntity<String> health() {
//        return ResponseEntity.ok("ok");
//    }

//    @GetMapping(value = "/me")
//    public ResponseEntity<Context> me() {
//        return ResponseEntity.ok(RequestContext.getRequestContext());
//    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @GetMapping("/bodyEmitter")
    public ResponseBodyEmitter handle() {
        // 创建一个ResponseBodyEmitter，-1代表不超时
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(-1L);
        // 异步执行耗时操作
        CompletableFuture.runAsync(() -> {
            try {
                // 模拟耗时操作
                for (int i = 0; i < 1000; i++) {
                    System.out.println("bodyEmitter " + i);
                    // 发送数据
                    emitter.send("bodyEmitter " + i + " @ " + new Date() + "\n");
                    Thread.sleep(1000);
                }
                // 完成
                emitter.complete();
            } catch (Exception e) {
                // 发生异常时结束接口
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    private static final Map<String, SseEmitter> EMITTER_MAP = new ConcurrentHashMap<>();

    @CrossOrigin
    @GetMapping("/subSseEmitter/{userId}")
    public SseEmitter sseEmitter(@PathVariable String userId) {
        log.info("sseEmitter: {}", userId);
        SseEmitter emitterTmp = new SseEmitter(-1L);
        EMITTER_MAP.put(userId, emitterTmp);
        CompletableFuture.runAsync(() -> {
            try {
                SseEmitter.SseEventBuilder event = SseEmitter.event()
                        .data("sseEmitter" + userId + " @ " + LocalTime.now())
                        .id(String.valueOf(userId))
                        .name("sseEmitter");
                emitterTmp.send(event);
            } catch (Exception ex) {
                emitterTmp.completeWithError(ex);
            }
        });
        return emitterTmp;
    }

    @CrossOrigin
    @GetMapping("/sendSseMsg/{userId}")
    public void sseEmitter(@PathVariable String userId, String msg) throws IOException, InterruptedException {
        SseEmitter sseEmitter = EMITTER_MAP.get(userId);
        if (sseEmitter == null) {
            return;
        }
        for (int i = 0; i < 1000; i++) {
            System.out.println("bodyEmitter " + i);
            // 发送数据
            sseEmitter.send("sseEmitter " + userId + " @ " + LocalTime.now() + "@" + i);
            Thread.sleep(1000);
        }
    }



    @GetMapping("/streamingResponse")
    public ResponseEntity<StreamingResponseBody> handleRbe() {

        StreamingResponseBody stream = out -> {
            String message = "streamingResponse";
            for (int i = 0; i < 1000; i++) {
                try {
                    out.write(((message + i) + "\r\n").getBytes());
                    out.write("\r\n".getBytes());
                    //调用一次flush就会像前端写入一次数据
                    out.flush();
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(stream);
    }
}
