package com.liraf.reader.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.liraf.reader.models.Article;
import com.liraf.reader.models.article.Content;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Article.class, Content.class}, version = 2)
public abstract class ArticleDatabase extends RoomDatabase {

    private static ArticleDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;
    private static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static ExecutorService getDatabaseWriteExecutor() {
        return databaseWriteExecutor;
    }

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
