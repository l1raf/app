package com.liraf.reader.ui.main;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.liraf.reader.R;
import com.liraf.reader.databinding.ArticleItemBinding;
import com.liraf.reader.models.Article;
import com.liraf.reader.ui.article.OnArticleListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> implements
        ListPreloader.PreloadModelProvider<String> {

    private List<Article> articles = new ArrayList<>();
    private final RequestManager requestManager;
    private final ViewPreloadSizeProvider<String> viewPreloadSizeProvider;
    private final OnArticleListener onArticleListener;

    public ArticleAdapter(RequestManager requestManager,
                          ViewPreloadSizeProvider<String> viewPreloadSizeProvider,
                          OnArticleListener onArticleListener) {
        this.requestManager = requestManager;
        this.viewPreloadSizeProvider = viewPreloadSizeProvider;
        this.onArticleListener = onArticleListener;
    }

    public Article getSelectedArticle(int position) {
        if (articles != null && articles.size() > 0)
            return articles.get(position);

        return null;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_item, parent, false);
        return new ArticleViewHolder(
                itemView,
                onArticleListener,
                requestManager,
                viewPreloadSizeProvider,
                ArticleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articles.get(position);
        if (article != null)
            holder.onBind(article);

        holder.binding.getRoot().setOnClickListener(view -> onArticleListener.onArticleClick(position));
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void setArticles(List<Article> articles) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ArticleComparator(this.articles, articles));
        this.articles = articles;
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public List<String> getPreloadItems(int position) {
        String url = articles.get(position).getImage();

        if (TextUtils.isEmpty(url))
            return Collections.emptyList();

        return Collections.singletonList(url);
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull String item) {
        return requestManager.load(item);
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final RequestManager requestManager;
        private final ViewPreloadSizeProvider<String> viewPreloadSizeProvider;
        private final ArticleItemBinding binding;
        private final OnArticleListener onArticleListener;

        public ArticleViewHolder(@NonNull View itemView,
                                 OnArticleListener onArticleListener,
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

    static class ArticleComparator extends DiffUtil.Callback {

        private final List<Article> oldList;
        private final List<Article> newList;

        public ArticleComparator(List<Article> newList, List<Article> oldList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getUrl() != null
                    && oldList.get(oldItemPosition).getUrl().equals(newList.get(newItemPosition).getUrl());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Article oldItem = oldList.get(oldItemPosition);
            Article newItem = newList.get(newItemPosition);

            return oldItem.getUrl().equals(newItem.getUrl())
                    && (oldItem.getTitle() != null && oldItem.getTitle().equals(newItem.getTitle()))
                    && (oldItem.getContent() != null && oldItem.getContent().equals(newItem.getContent()))
                    && (oldItem.getDescription() != null && oldItem.getDescription().equals(newItem.getDescription()))
                    && (oldItem.getImage() != null && oldItem.getImage().equals(newItem.getImage()));
        }
    }
}
