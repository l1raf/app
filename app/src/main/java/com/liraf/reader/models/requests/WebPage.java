package com.liraf.reader.models.requests;

public class WebPage {
    private final String title;
    private final String uri;

    public WebPage(String title, String uri) {
        this.title = title;
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public String getUri() {
        return uri;
    }
}
