package com.jxp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jxp.system.application.ApplicationManager;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-30 11:27
 */
@Slf4j
@RestController
@SpringBootApplication
public class TinyApplication {

    public static void main(String[] args) {
        SpringApplication.run(TinyApplication.class, args);
    }


    @GetMapping(value = {"/", "/health"})
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ok");
    }

    @GetMapping(value = "/run")
    public ResponseEntity<String> run(@RequestParam("arg") String[] args) {
        ApplicationManager.main(args);
        return ResponseEntity.ok("done");
    }
}
