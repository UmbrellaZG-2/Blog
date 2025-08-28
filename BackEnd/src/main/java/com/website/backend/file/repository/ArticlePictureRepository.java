package com.website.backend.file.repository;

import com.website.backend.article.entity.Article;
import com.website.backend.file.entity.ArticlePicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticlePictureRepository extends JpaRepository<ArticlePicture, Long> {

	Optional<ArticlePicture> findByArticle(Article article);

	void deleteByArticle(Article article);

}