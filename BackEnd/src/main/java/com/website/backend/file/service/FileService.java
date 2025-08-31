package com.website.backend.file.service;

import org.springframework.web.multipart.MultipartFile;
import com.website.backend.article.entity.Article;
import com.website.backend.file.entity.Attachment;
import java.io.IOException;
import java.util.List;

/**
 * 统一文件管理服务接口
 * 统一处理所有类型的文件上传、下载、删除等操作
 */
public interface FileService {
    
    /**
     * 上传附件文件
     * @param file 附件文件
     * @param article 关联的文章
     * @return 文件存储路径
     * @throws IOException 文件上传异常
     */
    String uploadAttachment(MultipartFile file, Article article) throws IOException;
    
    /**
     * 上传图片文件
     * @param file 图片文件
     * @param article 关联的文章
     * @return 文件存储路径
     * @throws IOException 文件上传异常
     */
    String uploadImage(MultipartFile file, Article article) throws IOException;
    
    /**
     * 下载附件文件
     * @param fileId 文件ID
     * @return 文件字节数组
     * @throws IOException 文件下载异常
     */
    byte[] downloadAttachment(Long fileId) throws IOException;
    
    /**
     * 删除附件文件
     * @param fileId 文件ID
     * @throws IOException 文件删除异常
     */
    void deleteAttachment(Long fileId) throws IOException;
    
    /**
     * 获取文章附件列表
     * @param articleId 文章ID
     * @return 附件列表
     */
    List<Attachment> getArticleAttachments(Long articleId);
}