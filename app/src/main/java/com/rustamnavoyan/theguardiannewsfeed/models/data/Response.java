package com.rustamnavoyan.theguardiannewsfeed.models.data;

import java.util.Collections;
import java.util.List;

public class Response {
    private String status;
    private String userTier;
    private int total;
    private int startIndex;
    private int pageSize;
    private int currentPage;
    private int pages;
    private String orderBy;
    private List<Result> results = Collections.emptyList();

    public String getStatus() {
        return status;
    }

    public String getUserTier() {
        return userTier;
    }

    public int getTotal() {
        return total;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPages() {
        return pages;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public List<Result> getResults() {
        return results;
    }
}
