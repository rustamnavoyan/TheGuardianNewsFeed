package com.rustamnavoyan.theguardiannewsfeed;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rustamnavoyan.theguardiannewsfeed.adapters.ArticleListAdapter;
import com.rustamnavoyan.theguardiannewsfeed.database.ArticleTable;
import com.rustamnavoyan.theguardiannewsfeed.manage.ArticleDownloader;
import com.rustamnavoyan.theguardiannewsfeed.manage.PreferenceHelper;
import com.rustamnavoyan.theguardiannewsfeed.models.ArticleItem;
import com.rustamnavoyan.theguardiannewsfeed.utils.ConnectionUtil;
import com.rustamnavoyan.theguardiannewsfeed.manage.PeriodicDownloadManager;
import com.rustamnavoyan.theguardiannewsfeed.utils.DateTimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainFragment extends Fragment implements
        ArticleListAdapter.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PINNED_ARTICLE_LOADER_ID = 1;
    private static final int SAVED_ARTICLE_LOADER_ID = 2;

    private ArticleDownloader mArticleDownloader;

    private int mPage = 1;
    private ArrayList<ArticleItem> mArticleItems;

    private RecyclerView mPinnedRecyclerView;
    private ArticleListAdapter mPinnedAdapter;

    private RecyclerView mRecyclerView;
    private ArticleListAdapter mAdapter;

    private boolean mConnected;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        mArticleDownloader = new ArticleDownloader();
    }

    @Override
    public void onResume() {
        super.onResume();

        PeriodicDownloadManager.cancel(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();

        PeriodicDownloadManager.schedule(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        int screenWidth = getScreenWidth();
        mPinnedRecyclerView = view.findViewById(R.id.pinned_articles_recycler_view);
        mPinnedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        mPinnedAdapter = new ArticleListAdapter(true, screenWidth, this);
        mPinnedRecyclerView.setAdapter(mPinnedAdapter);

        mRecyclerView = view.findViewById(R.id.articles_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ArticleListAdapter(false, screenWidth, this);
        mRecyclerView.setAdapter(mAdapter);
        mConnected = ConnectionUtil.isConnected(getContext());
        if (mConnected) {
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        mAdapter.setLoading();
                        loadArticles();
                    }
                }
            });
            if (mArticleItems == null) {
                loadArticles();
            } else {
                mAdapter.addArticleList(mArticleItems);
            }
        }

        return view;
    }

    private void loadArticles() {
        mArticleDownloader.downloadArticleList(mPage, articles -> {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (mPage == 1) {
                        Date date = DateTimeUtil.parseDate(articles.get(0).getPublishedDate());
                        PreferenceHelper.getInstance(getContext()).saveDate(date);
                    }
                    mAdapter.addArticleList(articles);
                    mArticleItems = new ArrayList<>(mAdapter.getArticleItemList());
                    mPage++;
                });
            }
        });
    }

    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    @Override
    public void onItemClicked(ArticleItem article) {
        Intent intent = new Intent(getContext(), ArticlePageActivity.class);
        intent.putExtra(ArticlePageActivity.EXTRA_ARTICLE_ITEM, article);
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LoaderManager.getInstance(getActivity()).initLoader(PINNED_ARTICLE_LOADER_ID, null, this);
        if (!mConnected) {
            LoaderManager.getInstance(getActivity()).initLoader(SAVED_ARTICLE_LOADER_ID, null, this);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case PINNED_ARTICLE_LOADER_ID:
                return new CursorLoader(getContext(), ArticleTable.PINNED_CONTENT_URI, null,
                        null, null, null);

            case SAVED_ARTICLE_LOADER_ID:
                return new CursorLoader(getContext(), ArticleTable.SAVED_CONTENT_URI, null,
                        null, null, null);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case PINNED_ARTICLE_LOADER_ID:
                if (data != null && data.getCount() > 0) {
                    // TODO Probably not efficient but we can reuse ArticleListAdapter
                    List<ArticleItem> articles = convertToArticles(data);

                    mPinnedRecyclerView.setVisibility(View.VISIBLE);
                    mPinnedAdapter.setArticleList(articles);
                } else {
                    mPinnedRecyclerView.setVisibility(View.GONE);
                    mPinnedAdapter.clearArticles();
                }
                break;

            case SAVED_ARTICLE_LOADER_ID:
                if (data != null && data.getCount() > 0) {
                    List<ArticleItem> articles = convertToArticles(data);
                    mAdapter.setArticleList(articles);
                } else {
                    mAdapter.clearArticles();
                }
                break;
        }
    }

    private List<ArticleItem> convertToArticles(Cursor cursor) {
        List<ArticleItem> articles = new ArrayList<>();
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            articles.add(ArticleTable.parseArticleItem(cursor));
        }

        return articles;
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
