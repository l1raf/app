package com.liraf.reader.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.liraf.reader.models.ArticleEntity;
import com.liraf.reader.repositories.ArticleRepository;
import com.liraf.reader.utils.Resource;

public class ArticleViewModel extends AndroidViewModel {

    private final ArticleRepository articleRepository;

    public ArticleViewModel(@NonNull Application application) {
        super(application);
        this.articleRepository = ArticleRepository.getInstance(application);
    }

    public LiveData<Resource<ArticleEntity>> loadArticleFromDb(String url) {
        return articleRepository.getArticle(url, false);
    }

    public void deleteArticle(String url) {
        articleRepository.deleteArticle(url);
    }
}
