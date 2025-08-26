package com.website.backend.DTO;

import lombok.Data;

/**
 * 文章搜索参数DTO 用于封装文章搜索请求参数
 */
@Data
public class ArticleSearchDTO {

    private String keyword;
    
    private int page = 0;
    
    private int size = 10;

}