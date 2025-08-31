package com.website.backend.article.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.website.backend.article.entity.ArticleLike;
import com.website.backend.article.repository.ArticleLikeRepository;
import com.website.backend.article.service.LikeService;
import com.website.backend.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 点赞服务实现类
 * 使用Redis存储点赞数据，提高性能
 * 使用MySQL持久化点赞记录
 */
@Service
public class LikeServiceImpl implements LikeService {

    private static final Logger log = LoggerFactory.getLogger(LikeServiceImpl.class);

    // Redis键前缀
    private static final String LIKE_COUNT_KEY_PREFIX = "article:like:count:";
    private static final String USER_LIKE_KEY_PREFIX = "user:like:";
    private static final String ARTICLE_LIKED_USERS_KEY_PREFIX = "article:liked:users:";

    private final ArticleLikeRepository articleLikeRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public LikeServiceImpl(ArticleLikeRepository articleLikeRepository, 
                          RedisTemplate<String, Object> redisTemplate) {
        this.articleLikeRepository = articleLikeRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public long likeArticle(Long articleId, Long userId) {
        log.info("用户点赞文章，articleId: {}, userId: {}", articleId, userId);
        
        try {
            // 检查是否已经点赞
            if (hasUserLikedArticle(articleId, userId)) {
                log.info("用户 {} 已经点赞过文章 {}", userId, articleId);
                return getArticleLikeCount(articleId);
            }

            // 保存点赞记录到数据库
            ArticleLike articleLike = new ArticleLike();
            articleLike.setArticleId(articleId);
            articleLike.setUserId(userId);
            articleLike.setCreateTime(LocalDateTime.now());
            articleLikeRepository.save(articleLike);

            // 更新Redis中的点赞计数
            String likeCountKey = LIKE_COUNT_KEY_PREFIX + articleId;
            redisTemplate.opsForValue().increment(likeCountKey, 1);

            // 记录用户点赞的文章
            if (userId != null) {
                String userLikeKey = USER_LIKE_KEY_PREFIX + userId;
                redisTemplate.opsForSet().add(userLikeKey, articleId.toString());
            }

            // 记录文章被哪些用户点赞
            String articleLikedUsersKey = ARTICLE_LIKED_USERS_KEY_PREFIX + articleId;
            if (userId != null) {
                redisTemplate.opsForSet().add(articleLikedUsersKey, userId);
            }

            // 获取更新后的点赞数
            Long likeCount = redisTemplate.opsForValue().get(likeCountKey) != null ? 
                (Long) redisTemplate.opsForValue().get(likeCountKey) : 0L;
            
            log.info("用户 {} 点赞文章 {} 成功，当前点赞数: {}", userId, articleId, likeCount);
            return likeCount;
        } catch (Exception e) {
            log.error("点赞文章失败，articleId: {}, userId: {}", articleId, userId, e);
            throw new BusinessException("点赞失败: " + e.getMessage(), "LIKE_ERROR", null, e);
        }
    }

    @Override
    @Transactional
    public long unlikeArticle(Long articleId, Long userId) {
        log.info("用户取消点赞文章，articleId: {}, userId: {}", articleId, userId);
        
        if (userId == null) {
            log.warn("匿名用户无法取消点赞");
            throw new BusinessException("匿名用户无法取消点赞", "UNLIKE_ERROR", null);
        }

        try {
            // 检查是否已经点赞
            if (!hasUserLikedArticle(articleId, userId)) {
                log.info("用户 {} 没有点赞过文章 {}", userId, articleId);
                return getArticleLikeCount(articleId);
            }

            // 从数据库删除点赞记录
            articleLikeRepository.deleteByArticleIdAndUserId(articleId, userId);

            // 更新Redis中的点赞计数
            String likeCountKey = LIKE_COUNT_KEY_PREFIX + articleId;
            redisTemplate.opsForValue().decrement(likeCountKey, 1);

            // 从用户点赞记录中移除
            String userLikeKey = USER_LIKE_KEY_PREFIX + userId;
            redisTemplate.opsForSet().remove(userLikeKey, articleId.toString());

            // 从文章被点赞用户记录中移除
            String articleLikedUsersKey = ARTICLE_LIKED_USERS_KEY_PREFIX + articleId;
            redisTemplate.opsForSet().remove(articleLikedUsersKey, userId);

            // 获取更新后的点赞数
            Long likeCount = redisTemplate.opsForValue().get(likeCountKey) != null ? 
                (Long) redisTemplate.opsForValue().get(likeCountKey) : 0L;
            
            log.info("用户 {} 取消点赞文章 {} 成功，当前点赞数: {}", userId, articleId, likeCount);
            return likeCount;
        } catch (Exception e) {
            log.error("取消点赞文章失败，articleId: {}, userId: {}", articleId, userId, e);
            throw new BusinessException("取消点赞失败: " + e.getMessage(), "UNLIKE_ERROR", null, e);
        }
    }

    @Override
    public long getArticleLikeCount(Long articleId) {
        log.info("获取文章点赞数，articleId: {}", articleId);
        
        try {
            String likeCountKey = LIKE_COUNT_KEY_PREFIX + articleId;
            Object countObj = redisTemplate.opsForValue().get(likeCountKey);
            
            if (countObj == null) {
                // 如果Redis中没有数据，从数据库加载
                long dbCount = articleLikeRepository.countByArticleId(articleId);
                redisTemplate.opsForValue().set(likeCountKey, dbCount);
                log.info("从数据库加载文章 {} 的点赞数: {}", articleId, dbCount);
                return dbCount;
            }
            
            long likeCount = (Long) countObj;
            log.info("从Redis获取文章 {} 的点赞数: {}", articleId, likeCount);
            return likeCount;
        } catch (Exception e) {
            log.error("获取文章点赞数失败，articleId: {}", articleId, e);
            throw new BusinessException("获取点赞数失败: " + e.getMessage(), "GET_LIKE_COUNT_ERROR", null, e);
        }
    }

    @Override
    public boolean hasUserLikedArticle(Long articleId, Long userId) {
        log.info("检查用户是否已点赞文章，articleId: {}, userId: {}", articleId, userId);
        
        if (userId == null) {
            return false;
        }

        try {
            String userLikeKey = USER_LIKE_KEY_PREFIX + userId;
            Boolean isMember = redisTemplate.opsForSet().isMember(userLikeKey, articleId.toString());
            boolean result = Boolean.TRUE.equals(isMember);
            log.info("用户 {} {}点赞过文章 {}", userId, result ? "已" : "未", articleId);
            return result;
        } catch (Exception e) {
            log.error("检查用户点赞状态失败，articleId: {}, userId: {}", articleId, userId, e);
            throw new BusinessException("检查点赞状态失败: " + e.getMessage(), "CHECK_LIKE_STATUS_ERROR", null, e);
        }
    }
    
    @Override
    public List<Long> getUserLikedArticles(Long userId, int limit) {
        log.info("获取用户点赞的文章列表，userId: {}, limit: {}", userId, limit);
        
        String userLikeKey = USER_LIKE_KEY_PREFIX + userId;
        Set<Object> articleIdsSet = redisTemplate.opsForSet().members(userLikeKey);
        
        if (articleIdsSet == null || articleIdsSet.isEmpty()) {
            log.info("用户 {} 没有点赞过任何文章", userId);
            return List.of();
        }
        
        // 将Set转换为List，并限制数量
        List<Long> articleIds = articleIdsSet.stream()
                .map(id -> Long.valueOf(id.toString()))
                .limit(limit)
                .collect(Collectors.toList());
        
        log.info("成功获取用户 {} 点赞了 {} 篇文章", userId, articleIds.size());
        return articleIds;
    }
}