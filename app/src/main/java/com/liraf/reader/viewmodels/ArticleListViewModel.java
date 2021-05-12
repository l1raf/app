package com.liraf.reader.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.liraf.reader.models.Article;
import com.liraf.reader.models.ArticleEntity;
import com.liraf.reader.models.requests.WebPage;
import com.liraf.reader.repositories.ArticleRepository;
import com.liraf.reader.utils.Resource;

import java.util.List;

public class ArticleListViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Article>> articles = new MutableLiveData<>();
    private final ArticleRepository articleRepository;

    public ArticleListViewModel(@NonNull Application application) {
        super(application);
        this.articleRepository = ArticleRepository.getInstance(application);
    }

    public void setArticles(List<Article> articles) {
        this.articles.postValue(articles);
    }

    public LiveData<Resource<List<ArticleEntity>>> loadArticles(boolean shouldFetch) {
        return articleRepository.getAllArticles(shouldFetch);
    }

    public LiveData<List<ArticleEntity>> getFavArticles() {
        return articleRepository.loadFavArticlesFromDb();
    }

    public LiveData<List<Article>> getArticles() {
        return articles;
    }

    public LiveData<Resource<ArticleEntity>> addArticle(String url) {
        return articleRepository.getArticle(url, true);
    }

    public void saveWebPage(WebPage webPage) {
        articleRepository.saveWebPage(webPage);
    }
}
