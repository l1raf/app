package com.liraf.reader.models.article;

public class TextElement implements ArticleElement {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.Text;
    }
}
