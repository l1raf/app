package com.liraf.reader.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.liraf.reader.R;
import com.liraf.reader.databinding.FragmentFavoritesBinding;
import com.liraf.reader.models.Article;
import com.liraf.reader.models.ArticleEntity;
import com.liraf.reader.ui.article.ArticleActivity;
import com.liraf.reader.ui.article.OnArticleListener;
import com.liraf.reader.viewmodels.ArticleListViewModel;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment implements OnArticleListener {

    private ArticlesAdapter articleAdapter;
    private FragmentFavoritesBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.favRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        initRecyclerView();

        ArticleListViewModel articleViewModel = new ViewModelProvider(this).get(ArticleListViewModel.class);
        articleViewModel.getArticles().observe(getViewLifecycleOwner(), articles -> articleAdapter.setArticles(articles));

        articleViewModel.getFavArticles().observe(getViewLifecycleOwner(), articleEntities -> {
            if (articleEntities != null && articleEntities.size() > 0) {
                if (articleEntities != null) {
                    List<Article> articles = new ArrayList<>();

                    for (ArticleEntity articleEntity : articleEntities) {
                        Article article = articleEntity.getArticle();
                        articles.add(0, article);
                    }

                    articleViewModel.setArticles(articles);
                }
            }
        });

        return view;
    }

    @Override
    public void onArticleClick(int position) {
        Intent intent = new Intent(getActivity(), ArticleActivity.class);
        intent.putExtra("article", articleAdapter.getSelectedArticle(position).getUrl());
        startActivity(intent);
    }

    private void initRecyclerView() {
        ViewPreloadSizeProvider<String> viewPreloadSizeProvider = new ViewPreloadSizeProvider<>();
        articleAdapter = new ArticlesAdapter(initGlide(), viewPreloadSizeProvider, this);

        RecyclerViewPreloader<String> preloader = new RecyclerViewPreloader<>(
                Glide.with(this),
                articleAdapter,
                viewPreloadSizeProvider,
                30);

        binding.favRecyclerView.addOnScrollListener(preloader);
        binding.favRecyclerView.setAdapter(articleAdapter);
    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}