package com.jxp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.JedisPool;


/**
 * Hello world!
 *
 */
@RestController
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
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
}
