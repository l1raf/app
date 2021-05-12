package com.liraf.reader.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity
public class ArticleFavUpdate {

    @ColumnInfo(name = "url")
    @SerializedName("uri")
    private final String url;

    @ColumnInfo(name = "favorite")
    private final boolean favorite;

    public String getUrl() {
        return url;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public ArticleFavUpdate(String url, boolean favorite) {
        this.url = url;
        this.favorite = favorite;
    }
}
