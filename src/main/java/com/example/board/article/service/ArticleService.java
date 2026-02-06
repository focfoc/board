package com.example.board.article.service;

import com.example.board.article.exception.ArticleNotFoundException;
import com.example.board.article.repository.ArticleRepository;
import com.example.board.article.viewcount.service.ViewCountService;
import com.example.board.domain.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ViewCountService viewCountService;

    public List<Article> findAll(){
        return articleRepository.findAll();
    }

    public Article findById(Long id){
        return articleRepository.findByArticleNo(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));
    }

    @Transactional
    public Article read(Long id){
        Article article = findById(id);
        viewCountService.increaseViewCount(id);
        return article;
    }

    @Transactional
    public Article save(Article article){
        return articleRepository.save(article);
    }

    @Transactional
    public Article update(Long id, String title) {
        Article article = findById(id);
        article.updateTitle(title);
        return article;  // 변경 감지(dirty checking)로 자동 업데이트
    }

    @Transactional
    public void delete(Long id) {
        Article article = findById(id);
        articleRepository.delete(article);
    }

}
