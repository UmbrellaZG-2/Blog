package com.website.backend.service.impl;

import lombok.extern.slf4j.Slf4j;
import com.website.backend.entity.Article;
import com.website.backend.entity.ArticlePicture;
import com.website.backend.repository.jpa.ArticlePictureRepository;
import com.website.backend.service.ArticlePictureService;
import com.website.backend.config.FileStorageConfig;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class ArticlePictureServiceImpl implements ArticlePictureService {

	private final ArticlePictureRepository articlePictureRepository;

	private final FileStorageConfig fileStorageConfig;

	public ArticlePictureServiceImpl(ArticlePictureRepository articlePictureRepository,
			FileStorageConfig fileStorageConfig) {
		this.articlePictureRepository = articlePictureRepository;
		this.fileStorageConfig = fileStorageConfig;
	}

	@Override
	public ArticlePicture uploadPicture(MultipartFile file, Article article) throws IOException {
		String fileName = file.getOriginalFilename();
		String contentType = file.getContentType();

		if (fileName == null || !fileName.toLowerCase().endsWith(".jpg") && !fileName.toLowerCase().endsWith(".png")) {
			throw new IOException("只支持JPG和PNG格式的图片");
		}

		if (contentType == null || !contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
			throw new IOException("文件内容类型不符合要求，只支持JPG和PNG格式的图片");
		}

		ArticlePicture picture = new ArticlePicture();
		picture.setFileName(file.getOriginalFilename());
		picture.setFileType(file.getContentType());
		picture.setFileSize(file.getSize());
		picture.setUploadTime(LocalDateTime.now());
		picture.setArticle(article);

		ArticlePicture savedPicture = articlePictureRepository.save(picture);

		String storagePath = fileStorageConfig.getArticlePictureStoragePath();

		String originalFilename = file.getOriginalFilename();
		String cleanedFileName = originalFilename != null ? StringUtils.cleanPath(originalFilename) : "";
		String uniqueFileName = savedPicture.getPictureId() + "_" + cleanedFileName;
		String filePath = storagePath + File.separator + uniqueFileName;

		try {
			File destinationFile = new File(filePath);
			file.transferTo(destinationFile);
			log.info("Image uploaded to file system: {}", filePath);

			savedPicture.setFilePath(filePath);
			return articlePictureRepository.save(savedPicture);
		}
		catch (IOException e) {
			log.error("Failed to upload image to file system: {}", e.getMessage());
			throw new IOException("Could not upload image file", e);
		}
	}

	@Override
	public byte[] downloadPicture(Long pictureId) throws IOException {
		Optional<ArticlePicture> pictureOptional = articlePictureRepository.findById(pictureId);
		if (pictureOptional.isEmpty()) {
			throw new IOException("Picture not found with id: " + pictureId);
		}

		ArticlePicture picture = pictureOptional.get();
		String filePath = picture.getFilePath();

		if (filePath == null || filePath.isEmpty()) {
			throw new IOException("Picture file path not found for id: " + pictureId);
		}

		File file = new File(filePath);
		if (!file.exists()) {
			throw new IOException("Picture file not found: " + filePath);
		}

		return Files.readAllBytes(file.toPath());
	}

	@Override
	public void deletePicture(Long pictureId) {
		Optional<ArticlePicture> pictureOptional = articlePictureRepository.findById(pictureId);
		if (pictureOptional.isPresent()) {
			ArticlePicture picture = pictureOptional.get();
			String filePath = picture.getFilePath();

			if (filePath != null && !filePath.isEmpty()) {
				File file = new File(filePath);
				if (file.exists()) {
					boolean deleted = file.delete();
					if (deleted) {
						log.info("Image deleted from file system: {}", filePath);
					}
					else {
						log.warn("Failed to delete image from file system: {}", filePath);
					}
				}
			}

			articlePictureRepository.deleteById(pictureId);
			log.info("Image information deleted from database: {}", pictureId);
		}
		else {
			log.warn("Picture not found for deletion: {}", pictureId);
		}
	}

	@Override
	public void deletePictureByArticle(Article article) {
		ArticlePicture picture = getPictureByArticle(article);
		if (picture != null) {
			String filePath = picture.getFilePath();

			if (filePath != null && !filePath.isEmpty()) {
				File file = new File(filePath);
				if (file.exists()) {
					boolean deleted = file.delete();
					if (deleted) {
						log.info("Image deleted from file system: {}", filePath);
					}
					else {
						log.warn("Failed to delete image from file system: {}", filePath);
					}
				}
			}

			articlePictureRepository.deleteByArticle(article);
			log.info("Image information deleted from database for article: {}", article.getId());
		}
		else {
			log.info("No picture found for article: {}", article.getId());
		}
	}

	@Override
	public ArticlePicture getPictureByArticle(Article article) {
		return articlePictureRepository.findByArticle(article).orElse(null);
	}

}