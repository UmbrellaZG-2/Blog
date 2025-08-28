package com.website.backend.comment.controller;

import com.website.backend.article.dto.CommentDTO;
import com.website.backend.comment.service.CommentService;
import com.website.backend.common.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/article/{articleId}")
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getCommentsByArticleId(@PathVariable Long articleId) {
        List<CommentDTO> comments = commentService.getCommentsByArticleId(articleId);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommentDTO>> createComment(@RequestBody CommentDTO commentDTO) {
        CommentDTO createdComment = commentService.createComment(commentDTO);
        return ResponseEntity.ok(ApiResponse.success(createdComment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentDTO>> updateComment(
            @PathVariable Long id,
            @RequestBody CommentDTO commentDTO) {
        CommentDTO updatedComment = commentService.updateComment(id, commentDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedComment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentDTO>> getCommentById(@PathVariable Long id) {
        CommentDTO comment = commentService.getCommentById(id);
        return ResponseEntity.ok(ApiResponse.success(comment));
    }
}