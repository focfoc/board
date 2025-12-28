package com.example.board.article.exception;

public class ArticleNotFoundException extends RuntimeException{

    public ArticleNotFoundException(Long id) {
        super("해당 게시글을 찾을 수 없습니다. (ID: " + id + ")");
    }
}
