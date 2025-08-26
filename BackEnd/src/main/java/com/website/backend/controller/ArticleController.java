package com.website.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.website.backend.constant.HttpStatusConstants;
import com.website.backend.entity.Article;
import com.website.backend.entity.Comment;
import com.website.backend.entity.Tag;
import com.website.backend.model.ApiResponse;
import com.website.backend.repository.jpa.ArticleRepository;
import com.website.backend.repository.jpa.CommentRepository;
import com.website.backend.repository.jpa.TagRepository;
import com.website.backend.repository.jpa.ArticleTagRepository;
import com.website.backend.DTO.ArticleDTO;
import com.website.backend.DTO.ArticleListDTO;
import com.website.backend.DTO.DeleteArticleResponseDTO;
import com.website.backend.util.DTOConverter;
import com.website.backend.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    private final ArticleRepository articleRepo;
    private final DTOConverter dtoConverter;
    private final CommentRepository commentRepository;
    private final TagRepository tagRepository;
    private final ArticleTagRepository articleTagRepository;

    public ArticleController(ArticleRepository articleRepo, DTOConverter dtoConverter,
                               CommentRepository commentRepository, TagRepository tagRepository,
                               ArticleTagRepository articleTagRepository) {
        this.articleRepo = articleRepo;
        this.dtoConverter = dtoConverter;
        this.commentRepository = commentRepository;
        this.tagRepository = tagRepository;
        this.articleTagRepository = articleTagRepository;
    }

    // 抽取公共方法处理分页和DTO转换
    private ArticleListDTO buildArticleListDTO(Page<Article> articlePage) {
        List<ArticleDTO> articleDTOList = articlePage.getContent()
                .stream()
                .map(dtoConverter::convertToDTO)
                .toList();

        ArticleListDTO articleListDTO = new ArticleListDTO();
        articleListDTO.setArticles(articleDTOList);
        articleListDTO.setTotalArticles((int) articlePage.getTotalElements());
        articleListDTO.setTotalPages(articlePage.getTotalPages());
        articleListDTO.setCurrentPage(articlePage.getNumber());
        articleListDTO.setPageSize(articlePage.getSize());
        return articleListDTO;
    }

    /**
     * 获取文章列表
     */
    @GetMapping
    @PreAuthorize("permitAll()")
    public ApiResponse<ArticleListDTO> articles(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articlePage = articleRepo.findAll(pageable);

        ArticleListDTO articleListDTO = buildArticleListDTO(articlePage);

        return ApiResponse.success(articleListDTO);
    }

    /**
     * 根据文章标题搜索文章
     */
    @GetMapping("/search")
    public ApiResponse<ArticleListDTO> searchArticles(@RequestParam String keyword,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articlePage = articleRepo.findByTitleContaining(keyword, pageable);
        ArticleListDTO articleListDTO = buildArticleListDTO(articlePage);
        return ApiResponse.success(articleListDTO);
    }

    /**
     * 创建文章（管理员专用）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ApiResponse<ArticleDTO> createArticle(@RequestParam String title, @RequestParam String category,
                                                @RequestParam String content, @RequestParam(defaultValue = "false") boolean isDraft) {

        if (title == null || title.trim().isEmpty() || category == null || category.trim().isEmpty() ||
                content == null || content.trim().isEmpty()) {
            logger.warn("文章标题、分类或内容不能为空");
            return ApiResponse.fail(HttpStatusConstants.BAD_REQUEST, "文章标题、分类和内容不能为空");
        }

        Article article = new Article();
        article.setTitle(title);
        article.setCategory(category);
        article.setContent(content);
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        article.setHasAttachment(false);
        article.setHasCoverImage(false);
        article.setDraft(isDraft);

        Article savedArticle = articleRepo.save(article);
        ArticleDTO dto = dtoConverter.convertToDTO(savedArticle);

        return ApiResponse.success(dto);
    }

    /**
     * 更新文章（管理员专用）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ApiResponse<ArticleDTO> updateArticle(@PathVariable Long id, @RequestParam String title,
                                                @RequestParam String category, @RequestParam String content,
                                                @RequestParam(defaultValue = "false") boolean isDraft) {

        Article article = articleRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("文章不存在"));

        if (title == null || title.trim().isEmpty() || category == null || category.trim().isEmpty() ||
                content == null || content.trim().isEmpty()) {
            logger.warn("文章标题、分类或内容不能为空");
            return ApiResponse.fail(HttpStatusConstants.BAD_REQUEST, "文章标题、分类和内容不能为空");
        }

        article.setTitle(title);
        article.setCategory(category);
        article.setContent(content);
        article.setUpdateTime(LocalDateTime.now());
        article.setDraft(isDraft);

        Article updatedArticle = articleRepo.save(article);
        ArticleDTO dto = dtoConverter.convertToDTO(updatedArticle);
        return ApiResponse.success(dto);
    }

    /**
     * 删除文章（管理员专用）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<DeleteArticleResponseDTO> deleteArticle(@PathVariable Long id) {

        Article article = articleRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("文章不存在"));

        articleRepo.deleteById(id);

        DeleteArticleResponseDTO response = new DeleteArticleResponseDTO();
        response.setSuccess(true);
        response.setMessage("文章删除成功");
        return ApiResponse.success(response);
    }

    /**
     * 文章详情页
     */
    @GetMapping("/get/{id}")
    public ApiResponse<ArticleDTO> articleDetails(@PathVariable Long id) {

        Article article = articleRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("文章不存在"));
        ArticleDTO dto = dtoConverter.convertToDTO(article);
        return ApiResponse.success(dto);
    }

    /**
     * 文章分类页
     */
    @GetMapping("/category/get/{category}")
    public ApiResponse<ArticleListDTO> articlesByCategory(@PathVariable String category,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articlePage = articleRepo.findByCategory(category, pageable);
        ArticleListDTO articleListDTO = buildArticleListDTO(articlePage);
        return ApiResponse.success(articleListDTO);
    }

    /**
     * 添加评论
     */
    @PostMapping("/{articleId}/comments/put")
    public ApiResponse<Comment> addComment(@PathVariable Long articleId, @RequestParam String content,
                                         @RequestParam(required = false) Long parentId) {

        Article article = articleRepo.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("文章不存在"));

        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setParentId(parentId);
        // 生成固定前缀+UUID的昵称
        String nickname = "Bro有话说" + UUID.randomUUID().toString().substring(0, 8);
        comment.setNickname(nickname);
        comment.setContent(content);
        comment.setCreateTime(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return ApiResponse.success(savedComment);
    }

    /**
     * 获取文章的所有评论
     */
    @GetMapping("/{articleId}/comments/get")
    public ApiResponse<List<Comment>> getArticleComments(@PathVariable Long articleId) {

        Article article = articleRepo.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("文章不存在"));

        List<Comment> comments = commentRepository.findByArticleId(articleId);
        return ApiResponse.success(comments);
    }

    /**
     * 获取所有分类
     */
    @GetMapping("/categories/get")
    public ApiResponse<List<String>> getAllCategories() {

        List<String> categories = articleRepo.findDistinctCategories();
        return ApiResponse.success(categories);
    }

    /**
     * 为文章添加标签
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{articleId}/tags/put")
    public ApiResponse<List<String>> addArticleTags(@PathVariable Long articleId, @RequestBody List<String> tagNames) {

        Article article = articleRepo.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("文章不存在"));

        for (String tagName : tagNames) {
            // 查找或创建标签
            tagRepository.findByName(tagName).orElseGet(() -> {
                Tag tag = new Tag();
                tag.setName(tagName);
                tag.setCreateTime(LocalDateTime.now());
                return tagRepository.save(tag);
            });
        }

        return ApiResponse.success(tagNames);
    }

    /**
     * 删除文章的标签
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{articleId}/tags/delete/{tagName}")
    public ApiResponse<String> deleteArticleTag(@PathVariable Long articleId, @PathVariable String tagName) {

        Article article = articleRepo.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("文章不存在"));
        Tag tag = tagRepository.findByName(tagName).orElseThrow(() -> new ResourceNotFoundException("标签不存在"));

        articleTagRepository.deleteByArticleIdAndTagId(articleId, tag.getId());

        // 检查是否还有其他文章使用该标签
        if (articleTagRepository.findByTagId(tag.getId()).isEmpty()) {
            tagRepository.delete(tag);
        }

        return ApiResponse.success("文章标签删除成功");
    }
}