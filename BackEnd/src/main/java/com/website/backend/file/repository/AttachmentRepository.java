package com.website.backend.file.repository;

import com.website.backend.file.entity.Attachment;
import com.website.backend.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

	List<Attachment> findByArticle(Article article);

	void deleteByArticle(Article article);

	int countByArticle(Article article);
	// 继承自JpaRepository的方法已包含findById等CRUD操作

}