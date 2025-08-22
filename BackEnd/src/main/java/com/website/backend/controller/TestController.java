package com.website.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/get")
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> testGet() {
        return ResponseEntity.ok("GET请求成功");
    }

    @PostMapping("/post")
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> testPost(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok("POST请求成功，收到数据: " + request.toString());
    }
}