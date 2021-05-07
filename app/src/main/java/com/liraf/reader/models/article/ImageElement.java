package com.liraf.reader.models.article;

public class ImageElement implements ArticleElement {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.Image;
    }
}
