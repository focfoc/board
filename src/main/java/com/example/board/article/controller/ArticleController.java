package com.example.board.article.controller;

import com.example.board.article.service.ArticleService;
import com.example.board.domain.Article;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public List<Article> getArticles() {
        List<Article> articles = articleService.findAll();
        return articles;
    }

    @GetMapping("/{id}")
    public Article getArticle(@PathVariable Long id) {
        return articleService.read(id);
    }

    @PostMapping
    public void createArticle(@RequestBody Article article) {
        articleService.save(article);
    }


}
