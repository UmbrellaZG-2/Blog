package com.website.backend.article.repository;

import com.website.backend.article.entity.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    
    /**
     * 根据文章ID和用户ID删除点赞记录
     * @param articleId 文章ID
     * @param userId 用户ID
     */
    void deleteByArticleIdAndUserId(Long articleId, Long userId);
    
    /**
     * 统计文章的点赞数
     * @param articleId 文章ID
     * @return 点赞数
     */
    long countByArticleId(Long articleId);
    
    /**
     * 检查用户是否已点赞文章
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean existsByArticleIdAndUserId(Long articleId, Long userId);
    
    /**
     * 查询用户点赞的文章数量
     * @param userId 用户ID
     * @return 点赞数量
     */
    @Query("SELECT COUNT(al) FROM ArticleLike al WHERE al.userId = ?1")
    long countByUserId(Long userId);
}