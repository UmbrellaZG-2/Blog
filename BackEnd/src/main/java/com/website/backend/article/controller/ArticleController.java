package com.website.backend.article.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.website.backend.article.dto.ArticleDTO;
import com.website.backend.article.dto.ArticleListDTO;
import com.website.backend.article.dto.ArticleSearchDTO;
import com.website.backend.article.dto.DeleteArticleResponseDTO;
import com.website.backend.article.dto.CommentDTO;
import com.website.backend.article.entity.Article;
import com.website.backend.comment.entity.Comment;
import com.website.backend.article.exception.ArticleNotFoundException;
import com.website.backend.common.model.ApiResponse;
import com.website.backend.article.repository.ArticleRepository;
import com.website.backend.comment.repository.CommentRepository;
import com.website.backend.article.repository.TagRepository;
import com.website.backend.article.repository.ArticleTagRepository;
import com.website.backend.common.util.DTOConverter;
import com.website.backend.system.service.SensitiveWordFilterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/articles")
public class ArticleController {

	private final ArticleRepository articleRepo;
	private final CommentRepository commentRepository;
	private final TagRepository tagRepository;
	private final ArticleTagRepository articleTagRepository;
	private final DTOConverter dtoConverter;
	private final SensitiveWordFilterService sensitiveWordFilterService;

	public ArticleController(ArticleRepository articleRepo, CommentRepository commentRepository,
			TagRepository tagRepository, ArticleTagRepository articleTagRepository, DTOConverter dtoConverter,
			SensitiveWordFilterService sensitiveWordFilterService) {
		this.articleRepo = articleRepo;
		this.commentRepository = commentRepository;
		this.tagRepository = tagRepository;
		this.articleTagRepository = articleTagRepository;
		this.dtoConverter = dtoConverter;
		this.sensitiveWordFilterService = sensitiveWordFilterService;
	}

	@GetMapping
	public ApiResponse<ArticleListDTO> getAllArticles(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<Article> articlePage = articleRepo.findAll(pageable);

		ArticleListDTO articleListDTO = buildArticleListDTO(articlePage);
		return ApiResponse.success(articleListDTO);
	}

	private ArticleListDTO buildArticleListDTO(Page<Article> articlePage) {
		ArticleListDTO articleListDTO = new ArticleListDTO();
		articleListDTO.setArticles(articlePage.getContent().stream().map(dtoConverter::convertToDTO)
				.collect(Collectors.toList()));
		articleListDTO.setCurrentPage(articlePage.getNumber());
		articleListDTO.setPageSize(articlePage.getSize());
		articleListDTO.setTotalElements(articlePage.getTotalElements());
		articleListDTO.setTotalPages(articlePage.getTotalPages());
		return articleListDTO;
	}

	@PostMapping("/search")
	public ApiResponse<ArticleListDTO> searchArticles(@RequestBody ArticleSearchDTO searchDTO) {

		Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize());
		Page<Article> articlePage;

		if (searchDTO.getCategory() != null && !searchDTO.getCategory().isEmpty()) {
			articlePage = articleRepo.findByCategoryAndTitleContainingOrContentContaining(searchDTO.getCategory(),
					searchDTO.getKeyword(), searchDTO.getKeyword(), pageable);
		}
		else {
			articlePage = articleRepo.findByTitleContainingOrContentContaining(searchDTO.getKeyword(),
					searchDTO.getKeyword(), pageable);
		}

		ArticleListDTO articleListDTO = buildArticleListDTO(articlePage);
		return ApiResponse.success(articleListDTO);
	}

	@GetMapping("/get/{id}")
	public ApiResponse<ArticleDTO> articleDetails(@PathVariable Long id) {

		Article article = articleRepo.findById(id)
				.orElseThrow(() -> new ArticleNotFoundException(id.toString()));
		ArticleDTO dto = dtoConverter.convertToDTO(article);
		return ApiResponse.success(dto);
	}

	@GetMapping("/category/get/{category}")
	public ApiResponse<ArticleListDTO> articlesByCategory(@PathVariable String category,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<Article> articlePage = articleRepo.findByCategory(category, pageable);
		ArticleListDTO articleListDTO = buildArticleListDTO(articlePage);
		return ApiResponse.success(articleListDTO);
	}

	@PostMapping("/{articleId}/comments/put")
	public ApiResponse<Comment> addComment(@PathVariable Long articleId, @RequestParam String content,
			@RequestParam(required = false) Long parentId) {

		Article article = articleRepo.findById(articleId)
				.orElseThrow(() -> new ArticleNotFoundException(articleId.toString()));

		// 过滤敏感词
		String filteredContent = sensitiveWordFilterService.filter(content);

		Comment comment = new Comment();
		comment.setArticleId(articleId);
		comment.setParentId(parentId);
		String nickname = "Bro有话聊" + UUID.randomUUID().toString().substring(0, 8);
		comment.setNickname(nickname);
		comment.setContent(filteredContent);
		comment.setCreateTime(LocalDateTime.now());

		Comment savedComment = commentRepository.save(comment);
		return ApiResponse.success(savedComment);
	}

	/**
	 * 编辑评论（仅管理员权限）
	 */
	@PutMapping("/comments/{commentId}/update")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<CommentDTO> updateComment(@PathVariable Long commentId, @RequestParam String content) {
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new ArticleNotFoundException("评论不存在，ID: " + commentId));

		// 过滤敏感词
		String filteredContent = sensitiveWordFilterService.filter(content);
		comment.setContent(filteredContent);

		Comment updatedComment = commentRepository.save(comment);
		CommentDTO commentDTO = convertToCommentDTO(updatedComment);
		return ApiResponse.success(commentDTO);
	}

	/**
	 * 删除评论（仅管理员权限）
	 */
	@DeleteMapping("/comments/{commentId}/delete")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<String> deleteComment(@PathVariable Long commentId) {
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new ArticleNotFoundException("评论不存在，ID: " + commentId));

		// 如果是父评论，需要先删除所有回�?
		if (comment.getParentId() == null) {
			List<Comment> replies = commentRepository.findByParentId(commentId);
			commentRepository.deleteAll(replies);
		}

		commentRepository.delete(comment);
		return ApiResponse.success("评论删除成功");
	}

	@GetMapping("/{articleId}/comments/get")
	public ApiResponse<List<CommentDTO>> getArticleComments(@PathVariable Long articleId) {

		Article article = articleRepo.findById(articleId)
				.orElseThrow(() -> new ArticleNotFoundException(articleId.toString()));

		// 获取顶级评论
		List<Comment> topLevelComments = commentRepository.findByArticleIdAndParentIdIsNull(articleId);
		
		// 构建评论�?
		List<CommentDTO> commentDTOs = new ArrayList<>();
		for (Comment comment : topLevelComments) {
			CommentDTO commentDTO = convertToCommentDTO(comment);
			
			// 获取该评论的所有回复并按时间排�?
			List<Comment> replies = commentRepository.findByParentIdOrderByCreateTimeAsc(comment.getId());
			List<CommentDTO> replyDTOs = replies.stream()
					.map(this::convertToCommentDTO)
					.collect(Collectors.toList());
			
			commentDTO.setReplies(replyDTOs);
			commentDTOs.add(commentDTO);
		}
		
		return ApiResponse.success(commentDTOs);
	}

	private CommentDTO convertToCommentDTO(Comment comment) {
		CommentDTO dto = new CommentDTO();
		dto.setId(comment.getId());
		dto.setArticleId(comment.getArticleId());
		dto.setParentId(comment.getParentId());
		dto.setNickname(comment.getNickname());
		dto.setContent(comment.getContent());
		dto.setCreateTime(comment.getCreateTime());
		dto.setIpAddress(comment.getIpAddress());
		return dto;
	}

	@GetMapping("/categories/get")
	public ApiResponse<List<String>> getAllCategories() {

		List<String> categories = articleRepo.findDistinctCategories();
		return ApiResponse.success(categories);
	}

	@PostMapping("/{articleId}/tags/put")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<List<String>> addArticleTags(@PathVariable Long articleId, @RequestBody List<String> tagNames) {

		Article article = articleRepo.findById(articleId)
				.orElseThrow(() -> new ArticleNotFoundException(articleId.toString()));

		for (String tagName : tagNames) {
			tagRepository.findByName(tagName).orElseGet(() -> {
				com.website.backend.article.entity.Tag tag = new com.website.backend.article.entity.Tag();
				tag.setName(tagName);
				tag.setCreateTime(LocalDateTime.now());
				return tagRepository.save(tag);
			});
		}

		List<com.website.backend.article.entity.Tag> tags = tagRepository.findByArticleId(articleId);
		List<String> currentTags = tags.stream().map(com.website.backend.article.entity.Tag::getName)
				.collect(Collectors.toList());
		return ApiResponse.success(currentTags);
	}

	@DeleteMapping("/{articleId}/tags/delete/{tagName}")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<List<String>> removeArticleTag(@PathVariable Long articleId, @PathVariable String tagName) {

		Article article = articleRepo.findById(articleId)
				.orElseThrow(() -> new ArticleNotFoundException(articleId.toString()));

		tagRepository.findByName(tagName).ifPresent(tag -> {
			articleTagRepository.deleteByArticleIdAndTagId(articleId, tag.getId());
		});

		List<com.website.backend.article.entity.Tag> tags = tagRepository.findByArticleId(articleId);
		List<String> currentTags = tags.stream().map(com.website.backend.article.entity.Tag::getName)
				.collect(Collectors.toList());
		return ApiResponse.success(currentTags);
	}

	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<ArticleDTO> createArticle(@RequestParam String title, @RequestParam String category,
			@RequestParam String content, @RequestParam(required = false) MultipartFile attachment,
			@RequestParam(required = false) MultipartFile picture) {

		Article article = new Article();
		article.setTitle(title);
		article.setCategory(category);
		article.setContent(content);
		article.setCreateTime(LocalDateTime.now());

		Article savedArticle = articleRepo.save(article);

		ArticleDTO dto = dtoConverter.convertToDTO(savedArticle);
		return ApiResponse.success(dto);
	}

	@PutMapping("/update/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<ArticleDTO> updateArticle(@PathVariable Long id, @RequestParam String title,
			@RequestParam String category, @RequestParam String content,
			@RequestParam(required = false) Boolean deleteAttachment,
			@RequestParam(required = false) Boolean deletePicture,
			@RequestParam(required = false) MultipartFile attachment,
			@RequestParam(required = false) MultipartFile picture) {

		Article article = articleRepo.findById(id)
				.orElseThrow(() -> new ArticleNotFoundException(id.toString()));

		article.setTitle(title);
		article.setCategory(category);
		article.setContent(content);
		article.setUpdateTime(LocalDateTime.now());

		Article updatedArticle = articleRepo.save(article);

		ArticleDTO dto = dtoConverter.convertToDTO(updatedArticle);
		return ApiResponse.success(dto);
	}

	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<DeleteArticleResponseDTO> deleteArticle(@PathVariable Long id) {

		Article article = articleRepo.findById(id)
				.orElseThrow(() -> new ArticleNotFoundException(id.toString()));

		articleRepo.deleteById(id);

		DeleteArticleResponseDTO response = new DeleteArticleResponseDTO();
		response.setSuccess(true);
		response.setMessage("文章删除成功");
		return ApiResponse.success(response);
	}
}
