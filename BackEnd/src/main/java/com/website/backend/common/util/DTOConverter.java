package com.website.backend.common.util;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.website.backend.article.dto.ArticleDTO;
import com.website.backend.article.entity.Article;
import com.website.backend.file.entity.ArticlePicture;
import com.website.backend.file.entity.Attachment;
import com.website.backend.file.repository.ArticlePictureRepository;
import com.website.backend.file.repository.AttachmentRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class DTOConverter {

	private final ModelMapper modelMapper;
	private final ArticlePictureRepository articlePictureRepository;
	private final AttachmentRepository attachmentRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	
	// 附件下载次数的Redis键前缀
	private static final String ATTACHMENT_DOWNLOAD_COUNT_PREFIX = "attachment:download:count:";

	@Autowired
	public DTOConverter(ArticlePictureRepository articlePictureRepository, 
	                   AttachmentRepository attachmentRepository,
	                   RedisTemplate<String, Object> redisTemplate) {
		this.modelMapper = new ModelMapper();
		this.articlePictureRepository = articlePictureRepository;
		this.attachmentRepository = attachmentRepository;
		this.redisTemplate = redisTemplate;
	}

	public ArticleDTO convertToDTO(Article article) {
		ArticleDTO dto = modelMapper.map(article, ArticleDTO.class);
		// 手动映射时间字段，因为实体和DTO字段名不同
		dto.setCreatedAt(article.getCreateTime());
		dto.setUpdatedAt(article.getUpdateTime());
		
		// 处理封面图片URL
		if (article.isHasCoverImage()) {
			Optional<ArticlePicture> coverPicture = articlePictureRepository.findByArticle(article);
			if (coverPicture.isPresent()) {
				// 设置封面图片的下载URL
				dto.setCoverImage("/images/article/" + article.getId() + "/cover/download");
			}
		}
		
		// 处理附件
		if (article.isHasAttachment()) {
			List<Attachment> attachments = attachmentRepository.findByArticle(article);
			// 为每个附件设置下载次数
			for (Attachment attachment : attachments) {
				String downloadCountKey = ATTACHMENT_DOWNLOAD_COUNT_PREFIX + attachment.getAttachmentId();
				Object countObj = redisTemplate.opsForValue().get(downloadCountKey);
				// 如果Redis中没有记录，则默认为0
				long downloadCount = 0L;
				if (countObj != null) {
					// 处理可能的Integer或Long类型
					if (countObj instanceof Integer) {
						downloadCount = ((Integer) countObj).longValue();
					} else if (countObj instanceof Long) {
						downloadCount = (Long) countObj;
					}
				}
				attachment.setDownloadCount(downloadCount);
			}
			dto.setAttachments(attachments);
		}
		
		return dto;
	}

	public Article convertToEntity(ArticleDTO dto) {
		return modelMapper.map(dto, Article.class);
	}

}