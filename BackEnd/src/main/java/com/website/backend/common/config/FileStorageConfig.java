package com.website.backend.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;

@Configuration
public class FileStorageConfig {

	@Value("${file.storage.path}")
	private String fileStoragePath;

	public String getFileStoragePath() {
		try {
			File file = ResourceUtils.getFile(fileStoragePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			return file.getAbsolutePath();
		}
		catch (IOException e) {
			throw new RuntimeException("Could not resolve file storage path", e);
		}
	}

	public String getArticlePictureStoragePath() {
		String path = getFileStoragePath() + File.separator + "article_pictures";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	public String getAttachmentStoragePath() {
		String path = getFileStoragePath() + File.separator + "attachments";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

}