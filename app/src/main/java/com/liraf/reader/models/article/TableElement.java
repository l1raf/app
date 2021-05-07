package com.liraf.reader.models.article;

import java.util.List;

public class TableElement implements ArticleElement {

    private List<List<String>> table;

    public List<List<String>> getTable() {
        return table;
    }

    public void setTable(List<List<String>> table) {
        this.table = table;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.Table;
    }
}
