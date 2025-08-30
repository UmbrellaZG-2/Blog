package com.website.backend.article.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ArticleDTO {
    private UUID id;
    private String title;
    private String content;
    private String author;
    private String authorNickname;
    private String status;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String summary;
    private String coverImage;
    private Boolean isTop;
    private Boolean isFeatured;
    
    public ArticleDTO() {}
    
    public ArticleDTO(UUID id, String title, String content, String author, String authorNickname, 
                     String status, Integer viewCount, Integer likeCount, Integer commentCount, 
                     List<String> tags, LocalDateTime createdAt, LocalDateTime updatedAt, 
                     String summary, String coverImage, Boolean isTop, Boolean isFeatured) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.authorNickname = authorNickname;
        this.status = status;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.tags = tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.summary = summary;
        this.coverImage = coverImage;
        this.isTop = isTop;
        this.isFeatured = isFeatured;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getAuthorNickname() { return authorNickname; }
    public void setAuthorNickname(String authorNickname) { this.authorNickname = authorNickname; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    
    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    
    public Boolean getIsTop() { return isTop; }
    public void setIsTop(Boolean isTop) { this.isTop = isTop; }
    
    public Boolean getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }
}