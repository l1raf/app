package com.liraf.reader.ui.article;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.liraf.reader.R;
import com.liraf.reader.models.Article;
import com.liraf.reader.models.ArticleEntity;
import com.liraf.reader.ui.main.MainActivity;
import com.liraf.reader.viewmodels.ArticleViewModel;

public class ArticleActivity extends AppCompatActivity {

    private String articleUrl;
    private ArticleViewModel articleViewModel;
    private Article article;
    private ArticleAdapter articleAdapter;
    private ManagerWithSmoothScroller linearLayoutManagerWithSmoothScroller;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        articleViewModel = new ViewModelProvider(this).get(ArticleViewModel.class);

        Toolbar toolbar = findViewById(R.id.article_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recycler_view_article_content);

        linearLayoutManagerWithSmoothScroller = new ManagerWithSmoothScroller(this);
        recyclerView.setLayoutManager(linearLayoutManagerWithSmoothScroller);

        articleAdapter = new ArticleAdapter(this);
        recyclerView.setAdapter(articleAdapter);

        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            if (articleViewModel.isScrollEnabled() && Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) // collapsed
                recyclerView.smoothScrollToPosition(articleAdapter.getItemCount());
        });

        if (getIntent().hasExtra("article")) {
            articleUrl = getIntent().getStringExtra("article");

            if (articleUrl != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);

                articleViewModel.loadArticleFromDb(articleUrl).observe(this, articleResource -> {
                    if (articleResource != null && articleResource.data != null) {
                        ArticleEntity articleEntity = articleResource.data;
                        article = articleEntity.getArticle();
                        articleAdapter.setContent(articleEntity.getContent());
                    }
                });
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.add_to_fav);

        if (article != null && !article.isFavorite())
            item.setTitle(R.string.add_to_favorites);
        else
            item.setTitle(R.string.remove_from_favorites);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.article_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete_article) {
            articleViewModel.deleteArticle(articleUrl);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.add_to_fav) {
            if (article != null)
                articleViewModel.addToFavorites(articleUrl, !article.isFavorite());
        } else if (id == R.id.web) {
            if (article != null) {
                if (isConnected()) {
                    Intent intent = new Intent(this, WebActivity.class);
                    intent.putExtra("articleUrl", article.getUrl());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.internet_required, Toast.LENGTH_LONG).show();
                }
            }
        } else if (id == R.id.text) {
            initBottomSheet();
        }

        return true;
    }

    public boolean isConnected() {
        String command = "ping -i 5 -c 1 google.com";
        try {
            return Runtime.getRuntime().exec(command).waitFor() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void initBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ArticleActivity.this, R.style.BottomSheetTheme);

        View bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(
                        R.layout.bottom_sheet_font_settings,
                        findViewById(R.id.bottom_sheet)
                );

        SeekBar seekBar = bottomSheetView.findViewById(R.id.scroll_speed_seek_bar);
        seekBar.setProgress(articleViewModel.getScrollSpeed());

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switchButton = bottomSheetView.findViewById(R.id.switch_enable_scroll);

        switchButton.setChecked(articleViewModel.isScrollEnabled());
        switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> articleViewModel.setScrollEnabled(isChecked));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                articleViewModel.setScrollSpeed(progress);
                linearLayoutManagerWithSmoothScroller.setScrollSpeed(11000 - progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        bottomSheetView.findViewById(R.id.image_view_increase_text).setOnClickListener(v -> articleAdapter.increaseTextSize(1));

        bottomSheetView.findViewById(R.id.image_view_decrease_text).setOnClickListener(v -> articleAdapter.increaseTextSize(-1));

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
}