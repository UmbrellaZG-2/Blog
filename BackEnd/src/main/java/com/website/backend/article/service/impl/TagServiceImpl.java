package com.website.backend.article.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.website.backend.article.entity.Tag;
import com.website.backend.article.entity.ArticleTag;
import com.website.backend.article.repository.TagRepository;
import com.website.backend.article.repository.ArticleTagRepository;
import com.website.backend.article.service.TagService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 标签服务实现类
 * 提供标签管理、统计和推荐功能的具体实现 */
@Service
public class TagServiceImpl implements TagService {
    
    private static final Logger log = LoggerFactory.getLogger(TagServiceImpl.class);
    
    private final TagRepository tagRepository;
    private final ArticleTagRepository articleTagRepository;
    
    public TagServiceImpl(TagRepository tagRepository, ArticleTagRepository articleTagRepository) {
        this.tagRepository = tagRepository;
        this.articleTagRepository = articleTagRepository;
    }
    
    @Override
    public List<Tag> getAllTags() {
        log.info("获取所有标签");
        return tagRepository.findAll();
    }
    
    @Override
    public Tag getTagByName(String name) {
        log.info("根据名称获取标签: {}", name);
        return tagRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("标签不存在: " + name));
    }
    
    @Override
    public Tag createTag(String name) {
        log.info("创建新标签: {}", name);
        
        // 检查标签是否已存在
        Optional<Tag> existingTag = tagRepository.findByName(name);
        if (existingTag.isPresent()) {
            log.warn("标签已存在: {}", name);
            return existingTag.get();
        }
        
        Tag tag = new Tag();
        tag.setName(name);
        tag.setCreateTime(LocalDateTime.now());
        
        return tagRepository.save(tag);
    }
    
    @Override
    public void deleteTag(Long id) {
        log.info("删除标签，ID: {}", id);
        
        // 先删除与文章的关联
        List<ArticleTag> articleTags = articleTagRepository.findByTagId(id);
        if (!articleTags.isEmpty()) {
            articleTagRepository.deleteAll(articleTags);
            log.info("删除了 {} 条文章与标签的关联", articleTags.size());
        }
        
        // 再删除标签
        tagRepository.deleteById(id);
    }
    
    @Override
    public Map<String, Long> getTagStatistics() {
        log.info("获取标签统计信息");
        
        // 获取所有标签
        List<Tag> tags = tagRepository.findAll();
        
        // 计算每个标签关联的文章数量
        return tags.stream()
                .collect(Collectors.toMap(
                        Tag::getName, 
                        tag -> (long) articleTagRepository.findByTagId(tag.getId()).size()
                ));
    }
    
    @Override
    public List<Tag> getTagsByArticleId(Long articleId) {
        log.info("获取文章的所有标签，文章ID: {}", articleId);
        return tagRepository.findByArticleId(articleId);
    }
    
    @Override
    public void addTagsToArticle(Long articleId, List<String> tagNames) {
        log.info("为文章添加标签，文章ID: {}, 标签数量: {}", articleId, tagNames.size());
        
        for (String tagName : tagNames) {
            // 获取或创建标签
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tagName);
                        newTag.setCreateTime(LocalDateTime.now());
                        return tagRepository.save(newTag);
                    });
            
            // 检查文章和标签是否已关联
            if (!articleTagRepository.existsByArticleIdAndTagId(articleId, tag.getId())) {
                // 创建关联
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(articleId);
                articleTag.setTagId(tag.getId());
                articleTagRepository.save(articleTag);
                log.info("文章 {} 成功添加标签: {}", articleId, tagName);
            } else {
                log.info("文章 {} 已关联标签: {}, 跳过添加", articleId, tagName);
            }
        }
    }
    
    @Override
    public void removeTagFromArticle(Long articleId, String tagName) {
        log.info("从文章中移除标签，文章ID: {}, 标签名称: {}", articleId, tagName);
        
        // 获取标签
        Tag tag = tagRepository.findByName(tagName)
                .orElseThrow(() -> new RuntimeException("标签不存在: " + tagName));
        
        // 删除关联
        articleTagRepository.deleteByArticleIdAndTagId(articleId, tag.getId());
        log.info("成功从文章 {} 移除标签: {}", articleId, tagName);
    }
    
    @Override
    public List<Tag> recommendTags(List<String> tagNames, int limit) {
        log.info("根据标签推荐相关标签，已知标签数量: {}, 推荐数量: {}", tagNames.size(), limit);
        
        // 这里实现一个简单的推荐算法
        // 1. 获取已知标签关联的所有文章
        List<Long> articleIds = new ArrayList<>();
        for (String tagName : tagNames) {
            try {
                Tag tag = tagRepository.findByName(tagName).orElse(null);
                if (tag != null) {
                    List<ArticleTag> articleTags = articleTagRepository.findByTagId(tag.getId());
                    for (ArticleTag articleTag : articleTags) {
                        articleIds.add(articleTag.getArticleId());
                    }
                }
            } catch (Exception e) {
                log.error("获取标签关联文章失败: {}", e.getMessage());
            }
        }
        // 去重
        articleIds = articleIds.stream().distinct().collect(Collectors.toList());
        
        // 2. 获取这些文章的所有标签并统计频率
        Map<String, Long> tagFrequency = new HashMap<>();
        for (Long articleId : articleIds) {
            try {
                List<Tag> tags = tagRepository.findByArticleId(articleId);
                for (Tag tag : tags) {
                    String tagName = tag.getName();
                    if (!tagNames.contains(tagName)) {
                        tagFrequency.put(tagName, tagFrequency.getOrDefault(tagName, 0L) + 1);
                    }
                }
            } catch (Exception e) {
                log.error("获取文章标签失败: {}", e.getMessage());
            }
        }
        
        // 3. 按频率排序并返回前N个标签
        return tagFrequency.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .map(entry -> tagRepository.findByName(entry.getKey()).orElse(null))
                .filter(tag -> tag != null)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Tag> getPopularTags(int limit) {
        log.info("获取热门标签，限制数量: {}", limit);
        
        // 获取标签统计信息
        Map<String, Long> tagStatistics = getTagStatistics();
        
        // 按文章数量排序并返回前N个标签
        return tagStatistics.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .map(entry -> tagRepository.findByName(entry.getKey()).orElse(null))
                .filter(tag -> tag != null)
                .collect(Collectors.toList());
    }
}
