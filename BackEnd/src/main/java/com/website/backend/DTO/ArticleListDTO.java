package com.website.backend.DTO;

import lombok.Data;
import java.util.List;

@Data
public class ArticleListDTO {

	private List<ArticleDTO> articles;

	private long totalElements;

	private int totalPages;

	private int currentPage;

	private int pageSize;

}