package com.website.backend.article.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import com.website.backend.article.entity.Article;
import com.website.backend.common.model.ApiResponse;
import com.website.backend.article.service.ArticleService;
import com.website.backend.article.service.LikeService;

import java.util.List;
import java.util.Map;

/**
 * 文章功能控制�? * 提供草稿功能和点赞功能的API端点
 */
@RestController
@RequestMapping("/api/articles")
public class ArticleFeatureController {
    
    private static final Logger log = LoggerFactory.getLogger(ArticleFeatureController.class);
    
    private final ArticleService articleService;
    private final LikeService likeService;
    
    public ArticleFeatureController(ArticleService articleService, LikeService likeService) {
        this.articleService = articleService;
        this.likeService = likeService;
    }
    
    // ----------------- 草稿功能 -----------------
    
    @PostMapping("/draft")
    public ApiResponse<Article> saveDraft(@RequestBody Article article) {
        log.info("保存文章草稿");
        Article savedDraft = articleService.saveDraft(article);
        return ApiResponse.success(savedDraft);
    }
    
    @GetMapping("/drafts/{userId}")
    public ApiResponse<List<Article>> getUserDrafts(@PathVariable Long userId) {
        log.info("获取用户草稿，用户ID: {}", userId);
        List<Article> drafts = articleService.getUserDrafts(userId);
        return ApiResponse.success(drafts);
    }
    
    @PostMapping("/draft/{id}/publish")
    public ApiResponse<Article> publishDraft(@PathVariable Long id) {
        log.info("发布文章草稿，ID: {}", id);
        Article publishedArticle = articleService.publishDraft(id);
        return ApiResponse.success(publishedArticle);
    }
    
    @GetMapping("/draft/check/{id}")
    public ApiResponse<Boolean> isArticleDraft(@PathVariable Long id) {
        log.info("检查文章是否为草稿，ID: {}", id);
        boolean isDraft = articleService.isArticleDraft(id);
        return ApiResponse.success(isDraft);
    }
    
    // ----------------- 点赞功能 -----------------
    
    @PostMapping("/{articleId}/like")
    public ApiResponse<Long> likeArticle(
            @PathVariable Long articleId, 
            @RequestBody Map<String, Object> request) {
        Long userId = request.containsKey("userId") ? ((Number) request.get("userId")).longValue() : null;
        log.info("点赞文章，articleId: {}, userId: {}", articleId, userId);
        long likeCount = likeService.likeArticle(articleId, userId);
        return ApiResponse.success(likeCount);
    }
    
    @PostMapping("/{articleId}/unlike")
    public ApiResponse<Long> unlikeArticle(
            @PathVariable Long articleId, 
            @RequestBody Map<String, Object> request) {
        Long userId = request.containsKey("userId") ? ((Number) request.get("userId")).longValue() : null;
        log.info("取消点赞文章，articleId: {}, userId: {}", articleId, userId);
        long likeCount = likeService.unlikeArticle(articleId, userId);
        return ApiResponse.success(likeCount);
    }
    
    @GetMapping("/{articleId}/likes")
    public ApiResponse<Long> getArticleLikeCount(@PathVariable Long articleId) {
        log.info("获取文章点赞数，articleId: {}", articleId);
        long likeCount = likeService.getArticleLikeCount(articleId);
        return ApiResponse.success(likeCount);
    }
    
    @GetMapping("/{articleId}/like/check")
    public ApiResponse<Boolean> hasUserLikedArticle(
            @PathVariable Long articleId, 
            @RequestParam Long userId) {
        log.info("检查用户是否已点赞文章，articleId: {}, userId: {}", articleId, userId);
        boolean hasLiked = likeService.hasUserLikedArticle(articleId, userId);
        return ApiResponse.success(hasLiked);
    }
    
    @GetMapping("/user/{userId}/likes")
    public ApiResponse<List<Long>> getUserLikedArticles(
            @PathVariable Long userId, 
            @RequestParam(defaultValue = "10") int limit) {
        log.info("获取用户点赞的文章列表，userId: {}, limit: {}", userId, limit);
        List<Long> articleIds = likeService.getUserLikedArticles(userId, limit);
        return ApiResponse.success(articleIds);
    }
}