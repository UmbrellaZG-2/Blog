package com.website.backend.article.service;

import com.website.backend.article.entity.Tag;
import java.util.List;
import java.util.Map;

/**
 * 标签服务接口
 * 提供标签管理、统计和推荐功能
 */
public interface TagService {
    
    /**
     * 获取所有标�?     * @return 标签列表
     */
    List<Tag> getAllTags();
    
    /**
     * 根据名称获取标签
     * @param name 标签名称
     * @return 标签对象
     */
    Tag getTagByName(String name);
    
    /**
     * 创建标签
     * @param name 标签名称
     * @return 创建的标�?     */
    Tag createTag(String name);
    
    /**
     * 删除标签
     * @param id 标签ID
     */
    void deleteTag(Long id);
    
    /**
     * 获取标签统计信息（标签及其关联的文章数量�?     * @return 标签统计map，键为标签名称，值为文章数量
     */
    Map<String, Long> getTagStatistics();
    
    /**
     * 获取文章的所有标�?     * @param articleId 文章ID
     * @return 标签列表
     */
    List<Tag> getTagsByArticleId(Long articleId);
    
    /**
     * 为文章添加标�?     * @param articleId 文章ID
     * @param tagNames 标签名称列表
     */
    void addTagsToArticle(Long articleId, List<String> tagNames);
    
    /**
     * 从文章中移除标签
     * @param articleId 文章ID
     * @param tagName 标签名称
     */
    void removeTagFromArticle(Long articleId, String tagName);
    
    /**
     * 根据其他标签推荐相关标签
     * @param tagNames 已知标签名称列表
     * @param limit 推荐数量限制
     * @return 推荐的标签列�?     */
    List<Tag> recommendTags(List<String> tagNames, int limit);
    
    /**
     * 获取热门标签
     * @param limit 获取数量限制
     * @return 热门标签列表
     */
    List<Tag> getPopularTags(int limit);
}
