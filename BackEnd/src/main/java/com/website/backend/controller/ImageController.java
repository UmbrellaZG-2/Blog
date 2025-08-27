package com.website.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.website.backend.constant.HttpStatusConstants;
import com.website.backend.entity.Article;
import com.website.backend.entity.ArticlePicture;
import com.website.backend.model.ApiResponse;
import com.website.backend.repository.jpa.ArticlePictureRepository;
import com.website.backend.repository.jpa.ArticleRepository;
import com.website.backend.service.ArticlePictureService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    private final ArticlePictureRepository articlePictureRepository;
    private final ArticlePictureService articlePictureService;
    private final ArticleRepository articleRepository;

    @Autowired
    public ImageController(ArticlePictureRepository articlePictureRepository, ArticlePictureService articlePictureService, ArticleRepository articleRepository) {
        this.articlePictureRepository = articlePictureRepository;
        this.articlePictureService = articlePictureService;
        this.articleRepository = articleRepository;
    }

     @PostMapping("/article/{articleId}/cover/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> uploadArticleCover(@PathVariable Long articleId, @RequestParam MultipartFile file) {

        try {
            Article article = articleRepository.findById(articleId)
                    .orElseThrow(() -> new RuntimeException("文章不存在"));
            
            Optional<ArticlePicture> oldCover = articlePictureRepository.findByArticle(article);
            if (oldCover.isPresent()) {
                articlePictureService.deletePicture(oldCover.get().getPictureId());
            }
            
            ArticlePicture newCover = articlePictureService.uploadPicture(file, article);
            newCover.setIsCover(true);
            articlePictureRepository.save(newCover);
            
            return ApiResponse.success(newCover.getFilePath());
        } catch (Exception e) {
            logger.error("文章封面图片上传失败: {}", e.getMessage());
            return ApiResponse.fail(HttpStatusConstants.INTERNAL_SERVER_ERROR, "图片上传失败");
        }
    }

    @DeleteMapping("/article/{articleId}/cover/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteArticleCover(@PathVariable Long articleId) {

        try {
            Article article = articleRepository.findById(articleId)
                    .orElseThrow(() -> new RuntimeException("文章不存在"));
            
            Optional<ArticlePicture> cover = articlePictureRepository.findByArticle(article);
            if (cover.isPresent()) {
                articlePictureService.deletePicture(cover.get().getPictureId());
        
                return ApiResponse.success("封面图片删除成功");
            } else {
                return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, "封面图片不存在");
            }
        } catch (Exception e) {
            logger.error("文章封面图片删除失败: {}", e.getMessage());
            return ApiResponse.fail(HttpStatusConstants.INTERNAL_SERVER_ERROR, "图片删除失败");
        }
    }

    @GetMapping("/article/{articleId}/cover/get")
    public ApiResponse<ArticlePicture> getArticleCover(@PathVariable Long articleId) {

        Optional<Article> articleOpt = articleRepository.findById(articleId);
        if (articleOpt.isEmpty()) {
            return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, "文章不存在");
        }
        
        Article article = articleOpt.get();
        
        Optional<ArticlePicture> picture = articlePictureRepository.findByArticle(article);
        if (picture.isPresent() && picture.get().getIsCover()) {
            return ApiResponse.success(picture.get());
        } else {
            return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, "封面图片不存在");
        }
    }

    @GetMapping("/article/{articleId}/getAll")
    public ApiResponse<List<ArticlePicture>> getArticleImages(@PathVariable Long articleId) {

        Optional<Article> articleOpt = articleRepository.findById(articleId);
        if (articleOpt.isEmpty()) {
            return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, "文章不存在");
        }
        
        Article article = articleOpt.get();
        
        Optional<ArticlePicture> picture = articlePictureRepository.findByArticle(article);
        List<ArticlePicture> images = picture.map(List::of).orElse(List.of());
        
        return ApiResponse.success(images);
    }
}