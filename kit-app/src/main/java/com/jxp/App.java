package com.jxp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jxp.web.AllowAnonymous;
import com.jxp.web.Context;
import com.jxp.web.RequestContext;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * Hello world!
 *
 */
@OpenAPIDefinition(
        info = @Info(
                title = "My API",
                version = "1.0",
                description = "This is a sample API documentation"
        )
)
@RestController
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Operation(summary = "Say Hello", description = "Returns a hello message")
    @GetMapping(value = {"/", "/health"})
    @AllowAnonymous
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ok");
    }


    @Operation(summary = "user info", description = "返回个人信息")
    @GetMapping(value = "/me")
    @AllowAnonymous
    public ResponseEntity<Context> me() {
        return ResponseEntity.ok(RequestContext.getRequestContext());
    }
}
