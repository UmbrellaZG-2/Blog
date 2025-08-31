package com.website.backend.article.service;

import java.util.List;

/**
 * 点赞服务接口
 * 提供文章点赞、取消点赞和获取点赞数等功能
 */
public interface LikeService {
    
    /**
     * 点赞文章
     * @param articleId 文章ID
     * @param userId 用户ID（可以为null，表示匿名用户）
     * @return 当前点赞数
     */
    long likeArticle(Long articleId, Long userId);
    
    /**
     * 取消点赞文章
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 当前点赞数
     */
    long unlikeArticle(Long articleId, Long userId);
    
    /**
     * 获取文章点赞数
     * @param articleId 文章ID
     * @return 点赞数
     */
    long getArticleLikeCount(Long articleId);
    
    /**
     * 检查用户是否已点赞文章
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean hasUserLikedArticle(Long articleId, Long userId);
    
    /**
     * 获取用户点赞的文章列表
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 文章ID列表
     */
    List<Long> getUserLikedArticles(Long userId, int limit);
}