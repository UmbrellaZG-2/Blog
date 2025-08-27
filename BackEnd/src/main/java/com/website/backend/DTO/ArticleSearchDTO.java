package com.website.backend.DTO;

import lombok.Data;

@Data
public class ArticleSearchDTO {

    private String keyword;
    
    private String category;
    
    private int page = 0;
    
    private int size = 10;

}