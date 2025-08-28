package com.website.backend.article.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import com.website.backend.article.entity.Tag;
import com.website.backend.common.model.ApiResponse;
import com.website.backend.article.service.TagService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    
    private static final Logger log = LoggerFactory.getLogger(TagController.class);

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

	@GetMapping("/get")
	public ApiResponse<List<String>> getAllTags() {
        log.info("获取所有标签");
        List<Tag> tags = tagService.getAllTags();
        List<String> tagNames = tags.stream()
                .map(Tag::getName)
                .collect(java.util.stream.Collectors.toList());
        return ApiResponse.success(tagNames);
    }
    
    @GetMapping("/list")
    public ApiResponse<List<Tag>> getTagList() {
        log.info("获取所有标签列表");
        List<Tag> tags = tagService.getAllTags();
        return ApiResponse.success(tags);
    }
    
    @PostMapping("/create")
    public ApiResponse<Tag> createTag(@RequestBody Map<String, String> request) {
        String tagName = request.get("name");
        log.info("创建新标签 {}", tagName);
        Tag tag = tagService.createTag(tagName);
        return ApiResponse.success(tag);
    }
    
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteTag(@PathVariable Long id) {
        log.info("删除标签，ID: {}", id);
        tagService.deleteTag(id);
        return ApiResponse.success("标签删除成功");
    }
    
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Long>> getTagStatistics() {
        log.info("获取标签统计信息");
        Map<String, Long> statistics = tagService.getTagStatistics();
        return ApiResponse.success(statistics);
    }
    
    @GetMapping("/article/{articleId}")
    public ApiResponse<List<Tag>> getArticleTags(@PathVariable Long articleId) {
        log.info("获取文章的标签，articleId: {}", articleId);
        List<Tag> tags = tagService.getTagsByArticleId(articleId);
        return ApiResponse.success(tags);
    }
    
    @GetMapping("/recommend")
    public ApiResponse<List<Tag>> recommendTags(
            @RequestParam("tags") List<String> tagNames,
            @RequestParam(defaultValue = "5") int limit) {
        log.info("推荐相关标签，已知标签数量: {}, 推荐数量: {}", tagNames.size(), limit);
        List<Tag> recommendedTags = tagService.recommendTags(tagNames, limit);
        return ApiResponse.success(recommendedTags);
    }
    
    @GetMapping("/popular")
    public ApiResponse<List<Tag>> getPopularTags(@RequestParam(defaultValue = "10") int limit) {
        log.info("获取热门标签，限制数量: {}", limit);
        List<Tag> popularTags = tagService.getPopularTags(limit);
        return ApiResponse.success(popularTags);
    }
}
