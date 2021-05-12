package com.liraf.reader.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.liraf.reader.models.ArticleEntity;
import com.liraf.reader.repositories.ArticleRepository;
import com.liraf.reader.utils.Resource;

import java.util.List;

public class ArticleViewModel extends AndroidViewModel {

    private final ArticleRepository articleRepository;
    private boolean scrollEnabled;
    private int scrollSpeed;

    public ArticleViewModel(@NonNull Application application) {
        super(application);
        this.articleRepository = ArticleRepository.getInstance(application);
        scrollSpeed = 300;
    }

    public LiveData<Resource<ArticleEntity>> loadArticleFromDb(String url) {
        return articleRepository.getArticle(url, false);
    }

    public void addToFavorites(String url, boolean makeFav) {
        articleRepository.addToFavorites(url, makeFav);
    }

    public void deleteArticle(String url) {
        articleRepository.deleteArticle(url);
    }

    public boolean isScrollEnabled() {
        return scrollEnabled;
    }

    public void setScrollSpeed(int scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public void setScrollEnabled(boolean scrollEnabled) {
        this.scrollEnabled = scrollEnabled;
    }

    public int getScrollSpeed() {
        return scrollSpeed;
    }
}
