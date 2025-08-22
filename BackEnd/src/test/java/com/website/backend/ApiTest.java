package com.website.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
public class ApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    public void contextLoads() {
        // 测试Spring上下文是否能正确加载
        assert mockMvc != null;
        assert restTemplate != null;
        assert port > 0;
    }

    /**
     * 测试ArticleController的各个接口
     */
    @Test
    public void testArticleController() throws Exception {
        // 测试获取文章列表接口
        mockMvc.perform(get("/api/articles?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        
        // 测试搜索文章接口
        mockMvc.perform(get("/api/articles/search?keyword=test&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        
        // 测试按分类获取文章接口
        mockMvc.perform(get("/api/articles/category/get/test-category?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        
        // 测试获取文章详情接口
        // 注意：需要先创建一篇文章才能测试此接口
        mockMvc.perform(get("/api/articles/get/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * 测试ImageController的各个接口
     */
    @Test
    public void testImageController() throws Exception {
        // 测试获取文章封面图片接口
        mockMvc.perform(get("/api/images/article/1/cover/get"))
                .andExpect(status().isNotFound()); // 由于没有文章ID为1的数据，应该返回404
        
        // 测试获取文章的所有图片接口
        mockMvc.perform(get("/api/images/article/1/getAll"))
                .andExpect(status().isNotFound()); // 由于没有文章ID为1的数据，应该返回404
    }

    /**
     * 测试AuthController的各个接口
     */
    @Test
    public void testAuthController() throws Exception {
        // 测试Auth API根路径接口
        mockMvc.perform(get("/api/auth/get"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        
        // 测试游客登录接口 - 该接口是POST请求
        mockMvc.perform(post("/api/auth/guest/login"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * 测试HomeController的各个接口
     */
    @Test
    public void testHomeController() throws Exception {
        // 测试首页接口
        mockMvc.perform(get("/api/home"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        
        // 测试关于我接口
        mockMvc.perform(get("/api/home/aboutMe"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * 测试TagController的各个接口
     */
    @Test
    public void testTagController() throws Exception {
        // 测试获取所有标签接口
        mockMvc.perform(get("/api/tags/get"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * 测试AttachmentController的各个接口
     */
    @Test
    public void testAttachmentController() throws Exception {
        // 测试获取文章附件列表接口
        mockMvc.perform(get("/api/attachments/article/get/1"))
                .andExpect(status().isInternalServerError()); // 由于没有文章ID为1的数据，会抛出异常，返回500
    }
}