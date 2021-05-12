package com.liraf.reader.ui.article;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.liraf.reader.R;
import com.liraf.reader.models.article.Content;
import com.liraf.reader.models.article.ElementType;

import java.util.ArrayList;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int IMAGE_TYPE = 1;
    private static final int TEXT_TYPE = 0;
    private static final int TABLE_TYPE = 2;
    private List<Content> articleContent;
    private final ArticleActivity activity;
    private int textSize;

    public ArticleAdapter(ArticleActivity activity) {
        this.activity = activity;
        articleContent = new ArrayList<>();
        textSize = 0;
    }

    public void increaseTextSize(int textSize) {
        this.textSize = textSize;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (articleContent.get(position).getType() == ElementType.Text.ordinal())
            return TEXT_TYPE;
        else if (articleContent.get(position).getType() == ElementType.Image.ordinal())
            return IMAGE_TYPE;
        else
            return TABLE_TYPE;
    }

    public void setContent(List<Content> content) {
        articleContent = content;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;

        if (viewType == TEXT_TYPE) {
            view = layoutInflater.inflate(R.layout.article_text_view, parent, false);
            return new TextViewHolder(view);
        } else if (viewType == IMAGE_TYPE) {
            view = layoutInflater.inflate(R.layout.article_image_view, parent, false);
            return new ImageViewHolder(view);
        } else {
            view = layoutInflater.inflate(R.layout.article_table_view, parent, false);
            return new TableViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TEXT_TYPE:
                if (articleContent.get(position) != null && holder instanceof TextViewHolder)
                    ((TextViewHolder) holder).onBind(articleContent.get(position), textSize);

            case IMAGE_TYPE:
                if (articleContent.get(position) != null && holder instanceof ImageViewHolder)
                    ((ImageViewHolder) holder).onBind(articleContent.get(position));

            case TABLE_TYPE:
                if (articleContent.get(position) != null && holder instanceof TableViewHolder)
                    ((TableViewHolder) holder).onBind(articleContent.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return articleContent.size();
    }

    static class TableViewHolder extends RecyclerView.ViewHolder {

        private final WebView webView;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);

            this.webView = itemView.findViewById(R.id.article_content_table);
        }

        public void onBind(Content content) {
            webView.loadDataWithBaseURL("", content.getText(), "text/html", "UTF-8", "");
        }
    }

    static class TextViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.article_content_text);
        }

        public void onBind(Content content, int textSize) {
            textView.setText(Html.fromHtml(content.getText(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView.getTextSize() + textSize);
        }
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.article_content_image);
        }

        public void onBind(Content content) {
            Glide.with(activity)
                    .load(content.getUrl())
                    .into(imageView);
        }
    }
}
