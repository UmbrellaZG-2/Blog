package com.website.backend.comment.service;

import com.website.backend.article.dto.CommentDTO;

import java.util.List;

public interface CommentService {
    
    List<CommentDTO> getCommentsByArticleId(Long articleId);
    
    CommentDTO createComment(CommentDTO commentDTO);
    
    CommentDTO updateComment(Long id, CommentDTO commentDTO);
    
    void deleteComment(Long id);
    
    CommentDTO getCommentById(Long id);
}