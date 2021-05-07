package com.liraf.reader.ui.article;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.liraf.reader.R;
import com.liraf.reader.models.ArticleEntity;
import com.liraf.reader.models.article.Content;
import com.liraf.reader.models.article.ElementType;
import com.liraf.reader.ui.main.MainActivity;
import com.liraf.reader.viewmodels.ArticleViewModel;

public class ArticleActivity extends AppCompatActivity {

    private String articleUrl;
    private ArticleViewModel articleViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        articleViewModel = new ViewModelProvider(this).get(ArticleViewModel.class);
        LinearLayout linearLayout = findViewById(R.id.scrollview_linear_layout);
        NestedScrollView scrollView = findViewById(R.id.scroll_view);
        Toolbar toolbar = findViewById(R.id.article_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

//        button.setOnClickListener(v -> {
//            int targetScrollY = scrollView.getChildAt(0).getHeight();
//
//            ValueAnimator smoothScrollAnimation =
//                    ValueAnimator.ofInt(scrollView.getScrollY(), targetScrollY);
//
//            smoothScrollAnimation.setDuration(6 * targetScrollY);
//
//            smoothScrollAnimation.addUpdateListener(animation -> {
//                int scrollTo = (Integer) animation.getAnimatedValue();
//                scrollView.scrollTo(0, scrollTo);
//            });
//
//            smoothScrollAnimation.start();
//        });

        if (getIntent().hasExtra("article")) {
            articleUrl = getIntent().getStringExtra("article");

            if (articleUrl != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);

                articleViewModel.loadArticleFromDb(articleUrl).observe(this, articleResource -> {
                    if (articleResource != null && articleResource.data != null) {
                        ArticleEntity articleEntity = articleResource.data;

                        for (Content content : articleEntity.getContent()) {
                            if (content.getType() == ElementType.Text.ordinal()) {
                                TextView textView = new TextView(this);

                                textView.setText(Html.fromHtml(content.getText(), HtmlCompat.FROM_HTML_MODE_LEGACY));
                                textView.setTextAppearance(R.style.TextViewWhite);
                                textView.setLinkTextColor(getColor(R.color.purple_700));

                                textView.setMovementMethod(LinkMovementMethod.getInstance());

                                linearLayout.addView(textView);
                            } else if (content.getType() == ElementType.Image.ordinal()) {
                                ImageView imageView = new ImageView(this);

                                Glide.with(this)
                                        .load(content.getUrl())
                                        .into(imageView);

                                linearLayout.addView(imageView);
                            } else if (content.getType() == ElementType.Table.ordinal()) {
                                WebView webView = new WebView(this);
                                webView.loadDataWithBaseURL("", content.getText(), "text/html", "UTF-8", "");
                                linearLayout.addView(webView);
                            }
                        }
                    }
                });
            }
        }
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
        }

        return true;
    }
}