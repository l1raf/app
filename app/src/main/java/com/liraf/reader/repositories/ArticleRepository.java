package com.liraf.reader.repositories;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.liraf.reader.R;
import com.liraf.reader.api.NetworkService;
import com.liraf.reader.data.ArticleDao;
import com.liraf.reader.data.ArticleDatabase;
import com.liraf.reader.models.Article;
import com.liraf.reader.models.ArticleEntity;
import com.liraf.reader.models.ArticleFavUpdate;
import com.liraf.reader.models.requests.WebPage;
import com.liraf.reader.models.responses.ApiResponse;
import com.liraf.reader.utils.AppExecutors;
import com.liraf.reader.utils.NetworkBoundResource;
import com.liraf.reader.utils.Resource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleRepository {
    private static volatile ArticleRepository instance;
    private final Application application;
    private final ArticleDao articleDao;

    private ArticleRepository(Application application) {
        this.application = application;
        ArticleDatabase articleDatabase = ArticleDatabase.getInstance(application);
        articleDao = articleDatabase.articleDao();
    }

    public static ArticleRepository getInstance(Application application) {
        if (instance == null)
            instance = new ArticleRepository(application);

        return instance;
    }

    public void deleteAllArticlesFromDb() {
        ArticleDatabase.getDatabaseWriteExecutor().execute(articleDao::deleteAllArticles);
    }

    public void saveWebPage(WebPage webPage) {
        NetworkService.getWebService(application).saveWebPage(webPage)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful())
                            Toast.makeText(application, "Successfully added article", Toast.LENGTH_LONG).show(); //TODO: remove
                        else
                            Toast.makeText(application, "Failed to add article", Toast.LENGTH_LONG).show(); //TODO: remove
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(application, application.getResources().getString(R.string.unknown_error), Toast.LENGTH_LONG).show(); //TODO: remove
                    }
                });
    }

    public LiveData<List<ArticleEntity>> loadFavArticlesFromDb() {
        return articleDao.getFavArticles();
    }

    public void addToFavorites(String url, boolean makeFav) {
        ArticleDatabase.getDatabaseWriteExecutor().execute(() -> {
            Log.d("DB", "Make favorite: " + makeFav);
            articleDao.updateFav(new ArticleFavUpdate(url, makeFav));
        });

        NetworkService.getWebService(application).updateArticle(new ArticleFavUpdate(url, makeFav))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d("API", "Make favorite: " + makeFav);

                        if (response.isSuccessful() && makeFav)
                            Toast.makeText(application, "Successfully added to favorites", Toast.LENGTH_LONG).show(); //TODO: remove
                        else if (response.isSuccessful())
                            Toast.makeText(application, "Successfully removed from favorites", Toast.LENGTH_LONG).show(); //TODO: remove
                        else if (makeFav)
                            Toast.makeText(application, "Failed to add to favorites", Toast.LENGTH_LONG).show(); //TODO: remove
                        else
                            Toast.makeText(application, "Failed to remove from favorites", Toast.LENGTH_LONG).show(); //TODO: remove
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(application, application.getResources().getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void deleteArticle(String url) {
        ArticleDatabase.getDatabaseWriteExecutor().execute(() -> articleDao.deleteArticle(url));

        NetworkService.getWebService(application).deleteArticle(url)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful())
                            Toast.makeText(application, "Successfully deleted article", Toast.LENGTH_LONG).show(); //TODO: remove
                        else
                            Toast.makeText(application, "Failed to delete article", Toast.LENGTH_LONG).show(); //TODO: remove
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(application, application.getResources().getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public LiveData<Resource<List<ArticleEntity>>> getAllArticles(boolean shouldFetch) {
        return new NetworkBoundResource<List<ArticleEntity>, List<Article>>(AppExecutors.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull List<Article> articles) {
                if (articles.size() > 0) {
                    for (Article article : articles) {
                        ArticleEntity articleEntity = new ArticleEntity(article, article.getContent());
                        articleDao.insertArticle(articleEntity);
                    }
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<ArticleEntity> data) {
                return shouldFetch;
            }

            @NonNull
            @Override
            protected LiveData<List<ArticleEntity>> loadFromDb() {
                return articleDao.getAllArticles();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Article>>> createCall() {
                return NetworkService.getWebService(application)
                        .getAllArticles();
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ArticleEntity>> getArticle(String url, boolean shouldFetch) {
        return new NetworkBoundResource<ArticleEntity, Article>(AppExecutors.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull Article article) {
                ArticleEntity articleEntity = new ArticleEntity(article, article.getContent());
                articleDao.insertArticle(articleEntity);
            }

            @Override
            protected boolean shouldFetch(@Nullable ArticleEntity data) {
                return shouldFetch;
            }

            @NonNull
            @Override
            protected LiveData<ArticleEntity> loadFromDb() {
                return articleDao.getArticle(url);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Article>> createCall() {
                return NetworkService.getWebService(application)
                        .getArticle(url);
            }
        }.getAsLiveData();
    }
}
