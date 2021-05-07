package com.liraf.reader.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.liraf.reader.models.Article;
import com.liraf.reader.models.article.Content;

@Database(entities = {Article.class, Content.class}, version = 1)
public abstract class ArticleDatabase extends RoomDatabase {

    private static ArticleDatabase instance;

    public abstract ArticleDao articleDao();

    public static synchronized ArticleDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ArticleDatabase.class, "articleDB")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }
}
