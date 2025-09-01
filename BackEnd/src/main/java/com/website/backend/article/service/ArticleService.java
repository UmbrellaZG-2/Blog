package com.website.backend.article.service;

import java.util.List;
import java.util.UUID;
import com.website.backend.article.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ArticleService {

	Page<Article> getAllArticles(Pageable pageable);

	Page<Article> getArticlesByCategory(String category, Pageable pageable);

	Article getArticleById(UUID articleId);

	Article createArticle(String title, String category, String content, String summary,
			String tags, String status, MultipartFile coverImage, MultipartFile[] attachments);

	Article updateArticle(UUID articleId, String title, String category, String content, boolean deleteAttachment,
			boolean deletePicture, MultipartFile attachment, MultipartFile picture);

	void deleteArticle(UUID articleId);

	// 草稿功能相关方法
	Article saveDraft(Article article);
	List<Article> getUserDrafts(Long userId);
	Article publishDraft(Long id);
	boolean isArticleDraft(Long id);

}
