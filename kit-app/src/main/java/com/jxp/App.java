package com.jxp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;


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

//    @GetMapping(value = {"/", "/health"})
//    public ResponseEntity<String> health() {
//        return ResponseEntity.ok("ok");
//    }

//    @GetMapping(value = "/me")
//    public ResponseEntity<Context> me() {
//        return ResponseEntity.ok(RequestContext.getRequestContext());
//    }
}
