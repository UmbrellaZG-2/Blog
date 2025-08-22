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

    /**
     * 上传文章封面图片
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/article/{articleId}/cover/update")
    public ApiResponse<String> uploadArticleCover(@PathVariable Long articleId, @RequestParam MultipartFile file) {

        try {
            // 根据articleId查询Article对象
            Article article = articleRepository.findById(articleId)
                    .orElseThrow(() -> new RuntimeException("文章不存在"));
            
            // 先删除原有的封面图片
            Optional<ArticlePicture> oldCover = articlePictureRepository.findByArticle(article);
            if (oldCover.isPresent()) {
                articlePictureService.deletePicture(oldCover.get().getPictureId());
            }
            
            // 上传新的封面图片
            ArticlePicture newCover = articlePictureService.uploadPicture(file, article);
            // 设置为封面
            newCover.setIsCover(true);
            articlePictureRepository.save(newCover);
            
    
            return ApiResponse.success(newCover.getFilePath());
        } catch (Exception e) {
            logger.error("文章封面图片上传失败: {}", e.getMessage());
            return ApiResponse.fail(HttpStatusConstants.INTERNAL_SERVER_ERROR, "图片上传失败");
        }
    }

    /**
     * 删除文章封面图片
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/article/{articleId}/cover/delete")
    public ApiResponse<String> deleteArticleCover(@PathVariable Long articleId) {

        try {
            // 根据articleId查询Article对象
            Article article = articleRepository.findById(articleId)
                    .orElseThrow(() -> new RuntimeException("文章不存在"));
            
            // 查询该文章的封面图片
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

    /**
     * 获取文章封面图片
     */
    @GetMapping("/article/{articleId}/cover/get")
    public ApiResponse<ArticlePicture> getArticleCover(@PathVariable Long articleId) {

        // 根据articleId查询Article对象
        Optional<Article> articleOpt = articleRepository.findById(articleId);
        if (articleOpt.isEmpty()) {
            return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, "文章不存在");
        }
        
        Article article = articleOpt.get();
        
        // 查询该文章的图片
        Optional<ArticlePicture> picture = articlePictureRepository.findByArticle(article);
        if (picture.isPresent() && picture.get().getIsCover()) {
            return ApiResponse.success(picture.get());
        } else {
            return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, "封面图片不存在");
        }
    }

    /**
     * 获取文章的所有图片
     */
    @GetMapping("/article/{articleId}/getAll")
    public ApiResponse<List<ArticlePicture>> getArticleImages(@PathVariable Long articleId) {

        // 根据articleId查询Article对象
        Optional<Article> articleOpt = articleRepository.findById(articleId);
        if (articleOpt.isEmpty()) {
            return ApiResponse.fail(HttpStatusConstants.NOT_FOUND, "文章不存在");
        }
        
        Article article = articleOpt.get();
        
        // 查询该文章的所有图片
        // 注意：这里假设一个文章只有一张图片，如果有多个图片，需要修改ArticlePictureRepository添加相应方法
        Optional<ArticlePicture> picture = articlePictureRepository.findByArticle(article);
        List<ArticlePicture> images = picture.map(List::of).orElse(List.of());
        
        return ApiResponse.success(images);
    }
}