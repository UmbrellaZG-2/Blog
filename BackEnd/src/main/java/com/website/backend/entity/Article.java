package com.website.backend.entity;

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
}