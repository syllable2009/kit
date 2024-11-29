package com.jxp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jxp.web.AllowAnonymous;
import com.jxp.web.Context;
import com.jxp.web.RequestContext;

/**
 * Hello world!
 *
 */
@RestController
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @GetMapping(value = {"/", "/health"})
    @AllowAnonymous
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ok");
    }

    @GetMapping(value = "/me")
    @AllowAnonymous
    public ResponseEntity<Context> me() {
        return ResponseEntity.ok(RequestContext.getRequestContext());
    }
}
