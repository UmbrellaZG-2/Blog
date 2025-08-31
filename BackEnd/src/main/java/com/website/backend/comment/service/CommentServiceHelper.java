package com.website.backend.comment.service;

import com.website.backend.comment.entity.Comment;
import com.website.backend.comment.exception.CommentNotFoundException;
import com.website.backend.comment.repository.CommentRepository;
import org.springframework.stereotype.Component;

/**
 * 评论服务辅助类，提供通用的评论查找功能
 */
@Component
public class CommentServiceHelper {
    
    private final CommentRepository commentRepository;
    
    public CommentServiceHelper(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
    
    /**
     * 根据评论ID查找评论，如果不存在则抛出CommentNotFoundException异常
     * 
     * @param id 评论ID
     * @return 评论对象
     * @throws CommentNotFoundException 当评论不存在时抛出
     */
    public Comment getCommentByIdOrThrow(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id.toString()));
    }
    
    /**
     * 检查评论是否存在
     * 
     * @param id 评论ID
     * @return 是否存在
     */
    public boolean existsById(Long id) {
        return commentRepository.existsById(id);
    }
}