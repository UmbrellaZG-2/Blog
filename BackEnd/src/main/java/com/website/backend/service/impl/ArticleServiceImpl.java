package com.website.backend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.website.backend.entity.Article;
import com.website.backend.repository.jpa.ArticleRepository;
import com.website.backend.service.ArticleService;
import com.website.backend.service.AttachmentService;
import com.website.backend.service.ArticlePictureService;
import com.website.backend.exception.ResourceNotFoundException;
import com.website.backend.exception.FileUploadException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ArticleServiceImpl implements ArticleService {

	private static final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);

	private final ArticleRepository articleRepository;

	private final AttachmentService attachmentService;

	private final ArticlePictureService articlePictureService;

	public ArticleServiceImpl(ArticleRepository articleRepository, AttachmentService attachmentService,
			ArticlePictureService articlePictureService) {
		this.articleRepository = articleRepository;
		this.attachmentService = attachmentService;
		this.articlePictureService = articlePictureService;
	}

	@Override
	public Page<Article> getAllArticles(Pageable pageable) {
		log.info("获取所有文章，分页参数: {}", pageable);
		return articleRepository.findAll(pageable);
	}

	@Override
	public Page<Article> getArticlesByCategory(String category, Pageable pageable) {
		log.info("获取分类 [{}] 的文章，分页参数: {}", category, pageable);
		return articleRepository.findByCategory(category, pageable);
	}

	@Override
	public Article getArticleById(UUID articleId) {
		log.info("获取文章，articleId: {}", articleId);
		return articleRepository.findByArticleId(articleId).orElseThrow(() -> new ResourceNotFoundException("文章不存在，articleId: " + articleId));
	}

	@Override
	public Article createArticle(String title, String category, String content, MultipartFile attachment,
			MultipartFile picture) {
		log.info("创建新文章，标题: {}", title);

		if (title == null || title.trim().isEmpty()) {
			throw new IllegalArgumentException("文章标题不能为空");
		}
		if (category == null || category.trim().isEmpty()) {
			throw new IllegalArgumentException("文章分类不能为空");
		}
		if (content == null || content.trim().isEmpty()) {
			throw new IllegalArgumentException("文章内容不能为空");
		}

		Article article = new Article(title, content, category);

		Article savedArticle = articleRepository.save(article);
		log.info("文章保存成功，ID: {}, articleId: {}", savedArticle.getId(), savedArticle.getArticleId());

		if (attachment != null && !attachment.isEmpty()) {
			try {
				log.info("开始上传文章附件，文章ID: {}", savedArticle.getId());
				attachmentService.uploadAttachment(attachment, savedArticle);
				savedArticle.setHasAttachment(true);
				savedArticle = articleRepository.save(savedArticle);
				log.info("文章附件上传成功");
			}
			catch (IOException e) {
				log.error("文章附件上传失败: {}", e.getMessage());
				throw new FileUploadException("附件上传失败: " + e.getMessage());
			}
		}

		if (picture != null && !picture.isEmpty()) {
			try {
				log.info("开始上传文章封面图片，文章ID: {}", savedArticle.getId());
				articlePictureService.uploadPicture(picture, savedArticle);
				savedArticle.setHasCoverImage(true);
				savedArticle = articleRepository.save(savedArticle);
				log.info("文章封面图片上传成功");
			}
			catch (IOException e) {
				log.error("文章封面图片上传失败: {}", e.getMessage());
				throw new FileUploadException("封面图片上传失败: " + e.getMessage());
			}
		}

		return savedArticle;
	}

	@Override
	public Article updateArticle(UUID articleId, String title, String category, String content, boolean deleteAttachment,
			boolean deletePicture, MultipartFile attachment, MultipartFile picture) {
		log.info("更新文章，articleId: {}", articleId);

		Article article = getArticleById(articleId);

		if (title != null && !title.trim().isEmpty()) {
			article.setTitle(title);
		}
		if (category != null && !category.trim().isEmpty()) {
			article.setCategory(category);
		}
		if (content != null && !content.trim().isEmpty()) {
			article.setContent(content);
		}
		article.setUpdateTime(LocalDateTime.now());

		if (deleteAttachment && article.isHasAttachment()) {
			log.info("删除文章附件，文章ID: {}", article.getId());
			attachmentService.deleteAttachmentsByArticle(article);
			article.setHasAttachment(false);
		}

		if (deletePicture && article.isHasCoverImage()) {
			log.info("删除文章封面图片，文章ID: {}", article.getId());
			articlePictureService.deletePictureByArticle(article);
			article.setHasCoverImage(false);
		}

		if (attachment != null && !attachment.isEmpty()) {
			try {
				log.info("上传新附件，文章ID: {}", article.getId());
				if (article.isHasAttachment()) {
					attachmentService.deleteAttachmentsByArticle(article);
				}
				attachmentService.uploadAttachment(attachment, article);
				article.setHasAttachment(true);
			}
			catch (IOException e) {
				log.error("附件上传失败: {}", e.getMessage());
				throw new FileUploadException("附件上传失败: " + e.getMessage());
			}
		}

		if (picture != null && !picture.isEmpty()) {
			try {
				log.info("上传新封面图片，文章ID: {}", article.getId());
				if (article.isHasCoverImage()) {
					articlePictureService.deletePictureByArticle(article);
				}
				articlePictureService.uploadPicture(picture, article);
				article.setHasCoverImage(true);
			}
			catch (IOException e) {
				log.error("封面图片上传失败: {}", e.getMessage());
				throw new FileUploadException("封面图片上传失败: " + e.getMessage());
			}
		}

		Article updatedArticle = articleRepository.save(article);
		log.info("文章更新成功，ID: {}", updatedArticle.getId());
		return updatedArticle;
	}

	@Override
	public void deleteArticle(UUID articleId) {
		log.info("删除文章，articleId: {}", articleId);

		Article article = getArticleById(articleId);

		if (article.isHasAttachment()) {
			log.info("删除文章附件，文章ID: {}", article.getId());
			attachmentService.deleteAttachmentsByArticle(article);
		}

		if (article.isHasCoverImage()) {
			log.info("删除文章封面图片，文章ID: {}", article.getId());
			articlePictureService.deletePictureByArticle(article);
		}

		articleRepository.delete(article);
		log.info("文章删除成功，articleId: {}", articleId);
	}

}