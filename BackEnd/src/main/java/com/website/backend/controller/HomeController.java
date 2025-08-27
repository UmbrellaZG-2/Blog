package com.website.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.website.backend.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/home")
public class HomeController {
    
    @GetMapping
    public ResponseEntity<ApiResponse<Void>> home() {
        return ResponseEntity.ok(ApiResponse.success("欢迎访问首页", null));
    }

    @GetMapping("/redirect/aboutMe")
    public ResponseEntity<Void> redirectToAboutMe() {
        log.info("重定向到关于我页面");
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", "/aboutMe.html").build();
    }

    @GetMapping("/aboutMe")
    public ResponseEntity<ApiResponse<Map<String, String>>> aboutMe() {
        Map<String, String> data = new HashMap<>();
        data.put("message", "这是关于我页面的API数据");
        data.put("status", "success");
        return ResponseEntity.ok(ApiResponse.success("获取关于我信息成功", data));
    }
}