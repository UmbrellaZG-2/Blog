package com.website.backend.file.service.impl;

import lombok.extern.slf4j.Slf4j;
import com.website.backend.common.config.FileStorageConfig;
import com.website.backend.file.service.FileOperationHelper;
import org.springframework.util.StringUtils;
import com.website.backend.article.entity.Article;
import com.website.backend.file.entity.Attachment;
import com.website.backend.system.entity.SystemConfig;
import com.website.backend.file.repository.AttachmentRepository;
import com.website.backend.system.repository.SystemConfigRepository;
import com.website.backend.file.service.AttachmentService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Optional;

@Slf4j
@Service
public class AttachmentServiceImpl implements AttachmentService {

	private final AttachmentRepository attachmentRepository;

	private final FileStorageConfig fileStorageConfig;

	private final SystemConfigRepository systemConfigRepository;
	
	private final FileOperationHelper fileOperationHelper;

	public AttachmentServiceImpl(AttachmentRepository attachmentRepository, FileStorageConfig fileStorageConfig,
			SystemConfigRepository systemConfigRepository, FileOperationHelper fileOperationHelper) {
		this.attachmentRepository = attachmentRepository;
		this.fileStorageConfig = fileStorageConfig;
		this.systemConfigRepository = systemConfigRepository;
		this.fileOperationHelper = fileOperationHelper;
	}

	@Override
	public Attachment uploadAttachment(MultipartFile file, Article article) throws IOException {
		int maxAttachCount = getMaxAttachmentCount();
		int currentAttachCount = getCurrentAttachmentCount(article);

		if (currentAttachCount >= maxAttachCount) {
			log.warn("文章附件数量超过限制，文章ID: {}, 当前数量: {}, 限制数量: {}", article.getId(), currentAttachCount, maxAttachCount);
			throw new IOException("附件数量超过限制，最多可上传" + maxAttachCount + "个附件");
		}

		String fileName = file.getOriginalFilename();

		if (fileName == null) {
			throw new IOException("文件名不能为空");
		}

		String lowerFileName = fileName.toLowerCase();
		if (!lowerFileName.endsWith(".7z") && !lowerFileName.endsWith(".zip") && !lowerFileName.endsWith(".rar")) {
			throw new IOException("只支持7Z、ZIP和RAR格式的压缩文件");
		}

		log.debug("开始上传附件，文件名: {}, 文章ID: {}", fileName, article.getId());
		String fileType = file.getContentType();
		Long fileSize = file.getSize();

		String storagePath = fileStorageConfig.getAttachmentStoragePath();

		String uniqueFileName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(fileName);
		String filePath = storagePath + File.separator + uniqueFileName;

		try {
			File destinationFile = new File(filePath);
			file.transferTo(destinationFile);
			log.debug("附件上传到文件系统成功: {}", filePath);
		}
		catch (IOException e) {
			log.error("附件上传到文件系统失败: {}", e.getMessage());
			throw new IOException("Could not upload attachment file", e);
		}

		Attachment attachment = new Attachment();
		attachment.setFileName(fileName);
		attachment.setFilePath(filePath);
		attachment.setFileType(fileType);
		attachment.setFileSize(fileSize);
		attachment.setUploadTime(LocalDateTime.now());
		attachment.setArticle(article);

		Attachment savedAttachment = attachmentRepository.save(attachment);
		log.debug("附件信息保存到数据库成功，附件ID: {}", savedAttachment.getAttachmentId());
		return savedAttachment;
	}

	@Override
	public byte[] downloadAttachment(Long attachmentId) throws IOException {
		log.debug("开始下载附件，附件ID: {}", attachmentId);
		Attachment attachment = attachmentRepository.findById(attachmentId).orElseThrow(() -> {
			log.error("附件不存在，附件ID: {}", attachmentId);
			return new RuntimeException("Attachment not found");
		});

		String filePath = attachment.getFilePath();
		return fileOperationHelper.readFileContent(filePath);
	}

	@Override
	public void deleteAttachment(Long attachmentId) {
		log.debug("开始删除附件，附件ID: {}", attachmentId);
		Attachment attachment = attachmentRepository.findById(attachmentId).orElseThrow(() -> {
			log.error("附件不存在，附件ID: {}", attachmentId);
			return new RuntimeException("Attachment not found");
		});

		String filePath = attachment.getFilePath();
		fileOperationHelper.deleteFile(filePath);

		attachmentRepository.delete(attachment);
		log.debug("从数据库删除附件记录成功,附件ID: {}", attachmentId);
	}

	private int getMaxAttachmentCount() {
		Optional<SystemConfig> configOptional = systemConfigRepository.findByConfigKey("MaxAttachCount");
		if (configOptional.isPresent()) {
			try {
				return Integer.parseInt(configOptional.get().getConfigValue());
			}
			catch (NumberFormatException e) {
				log.error("MaxAttachCount配置值不是有效数字: {}", configOptional.get().getConfigValue());
			}
		}
		else {
			log.warn("未找到MaxAttachCount配置，使用默认值: 5");
		}
		return 5;
	}

	private int getCurrentAttachmentCount(Article article) {
		return attachmentRepository.countByArticle(article);
	}

	@Override
	public void deleteAttachmentsByArticle(Article article) {
		log.info("开始删除文章相关的所有附件，文章ID: {}", article.getId());
		java.util.List<Attachment> attachments = attachmentRepository.findByArticle(article);
		log.info("找到{}个与文章相关的附件", attachments.size());

		for (Attachment attachment : attachments) {
			String filePath = attachment.getFilePath();
			fileOperationHelper.deleteFile(filePath);

			attachmentRepository.delete(attachment);
			log.info("从数据库删除附件记录成功,附件ID: {}", attachment.getAttachmentId());
		}

		log.info("从数据库删除文章相关的所有附件记录成功，文章ID: {}", article.getId());
	}

}