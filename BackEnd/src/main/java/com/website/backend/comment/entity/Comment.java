package com.website.backend.comment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import java.time.LocalDateTime;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 评论实体类，对应文章评论表
 */
@Entity
@Table(name = "comments")
@Data
public class Comment {

	/** 主键ID */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** 所属文章ID */
	@Column(nullable = false)
	private Long articleId;

	/** 父评论ID（顶级评论为null）*/
	private Long parentId;

	/** 昵称 */
	@Column(nullable = false, length = 100)
	private String nickname;

	/** 评论内容 */
	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	/** 创建时间 */
	@Column(nullable = false, columnDefinition = "TIMESTAMP")
	private LocalDateTime createTime;

	/** 评论者IP地址 */
	@Column(length = 50)
	private String ipAddress;
	
	/** 更新时间 */
	@Column(name = "update_time", columnDefinition = "TIMESTAMP")
	private LocalDateTime updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getArticleId() {
		return articleId;
	}

	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}
	
	public Long getParentId() {
		return parentId;
	}
	
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	/**
	 * 重写toString方法，用于显示评论的层级结构
	 * 
	 * @return 包含层级缩进的字符串表示
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Comment{");
		sb.append("id=").append(id);
		sb.append(", parentId=").append(parentId);
		sb.append(", articleId=").append(articleId);
		sb.append(", nickname='").append(nickname).append('\'');
		sb.append(", content='").append(content).append('\'');
		sb.append(", createTime=").append(createTime);
		sb.append(", updateTime=").append(updateTime);
		sb.append(", ipAddress='").append(ipAddress).append('\'');
		sb.append('}');
		return sb.toString();
	}
}