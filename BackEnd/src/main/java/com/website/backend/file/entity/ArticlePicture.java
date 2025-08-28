package com.website.backend.file.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import com.website.backend.article.entity.Article;

@Entity
@Table(name = "article_pictures")
@Data
public class ArticlePicture {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long pictureId;

	@Column(length = 255, nullable = false)
	private String fileName;

	@Column(length = 255, nullable = false)
	private String filePath;

	@Column(length = 100)
	private String fileType;

	private Long fileSize;

	@Column(name = "upload_time", columnDefinition = "TIMESTAMP")
	private LocalDateTime uploadTime;

	@OneToOne
	@JoinColumn(name = "article_id", nullable = false)
	private Article article;

	@Column(name = "is_cover", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private Boolean isCover = false;

	public ArticlePicture() {
	}

	public Boolean getIsCover() {
		return isCover;
	}

	public void setIsCover(Boolean isCover) {
		this.isCover = isCover;
	}
	
	public Long getPictureId() {
		return pictureId;
	}
	
	public void setPictureId(Long pictureId) {
		this.pictureId = pictureId;
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
}