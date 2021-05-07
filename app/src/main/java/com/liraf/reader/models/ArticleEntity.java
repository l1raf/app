package com.liraf.reader.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.liraf.reader.models.article.Content;

import java.util.List;

public class ArticleEntity {

    @Embedded
    private Article article;

    @Relation(parentColumn = "url", entityColumn = "articleUrlFk")
    private List<Content> content;

    public ArticleEntity(Article article, List<Content> content) {
        this.article = article;
        this.content = content;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public List<Content> getContent() {
        return content;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }
}
