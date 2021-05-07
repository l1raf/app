package com.liraf.reader.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.liraf.reader.R;
import com.liraf.reader.databinding.FragmentHomeBinding;
import com.liraf.reader.models.Article;
import com.liraf.reader.models.ArticleEntity;
import com.liraf.reader.models.requests.WebPage;
import com.liraf.reader.ui.article.ArticleActivity;
import com.liraf.reader.ui.article.OnArticleListener;
import com.liraf.reader.utils.Resource;
import com.liraf.reader.viewmodels.ArticleListViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OnArticleListener {

    private ArticleAdapter articleAdapter;
    private FragmentHomeBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        initRecyclerView();

        ArticleListViewModel articleViewModel = new ViewModelProvider(this).get(ArticleListViewModel.class);
        articleViewModel.getArticles().observe(getViewLifecycleOwner(), articles -> articleAdapter.setArticles(articles));

        articleViewModel.loadArticles(true).observe(getViewLifecycleOwner(), articlesResource -> {
            if (articlesResource != null) {
                if (articlesResource.data != null) {
                    List<ArticleEntity> articleEntities = articlesResource.data;

                    if (articleEntities != null) {
                        List<Article> articles = new ArrayList<>();

                        for (ArticleEntity articleEntity : articleEntities) {
                            Article article = articleEntity.getArticle();
                            articles.add(0, article);
                        }

                        articleViewModel.setArticles(articles);
                    }
                } else if (articlesResource.status == Resource.Status.ERROR) {
                    Toast.makeText(getContext(), articlesResource.message, Toast.LENGTH_LONG).show();
                }
            }
        });

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            String url = bundle.getString("url");

            if (url != null) {
                articleViewModel.addArticle(url).observe(getViewLifecycleOwner(), articleResource -> {
                    if (articleResource != null) {
                        if (articleResource.status == Resource.Status.ERROR) {
                            Toast.makeText(getContext(), articleResource.message, Toast.LENGTH_LONG).show();
                        } else if (articleResource.status == Resource.Status.SUCCESS) {
                            articleViewModel.saveWebPage(new WebPage(
                                    articleResource.data.getArticle().getTitle(),
                                    articleResource.data.getArticle().getUrl())
                            );
                        }
                    }
                });
            }
        }

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
        articleAdapter = new ArticleAdapter(initGlide(), viewPreloadSizeProvider, this);

        RecyclerViewPreloader<String> preloader = new RecyclerViewPreloader<String>(
                Glide.with(this),
                articleAdapter,
                viewPreloadSizeProvider,
                30);

        binding.recyclerView.addOnScrollListener(preloader);
        binding.recyclerView.setAdapter(articleAdapter);
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