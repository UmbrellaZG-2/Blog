package com.website.backend.file.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import com.website.backend.article.entity.Article;

@Entity
@Table(name = "attachments")
@Data
public class Attachment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long attachmentId;

	@Column(length = 255, nullable = false)
	private String fileName;

	@Column(length = 255, nullable = false)
	private String filePath;

	@Column(length = 100)
	private String fileType;

	private Long fileSize;

	@Column(name = "upload_time", columnDefinition = "TIMESTAMP")
	private LocalDateTime uploadTime;

	@ManyToOne
	@JoinColumn(name = "article_id", nullable = false)
	private Article article;
	
	// 下载次数字段，不直接存储在数据库中，而是通过Redis获取
	@Transient
	private Long downloadCount = 0L;

	public Attachment() {
	}
	
	// Getter和Setter方法
	public Long getAttachmentId() {
		return attachmentId;
	}
	
	public void setAttachmentId(Long attachmentId) {
		this.attachmentId = attachmentId;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getFileType() {
		return fileType;
	}
	
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	public Long getFileSize() {
		return fileSize;
	}
	
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	
	public LocalDateTime getUploadTime() {
		return uploadTime;
	}
	
	public void setUploadTime(LocalDateTime uploadTime) {
		this.uploadTime = uploadTime;
	}
	
	public Article getArticle() {
		return article;
	}
	
	public void setArticle(Article article) {
		this.article = article;
	}
	
	public Long getDownloadCount() {
		return downloadCount;
	}
	
	public void setDownloadCount(Long downloadCount) {
		this.downloadCount = downloadCount;
	}
}