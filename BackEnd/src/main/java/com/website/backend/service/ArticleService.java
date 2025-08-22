package com.website.backend.service;

import java.util.UUID;
import com.website.backend.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ArticleService {

	/**
	 * 获取所有文章（分页）
	 * @param pageable 分页参数
	 * @return 文章分页列表
	 */
	Page<Article> getAllArticles(Pageable pageable);

	/**
	 * 根据分类获取文章（分页）
	 * @param category 文章分类
	 * @param pageable 分页参数
	 * @return 分类文章分页列表
	 */
	Page<Article> getArticlesByCategory(String category, Pageable pageable);

	/**
	 * 根据articleId获取文章
	 * @param articleId 文章ID
	 * @return 文章详情
	 */
	Article getArticleById(UUID articleId);

	/**
	 * 创建新文章
	 * @param title 文章标题
	 * @param category 文章分类
	 * @param content 文章内容
	 * @param attachment 附件
	 * @param picture 封面图片
	 * @return 创建的文章
	 */
	Article createArticle(String title, String category, String content, MultipartFile attachment,
			MultipartFile picture);

	/**
	 * 更新文章
	 * @param articleId 文章ID
	 * @param title 文章标题 
	 * @param category 文章分类
	 * @param content 文章内容
	 * @param deleteAttachment 是否删除附件
	 * @param deletePicture 是否删除封面图片
	 * @param attachment 新附件
	 * @param picture 新封面图片
	 * @return 更新后的文章
	 */
	Article updateArticle(UUID articleId, String title, String category, String content, boolean deleteAttachment,
			boolean deletePicture, MultipartFile attachment, MultipartFile picture);

	/**
	 * 删除文章
	 * @param articleId 文章ID
	 */
	void deleteArticle(UUID articleId);

}