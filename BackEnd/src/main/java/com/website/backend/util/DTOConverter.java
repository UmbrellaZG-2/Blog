package com.website.backend.util;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.website.backend.DTO.ArticleDTO;
import com.website.backend.entity.Article;

@Component
public class DTOConverter {

	private final ModelMapper modelMapper;

	public DTOConverter() {
		this.modelMapper = new ModelMapper();
	}

	public ArticleDTO convertToDTO(Article article) {
		return modelMapper.map(article, ArticleDTO.class);
	}

	public Article convertToEntity(ArticleDTO dto) {
		return modelMapper.map(dto, Article.class);
	}

}