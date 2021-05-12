package com.liraf.reader.ui.main;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OnArticleListener {

    private ArticlesAdapter articleAdapter;
    private FragmentHomeBinding binding;
    private ClipboardManager clipboard;
    private ArticleListViewModel articleViewModel;
    private static boolean shouldFetch = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (getActivity() != null)
            clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        initRecyclerView();

        articleViewModel = new ViewModelProvider(this).get(ArticleListViewModel.class);
        articleViewModel.getArticles().observe(getViewLifecycleOwner(), articles -> articleAdapter.setArticles(articles));

        articleViewModel.loadArticles(shouldFetch).observe(getViewLifecycleOwner(), articlesResource -> {
            if (articlesResource != null) {
                if (articlesResource.data != null && articlesResource.data.size() > 0) {
                    List<ArticleEntity> articleEntities = articlesResource.data;

                    List<Article> articles = new ArrayList<>();

                    for (ArticleEntity articleEntity : articleEntities) {
                        Article article = articleEntity.getArticle();
                        articles.add(0, article);
                    }

                    articleViewModel.setArticles(articles);
                    shouldFetch = false;
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
                    if (articleResource != null && articleResource.data != null) {
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
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem item = menu.findItem(R.id.action_paste_url);

        item.setEnabled(clipboard.hasPrimaryClip());

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_paste_url) {
            try {
                Log.d("HomeFragment", "Url from clipboard: " + clipboard.getPrimaryClip().getItemAt(0).getText().toString());
                URL url = new URL(clipboard.getPrimaryClip().getItemAt(0).getText().toString());

                articleViewModel.addArticle(url.toString()).observe(getViewLifecycleOwner(), articleResource -> {
                    if (articleResource != null && articleResource.data != null) {
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
            } catch (MalformedURLException e) {
                Toast.makeText(getActivity(), getResources().getString(R.string.invalid_url), Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        inflater.inflate(R.menu.home_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);

        if (getContext() != null && ((MainActivity) getContext()).getSupportActionBar() != null) {
            SearchView searchView = new SearchView(((MainActivity) getContext()).getSupportActionBar().getThemedContext());

            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
            item.setActionView(searchView);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    articleAdapter.getFilter().filter(newText);
                    return false;
                }
            });
        }
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