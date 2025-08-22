package com.website.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
public class DatabaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testDatabaseInitialization() throws Exception {
        // 测试获取文章列表接口，验证数据库是否初始化了数据
        mockMvc.perform(get("/api/articles?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
        
        // 测试获取特定文章接口，验证是否有文章ID为1的数据
        mockMvc.perform(get("/api/articles/get/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
}