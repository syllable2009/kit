package com.jxp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;


/**
 * Hello world!
 *
 */
@Slf4j
@RestController
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
//        log.info("{}", getLuaScript("a", Lists.newArrayList("2", "1", "3")));
        SpringApplication.run(Application.class, args);

        // jvm钩子模式关闭资源
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 强制关闭第三方SDK
        }));
    }

    @GetMapping(value = {"/", "/health"})
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ok");
    }


    @Autowired(required = false)
    private JedisPool jedisPool;


    @GetMapping("/set")
    public ResponseEntity<String> setValue(@RequestParam String key, @RequestParam String value) {
        final String set = jedisPool.getResource().set(key, value);
        return ResponseEntity.ok(set);
    }

    @GetMapping("/get")
    public ResponseEntity<String> getValue(@RequestParam String key) {
        final String get = jedisPool.getResource().get(key);
        return ResponseEntity.ok(get);
    }

    @GetMapping("/delete")
    public ResponseEntity<String> deleteValue(@RequestParam String key) {
        final Long del = jedisPool.getResource().del(key);
        return ResponseEntity.ok(Long.toString(del));
    }

    @GetMapping("/eval")
    public ResponseEntity<List<String>> evalLuaScript(@RequestParam String key) {
        final String luaScript = getLuaScript(key, Lists.newArrayList("2", "1", "3"));
        // 此时脚本返回为null
        jedisPool.getResource().eval(luaScript);
//        int start = (pageNum - 1) * pageSize; // 从0开始
//        int end = start + pageSize - 1;
        final List<String> lrange = jedisPool.getResource().lrange(key, 0, -1);
        return ResponseEntity.ok(lrange);
    }

    private static String getLuaScript(String key, List<String> value) {
        String luaScript =
                "redis.call('DEL', '{}') "
                        + "redis.call('RPUSH', '{}', '{}')";
        return StrUtil.format(luaScript, key, key, Joiner.on("','").skipNulls().join(value));
    }
}
