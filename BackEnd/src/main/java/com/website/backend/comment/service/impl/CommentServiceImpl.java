package com.website.backend.comment.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.website.backend.article.dto.CommentDTO;
import com.website.backend.comment.entity.Comment;
import com.website.backend.comment.repository.CommentRepository;
import com.website.backend.comment.service.CommentService;
import com.website.backend.comment.service.CommentServiceHelper;

import com.website.backend.comment.exception.CommentNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentServiceHelper commentServiceHelper;

    public CommentServiceImpl(CommentRepository commentRepository, CommentServiceHelper commentServiceHelper) {
        this.commentRepository = commentRepository;
        this.commentServiceHelper = commentServiceHelper;
    }

    @Override
    public List<CommentDTO> getCommentsByArticleId(Long articleId) {
        return commentRepository.findByArticleId(articleId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDTO createComment(CommentDTO commentDTO) {
        Comment comment = convertToEntity(commentDTO);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        return convertToDTO(savedComment);
    }

    @Override
    public CommentDTO updateComment(Long id, CommentDTO commentDTO) {
        Comment existingComment = commentServiceHelper.getCommentByIdOrThrow(id);
        
        existingComment.setContent(commentDTO.getContent());
        existingComment.setNickname(commentDTO.getAuthor());
        existingComment.setUpdateTime(LocalDateTime.now());
        
        Comment updatedComment = commentRepository.save(existingComment);
        return convertToDTO(updatedComment);
    }

    @Override
    public void deleteComment(Long id) {
        if (!commentServiceHelper.existsById(id)) {
            throw new CommentNotFoundException(id.toString());
        }
        commentRepository.deleteById(id);
    }

    @Override
    public CommentDTO getCommentById(Long id) {
        Comment comment = commentServiceHelper.getCommentByIdOrThrow(id);
        return convertToDTO(comment);
    }

    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setArticleId(comment.getArticleId());
        dto.setParentId(comment.getParentId());
        dto.setAuthor(comment.getNickname());
        dto.setContent(comment.getContent());
        dto.setCreateTime(comment.getCreateTime());
        dto.setIpAddress(comment.getIpAddress());
        return dto;
    }

    private Comment convertToEntity(CommentDTO dto) {
        Comment comment = new Comment();
        comment.setId(dto.getId());
        comment.setArticleId(dto.getArticleId());
        comment.setParentId(dto.getParentId());
        comment.setNickname(dto.getAuthor());
        comment.setContent(dto.getContent());
        comment.setIpAddress(dto.getIpAddress());
        return comment;
    }
}