package com.website.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.website.backend.entity.Tag;
import com.website.backend.model.ApiResponse;
import com.website.backend.repository.jpa.TagRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagRepository tagRepository;

    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
	 * 获取所有标签接口
	 */
	@GetMapping("/get")
	public ApiResponse<List<String>> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        List<String> tagNames = tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
        return ApiResponse.success(tagNames);
    }
}