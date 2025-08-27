package com.website.backend.service;

import java.util.UUID;
import com.website.backend.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ArticleService {

	Page<Article> getAllArticles(Pageable pageable);

	Page<Article> getArticlesByCategory(String category, Pageable pageable);

	Article getArticleById(UUID articleId);

	Article createArticle(String title, String category, String content, MultipartFile attachment,
			MultipartFile picture);

	Article updateArticle(UUID articleId, String title, String category, String content, boolean deleteAttachment,
			boolean deletePicture, MultipartFile attachment, MultipartFile picture);

	void deleteArticle(UUID articleId);

}