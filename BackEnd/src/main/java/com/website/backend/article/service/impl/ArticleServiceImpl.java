package com.website.backend.article.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.website.backend.article.entity.Article;
import com.website.backend.article.repository.ArticleRepository;
import com.website.backend.article.service.ArticleService;
import com.website.backend.article.service.ArticleServiceHelper;
import com.website.backend.file.service.AttachmentService;
import com.website.backend.file.service.ArticlePictureService;

import com.website.backend.file.exception.FileProcessingException;
import com.website.backend.article.exception.ArticleNotFoundException;
import com.website.backend.article.exception.DraftNotFoundException;
import com.website.backend.common.exception.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ArticleServiceImpl implements ArticleService {

	private static final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);

	private final ArticleRepository articleRepository;
	private final AttachmentService attachmentService;
	private final ArticlePictureService articlePictureService;
	private final ArticleServiceHelper articleServiceHelper;

	public ArticleServiceImpl(ArticleRepository articleRepository, 
			AttachmentService attachmentService,
			ArticlePictureService articlePictureService,
			ArticleServiceHelper articleServiceHelper) {
		this.articleRepository = articleRepository;
		this.attachmentService = attachmentService;
		this.articlePictureService = articlePictureService;
		this.articleServiceHelper = articleServiceHelper;
	}

	@Override
	public Page<Article> getAllArticles(Pageable pageable) {
		return articleRepository.findAll(pageable);
	}

	@Override
	public Page<Article> getArticlesByCategory(String category, Pageable pageable) {
		return articleRepository.findByCategory(category, pageable);
	}

	@Override
	public Article getArticleById(UUID articleId) {
		return articleServiceHelper.getArticleByIdOrThrow(articleId);
	}
	
	@Override
	public Article createArticle(String title, String category, String content, String summary,
			String tags, String status, MultipartFile coverImage, MultipartFile[] attachments) {
		// 参数验证
		if (title == null || title.trim().isEmpty()) {
			throw new ValidationException("标题不能为空");
		}
		if (category == null || category.trim().isEmpty()) {
			throw new ValidationException("分类不能为空");
		}
		if (content == null || content.trim().isEmpty()) {
			throw new ValidationException("内容不能为空");
		}

		Article article = new Article();
		article.setTitle(title);
		article.setCategory(category);
		article.setContent(content);
		article.setSummary(summary != null ? summary : "");
		article.setTags(tags != null ? tags : "");
		article.setStatus(status != null ? status : "draft");
		article.setCreateTime(LocalDateTime.now());
		article.setUpdateTime(LocalDateTime.now());
		article.setViewCount(0L);
		article.setLikeCount(0L);

		try {
			Article savedArticle = articleRepository.save(article);

			// 处理封面图片上传
			if (coverImage != null && !coverImage.isEmpty()) {
				articlePictureService.uploadPicture(coverImage, savedArticle);
				savedArticle.setHasCoverImage(true);
			}

			// 处理附件上传
			if (attachments != null && attachments.length > 0) {
				for (MultipartFile attachment : attachments) {
					if (attachment != null && !attachment.isEmpty()) {
						attachmentService.uploadAttachment(attachment, savedArticle);
						savedArticle.setHasAttachment(true);
					}
				}
			}

			return articleRepository.save(savedArticle);
		} catch (IOException e) {
			throw new FileProcessingException("文件上传失败: " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional
	public Article updateArticle(UUID articleId, String title, String category, String content, boolean deleteAttachment,
			boolean deletePicture, MultipartFile attachment, MultipartFile picture) {
		Article existingArticle = articleServiceHelper.getArticleByIdOrThrow(articleId);

		// 更新基本信息
		existingArticle.setTitle(title);
		existingArticle.setCategory(category);
		existingArticle.setContent(content);
		existingArticle.setUpdateTime(LocalDateTime.now());

		try {
			// 处理附件删除
			if (deleteAttachment) {
				if (existingArticle.getAttachmentPath() != null && !existingArticle.getAttachmentPath().isEmpty()) {
					attachmentService.deleteAttachmentsByArticle(existingArticle);
					existingArticle.setHasAttachment(false);
				}
			}

			// 处理图片删除
			if (deletePicture) {
				if (existingArticle.getPicturePath() != null && !existingArticle.getPicturePath().isEmpty()) {
					articlePictureService.deletePictureByArticle(existingArticle);
					existingArticle.setHasCoverImage(false);
				}
			}

			// 处理新附件上传
			if (attachment != null && !attachment.isEmpty()) {
				attachmentService.uploadAttachment(attachment, existingArticle);
				existingArticle.setHasAttachment(true);
			}

			// 处理新图片上传
			if (picture != null && !picture.isEmpty()) {
				articlePictureService.uploadPicture(picture, existingArticle);
				existingArticle.setHasCoverImage(true);
			}

			return articleRepository.save(existingArticle);
		} catch (IOException e) {
			throw new FileProcessingException("文件上传失败: " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteArticle(UUID articleId) {
		Article article = articleServiceHelper.getArticleByIdOrThrow(articleId);

		try {
			// 删除关联的附件
			if (article.getAttachmentPath() != null && !article.getAttachmentPath().isEmpty()) {
				attachmentService.deleteAttachmentsByArticle(article);
			}

			// 删除关联的图片
			if (article.getPicturePath() != null && !article.getPicturePath().isEmpty()) {
				articlePictureService.deletePictureByArticle(article);
			}

			articleRepository.delete(article);
		} catch (Exception e) {
			log.error("删除文章关联文件失败，文章ID: {}", articleId, e);
			throw new FileProcessingException("删除文件失败: " + e.getMessage(), e);
		}
	}

	@Override
	public Article saveDraft(Article article) {
		article.setDraft(true);
		article.setUpdateTime(LocalDateTime.now());
		if (article.getCreateTime() == null) {
			article.setCreateTime(LocalDateTime.now());
		}
		return articleRepository.save(article);
	}

	@Override
	public List<Article> getUserDrafts(Long userId) {
		return articleRepository.findByUserIdAndIsDraft(userId, true);
	}

	@Override
	public Article publishDraft(Long id) {
		Article draft = articleRepository.findById(id)
				.orElseThrow(() -> new DraftNotFoundException(id.toString()));
		draft.setDraft(false);
		draft.setUpdateTime(LocalDateTime.now());
		return articleRepository.save(draft);
	}

	@Override
	public boolean isArticleDraft(Long id) {
		return articleRepository.findById(id)
				.map(Article::isDraft)
				.orElse(false);
	}
}