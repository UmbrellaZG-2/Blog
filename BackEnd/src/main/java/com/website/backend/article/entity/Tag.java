package com.website.backend.article.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.GeneratedValue;
import java.time.LocalDateTime;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tags")
@Data
public class Tag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String name;

	@Column(nullable = false, columnDefinition = "TIMESTAMP")
	private LocalDateTime createTime;

	@PrePersist
	protected void onCreate() {
		createTime = LocalDateTime.now();
	}

	// 手动添加getter和setter方法
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}
	
	public Long getId() {
		return id;
	}
}