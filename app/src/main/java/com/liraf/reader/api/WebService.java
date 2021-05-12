package com.liraf.reader.api;

import androidx.lifecycle.LiveData;

import com.liraf.reader.models.Article;
import com.liraf.reader.models.ArticleFavUpdate;
import com.liraf.reader.models.requests.AuthUser;
import com.liraf.reader.models.requests.RefreshTokenRequest;
import com.liraf.reader.models.requests.RegisterUser;
import com.liraf.reader.models.requests.UpdateUser;
import com.liraf.reader.models.requests.WebPage;
import com.liraf.reader.models.responses.ApiResponse;
import com.liraf.reader.models.responses.AuthUserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface WebService {
    @Headers("Content-type: application/json")
    @POST("api/account/authenticate")
    Call<AuthUserResponse> authUser(@Body AuthUser user);

    @Headers("Content-type: application/json")
    @POST("api/account/register")
    Call<Void> registerUser(@Body RegisterUser user);

    @Headers("Content-type: application/json")
    @GET("api/webpages/article")
    LiveData<ApiResponse<Article>> getArticle(@Query("uri") String url);

    @Headers("Content-type: application/json")
    @GET("api/webpages/articles")
    LiveData<ApiResponse<List<Article>>> getAllArticles();

    @Headers("Content-type: application/json")
    @POST("api/account/refresh-token")
    Call<AuthUserResponse> refreshToken(@Body  RefreshTokenRequest request);

    @Headers("Content-type: application/json")
    @POST("api/webpages/")
    Call<Void> saveWebPage(@Body WebPage webPage);

    @DELETE("api/webpages/")
    Call<Void> deleteArticle(@Query("uri") String url);

    @PUT("api/webpages/")
    Call<Void> updateArticle(@Body ArticleFavUpdate articleFavUpdate);

    @PUT("api/users/")
    Call<Void> updateUser(@Body UpdateUser user);
}
