package com.website.backend.article.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CommentDTO {
    private Long id;
    private Long articleId;
    private Long parentId;
    private String author;
    private String content;
    private LocalDateTime createTime;
    private String ipAddress;
    private List<CommentDTO> replies;
    
    public CommentDTO() {}
    
    public CommentDTO(Long id, Long articleId, Long parentId, String author, 
                     String content, LocalDateTime createTime, String ipAddress) {
        this.id = id;
        this.articleId = articleId;
        this.parentId = parentId;
        this.author = author;
        this.content = content;
        this.createTime = createTime;
        this.ipAddress = ipAddress;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }
    
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public void setNickname(String nickname) { this.author = nickname; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public List<CommentDTO> getReplies() { return replies; }
    public void setReplies(List<CommentDTO> replies) { this.replies = replies; }
}