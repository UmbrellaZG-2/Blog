package com.website.backend.article.service;

import com.website.backend.article.entity.Article;
import com.website.backend.article.repository.ArticleRepository;
import com.website.backend.article.exception.ArticleNotFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 文章服务辅助类，提供通用的文章查找功能
 */
@Component
public class ArticleServiceHelper {

	private final ArticleRepository articleRepository;

	public ArticleServiceHelper(ArticleRepository articleRepository) {
		this.articleRepository = articleRepository;
	}

	/**
	 * 根据文章ID查找文章，如果不存在则抛出ArticleNotFoundException异常
	 * 
	 * @param articleId 文章ID
	 * @return 文章对象
	 * @throws ArticleNotFoundException 当文章不存在时抛出
	 */
	public Article getArticleByIdOrThrow(UUID articleId) {
		return articleRepository.findByArticleId(articleId)
				.orElseThrow(() -> new ArticleNotFoundException(articleId.toString()));
	}

	/**
	 * 根据文章ID查找文章，如果不存在则抛出ArticleNotFoundException异常
	 * 
	 * @param id 文章ID
	 * @return 文章对象
	 * @throws ArticleNotFoundException 当文章不存在时抛出
	 */
	public Article getArticleByIdOrThrow(Long id) {
		return articleRepository.findById(id)
				.orElseThrow(() -> new ArticleNotFoundException(id.toString()));
	}
}