package com.website.backend.file.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.website.backend.article.entity.Article;
import com.website.backend.file.entity.Attachment;
import com.website.backend.file.entity.ArticlePicture;
import com.website.backend.article.repository.ArticleRepository;
import com.website.backend.file.repository.AttachmentRepository;
import com.website.backend.file.service.AttachmentService;
import com.website.backend.file.service.ArticlePictureService;
import com.website.backend.file.service.FileService;
import com.website.backend.common.exception.ArticleNotFoundException;
import com.website.backend.common.exception.FileProcessingException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统一文件管理服务实现
 * 整合附件和图片服务，提供统一的文件操作接口
 */
@Service
public class FileServiceImpl implements FileService {
    
    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);
    
    private final AttachmentService attachmentService;
    private final ArticlePictureService articlePictureService;
    private final ArticleRepository articleRepository;
    private final AttachmentRepository attachmentRepository;
    
    public FileServiceImpl(AttachmentService attachmentService, 
                         ArticlePictureService articlePictureService,
                         ArticleRepository articleRepository,
                         AttachmentRepository attachmentRepository) {
        this.attachmentService = attachmentService;
        this.articlePictureService = articlePictureService;
        this.articleRepository = articleRepository;
        this.attachmentRepository = attachmentRepository;
    }
    
    @Override
    public String uploadAttachment(MultipartFile file, Article article) throws IOException {
        log.info("通过统一文件服务上传附件，文章ID: {}", article.getId());
        Attachment attachment = attachmentService.uploadAttachment(file, article);
        return attachment.getFilePath();
    }
    
    @Override
    public String uploadImage(MultipartFile file, Article article) throws IOException {
        log.info("通过统一文件服务上传图片，文章ID: {}", article.getId());
        ArticlePicture articlePicture = articlePictureService.uploadPicture(file, article);
        return articlePicture.getFilePath();
    }
    
    @Override
    public byte[] downloadAttachment(Long fileId) throws IOException {
        log.info("通过统一文件服务下载附件，文件ID: {}", fileId);
        return attachmentService.downloadAttachment(fileId);
    }
    
    @Override
    public void deleteAttachment(Long fileId) throws IOException {
        log.info("通过统一文件服务删除附件，文件ID: {}", fileId);
        attachmentService.deleteAttachment(fileId);
    }
    
    @Override
    public List<Attachment> getArticleAttachments(Long articleId) {
        log.info("通过统一文件服务获取文章附件列表，文章ID: {}", articleId);
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException("文章不存在，ID: " + articleId));
        List<Attachment> attachments = attachmentRepository.findByArticle(article);
        log.info("找到 {} 个附件，文章ID: {}", attachments.size(), articleId);
        return attachments;
    }
}