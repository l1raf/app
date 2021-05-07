package com.liraf.reader.models.article;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.liraf.reader.models.Article;

@Entity(foreignKeys = @ForeignKey(
        entity = Article.class,
        parentColumns = "url",
        childColumns = "articleUrlFk",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
        tableName = "content")
public class Content {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int type;
    private String text;
    private String url;
    private String articleUrlFk;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArticleUrlFk() {
        return articleUrlFk;
    }

    public void setArticleUrlFk(String articleUrlFk) {
        this.articleUrlFk = articleUrlFk;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}