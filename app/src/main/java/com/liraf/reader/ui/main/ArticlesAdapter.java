package com.liraf.reader.ui.main;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.liraf.reader.databinding.ArticleItemBinding;
import com.liraf.reader.models.Article;
import com.liraf.reader.ui.article.OnArticleListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder> implements
        ListPreloader.PreloadModelProvider<String>, Filterable {

    private List<Article> articles;
    private final AsyncListDiffer<Article> mDiffer = new AsyncListDiffer<>(this, diffCallback);
    private final RequestManager requestManager;
    private final ViewPreloadSizeProvider<String> viewPreloadSizeProvider;
    private final OnArticleListener onArticleListener;

    public ArticlesAdapter(RequestManager requestManager,
                           ViewPreloadSizeProvider<String> viewPreloadSizeProvider,
                           OnArticleListener onArticleListener) {
        this.requestManager = requestManager;
        this.viewPreloadSizeProvider = viewPreloadSizeProvider;
        this.onArticleListener = onArticleListener;
        articles = new ArrayList<>();
    }

    public Article getSelectedArticle(int position) {
        return mDiffer.getCurrentList().get(position);
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArticleViewHolder(
                onArticleListener,
                requestManager,
                viewPreloadSizeProvider,
                ArticleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = mDiffer.getCurrentList().get(position);
        if (article != null)
            holder.onBind(article);

        holder.binding.getRoot().setOnClickListener(view -> onArticleListener.onArticleClick(position));
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void setArticles(List<Article> articles) {
        mDiffer.submitList(articles);
        this.articles = mDiffer.getCurrentList();
    }

    @NonNull
    @Override
    public List<String> getPreloadItems(int position) {
        String url = mDiffer.getCurrentList().get(position).getImage();

        if (TextUtils.isEmpty(url))
            return Collections.emptyList();

        return Collections.singletonList(url);
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull String item) {
        return requestManager.load(item);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Article> filteredList = new ArrayList<>();

            if (constraint.toString().isEmpty()) {
                filteredList = articles;
            } else {
                String query = constraint.toString().toLowerCase();

                for (Article article : mDiffer.getCurrentList()) {
                    if (article.getTitle() != null && article.getTitle().toLowerCase().contains(query)) {
                        filteredList.add(article);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mDiffer.submitList((List<Article>) results.values);
        }
    };

    static class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final RequestManager requestManager;
        private final ViewPreloadSizeProvider<String> viewPreloadSizeProvider;
        private final ArticleItemBinding binding;
        private final OnArticleListener onArticleListener;

        public ArticleViewHolder(OnArticleListener onArticleListener,
                                 RequestManager requestManager,
                                 ViewPreloadSizeProvider<String> viewPreloadSizeProvider,
                                 ArticleItemBinding binding) {
            super(binding.getRoot());

            this.onArticleListener = onArticleListener;
            this.requestManager = requestManager;
            this.viewPreloadSizeProvider = viewPreloadSizeProvider;
            this.binding = binding;
        }

        public void onBind(Article article) {
            binding.textViewTitle.setText(article.getTitle());
            binding.textViewDescription.setText(article.getDescription());

            if (article.getImage() != null) {
                requestManager.load(article.getImage())
                        .into(binding.imgView);

                viewPreloadSizeProvider.setView(binding.imgView);
            }
        }

        @Override
        public void onClick(View v) {
            onArticleListener.onArticleClick(getAdapterPosition());
        }
    }

    private static final DiffUtil.ItemCallback<Article> diffCallback
            = new DiffUtil.ItemCallback<Article>() {
        @Override
        public boolean areItemsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
            return oldItem.getUrl().equals(newItem.getUrl());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
            return oldItem.getUrl().equals(newItem.getUrl())
                    && (oldItem.getTitle() != null && oldItem.getTitle().equals(newItem.getTitle()))
                    && (oldItem.getContent() != null && oldItem.getContent().equals(newItem.getContent()))
                    && (oldItem.getDescription() != null && oldItem.getDescription().equals(newItem.getDescription()))
                    && (oldItem.getImage() != null && oldItem.getImage().equals(newItem.getImage()));
        }
    };
}
