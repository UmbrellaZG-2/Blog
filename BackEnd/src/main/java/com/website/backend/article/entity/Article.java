package com.website.backend.article.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "articles")
@Data
@Entity
public class Article {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, unique = true)
	private Long id;

	@Column(name = "article_id", nullable = false, unique = true, updatable = false)
	private UUID articleId;

	@Column(name = "user_id")
	private Long userId;

	@Column(length = 100)
	private String title;

	@Lob
	@Column(columnDefinition = "TEXT")
	private String content;

	private String category;

	@Column(name = "add_attach")
	private boolean hasAttachment = false;

	@Column(name = "add_picture")
	private boolean hasCoverImage = false;

	@Column(name = "is_draft")
	private boolean isDraft = false;

	private LocalDateTime createTime;

	private LocalDateTime updateTime;
	
	private Long viewCount;
	
	private Long likeCount;
	
	private String attachmentPath;
	
	private String picturePath;

	public Article() {
		this.articleId = UUID.randomUUID();
	}

	public Article(String title, String content, String category) {
		this.articleId = UUID.randomUUID();
		this.title = title;
		this.content = content;
		this.category = category;
		this.createTime = LocalDateTime.now();
		this.updateTime = LocalDateTime.now();
	}

	@PrePersist
	protected void onCreate() {
		createTime = LocalDateTime.now();
		updateTime = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updateTime = LocalDateTime.now();
	}

	// 手动添加getter和setter方法
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public UUID getArticleId() {
		return articleId;
	}
	
	public void setArticleId(UUID articleId) {
		this.articleId = articleId;
	}
	
	public boolean isHasAttachment() {
		return hasAttachment;
	}
	
	public void setHasAttachment(boolean hasAttachment) {
		this.hasAttachment = hasAttachment;
	}
	
	public boolean isHasCoverImage() {
		return hasCoverImage;
	}
	
	public void setHasCoverImage(boolean hasCoverImage) {
		this.hasCoverImage = hasCoverImage;
	}
	
	public boolean isDraft() {
		return isDraft;
	}
	
	public void setDraft(boolean isDraft) {
		this.isDraft = isDraft;
	}
	
	public Long getViewCount() {
		return viewCount;
	}
	
	public void setViewCount(Long viewCount) {
		this.viewCount = viewCount;
	}
	
	public Long getLikeCount() {
		return likeCount;
	}
	
	public void setLikeCount(Long likeCount) {
		this.likeCount = likeCount;
	}
	
	public String getAttachmentPath() {
		return attachmentPath;
	}
	
	public void setAttachmentPath(String attachmentPath) {
		this.attachmentPath = attachmentPath;
	}
	
	public String getPicturePath() {
		return picturePath;
	}
	
	public void setPicturePath(String picturePath) {
		this.picturePath = picturePath;
	}
}