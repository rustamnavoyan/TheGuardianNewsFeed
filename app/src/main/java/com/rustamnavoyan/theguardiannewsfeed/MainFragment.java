package com.rustamnavoyan.theguardiannewsfeed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rustamnavoyan.theguardiannewsfeed.adapters.ArticleListAdapter;
import com.rustamnavoyan.theguardiannewsfeed.manage.ArticleDownloader;
import com.rustamnavoyan.theguardiannewsfeed.models.ArticleItem;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainFragment extends Fragment {
    private ArticleDownloader mArticleDownloader;

    private int mPage = 1;
    private ArrayList<ArticleItem> mArticleItems;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        mArticleDownloader = new ArticleDownloader();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.articles_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        ArticleListAdapter adapter = new ArticleListAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                // start loading articles if there are 2 items to reach the last loaded item
                if (totalItemCount <= (lastVisibleItem + 2)) {
                    loadArticles(adapter);
                }
            }
        });

        if (mArticleItems == null) {
            loadArticles(adapter);
        } else {
            adapter.addArticleList(mArticleItems);
        }

        return view;
    }

    private void loadArticles(ArticleListAdapter adapter) {
        mArticleDownloader.downloadArticleList(mPage, articles -> {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    adapter.addArticleList(articles);
                    mArticleItems = new ArrayList<>(adapter.getArticleItemList());
                    mPage++;
                });
            }
        });
    }
}
