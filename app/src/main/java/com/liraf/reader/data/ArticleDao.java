package com.liraf.reader.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.liraf.reader.models.Article;
import com.liraf.reader.models.ArticleEntity;
import com.liraf.reader.models.ArticleFavUpdate;
import com.liraf.reader.models.article.Content;

import java.util.List;

@Dao
public abstract class ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Transaction
    public abstract void insertArticle(Article article);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertContent(List<Content> content);

    @Transaction
    public void insertAllArticles(List<ArticleEntity> articles) {
        for (ArticleEntity article : articles)
            insertArticle(article);
    }

    @Transaction
    public void insertArticle(ArticleEntity article) {
        insertArticle(article.getArticle());
        for (Content content : article.getContent())
            content.setArticleUrlFk(article.getArticle().getUrl());
        insertContent(article.getContent());
    }

    @Query("SELECT * FROM article WHERE favorite = 1")
    public abstract LiveData<List<ArticleEntity>> getFavArticles();

    @Update(entity = Article.class)
    public abstract void updateFav(ArticleFavUpdate obj);

    @Query("SELECT * FROM article WHERE url = :url")
    @Transaction
    public abstract LiveData<ArticleEntity> getArticle(String url);

    @Query("SELECT * FROM article")
    @Transaction
    public abstract LiveData<List<ArticleEntity>> getAllArticles();

    @Query("DELETE FROM article WHERE url = :url")
    public abstract void deleteArticle(String url);

    @Query("DELETE FROM article")
    public abstract void deleteAllArticles();
}
