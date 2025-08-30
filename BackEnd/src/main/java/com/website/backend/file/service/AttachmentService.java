package com.website.backend.file.service;

import com.website.backend.file.entity.Attachment;
import com.website.backend.article.entity.Article;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface AttachmentService {

	Attachment uploadAttachment(MultipartFile file, Article article) throws IOException;

	byte[] downloadAttachment(Long attachmentId) throws IOException;

	void deleteAttachment(Long attachmentId);

	void deleteAttachmentsByArticle(Article article);

}
