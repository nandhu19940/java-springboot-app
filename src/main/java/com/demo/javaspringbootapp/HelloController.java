package com.demo.javaspringbootapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Java Spring Boot - CI/CD Pipeline is working 2026 learning Devops Java pipeline!";
    }

}