package com.website.backend.file.service.impl;

import lombok.extern.slf4j.Slf4j;
import com.website.backend.common.config.FileStorageConfig;
import com.website.backend.file.service.FileOperationHelper;
import org.springframework.util.StringUtils;
import com.website.backend.article.entity.Article;
import com.website.backend.file.entity.ArticlePicture;
import com.website.backend.file.repository.ArticlePictureRepository;
import com.website.backend.file.service.ArticlePictureService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ArticlePictureServiceImpl implements ArticlePictureService {

	private final ArticlePictureRepository articlePictureRepository;
	private final FileStorageConfig fileStorageConfig;
	private final FileOperationHelper fileOperationHelper;

	public ArticlePictureServiceImpl(ArticlePictureRepository articlePictureRepository,
			FileStorageConfig fileStorageConfig, FileOperationHelper fileOperationHelper) {
		this.articlePictureRepository = articlePictureRepository;
		this.fileStorageConfig = fileStorageConfig;
		this.fileOperationHelper = fileOperationHelper;
	}

	@Override
	@Transactional
	public ArticlePicture uploadPicture(MultipartFile file, Article article) throws IOException {
		String fileName = file.getOriginalFilename();
		if (fileName == null) {
			throw new IOException("文件名不能为空");
		}

		String lowerFileName = fileName.toLowerCase();
		if (!lowerFileName.endsWith(".jpg") && !lowerFileName.endsWith(".jpeg") && !lowerFileName.endsWith(".png")
				&& !lowerFileName.endsWith(".gif") && !lowerFileName.endsWith(".webp")) {
			throw new IOException("只支持JPG、JPEG、PNG、GIF和WEBP格式的图片文件");
		}

		log.debug("开始上传文章图片，文件名: {}, 文章ID: {}", fileName, article.getId());
		String fileType = file.getContentType();
		Long fileSize = file.getSize();

		String storagePath = fileStorageConfig.getArticlePictureStoragePath();
		String uniqueFileName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(fileName);
		String filePath = storagePath + File.separator + uniqueFileName;

		try {
			File destinationFile = new File(filePath);
			file.transferTo(destinationFile);
			log.debug("文章图片上传到文件系统成功: {}", filePath);
		}
		catch (IOException e) {
			log.error("文章图片上传到文件系统失败: {}", e.getMessage());
			throw new IOException("Could not upload picture file", e);
		}

		ArticlePicture picture = new ArticlePicture();
		picture.setFileName(fileName);
		picture.setFilePath(filePath);
		picture.setFileType(fileType);
		picture.setFileSize(fileSize);
		picture.setUploadTime(LocalDateTime.now());
		picture.setArticle(article);

		ArticlePicture savedPicture = articlePictureRepository.save(picture);
		log.debug("文章图片信息保存到数据库成功，图片ID: {}", savedPicture.getPictureId());
		return savedPicture;
	}

	@Override
	@Transactional
	public byte[] downloadPicture(Long pictureId) throws IOException {
		log.debug("开始下载文章图片，图片ID: {}", pictureId);
		Optional<ArticlePicture> pictureOptional = articlePictureRepository.findById(pictureId);
		if (!pictureOptional.isPresent()) {
			log.error("文章图片不存在，图片ID: {}", pictureId);
			throw new RuntimeException("Article picture not found");
		}

		String filePath = pictureOptional.get().getFilePath();
		return fileOperationHelper.readFileContent(filePath);
	}

	public byte[] getPicture(Long pictureId) throws IOException {
		return downloadPicture(pictureId);
	}

	@Override
	@Transactional
	public void deletePicture(Long pictureId) {
		log.debug("开始删除文章图片，图片ID: {}", pictureId);
		Optional<ArticlePicture> pictureOptional = articlePictureRepository.findById(pictureId);
		if (!pictureOptional.isPresent()) {
			log.error("文章图片不存在，图片ID: {}", pictureId);
			throw new RuntimeException("Article picture not found");
		}

		String filePath = pictureOptional.get().getFilePath();
		fileOperationHelper.deleteFile(filePath);

		articlePictureRepository.delete(pictureOptional.get());
		log.debug("从数据库删除文章图片记录成功，图片ID: {}", pictureId);
	}

	@Override
	@Transactional
	public void deletePictureByArticle(Article article) {
		log.info("开始删除文章相关的图片，文章ID: {}", article.getId());
		Optional<ArticlePicture> pictureOptional = articlePictureRepository.findByArticle(article);

		if (pictureOptional.isPresent()) {
			String filePath = pictureOptional.get().getFilePath();
			fileOperationHelper.deleteFile(filePath);

			articlePictureRepository.delete(pictureOptional.get());
			log.info("从数据库删除文章图片记录成功，图片ID: {}", pictureOptional.get().getPictureId());
		}
		else {
			log.warn("未找到文章相关的图片，文章ID: {}", article.getId());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ArticlePicture getPictureByArticle(Article article) {
		log.debug("根据文章获取图片，文章ID: {}", article.getId());
		Optional<ArticlePicture> pictureOptional = articlePictureRepository.findByArticle(article);
		return pictureOptional.orElse(null);
	}
}