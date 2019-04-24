package com.rustamnavoyan.theguardiannewsfeed.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rustamnavoyan.theguardiannewsfeed.R;
import com.rustamnavoyan.theguardiannewsfeed.models.ArticleItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.AbstractViewHolder> {

    public interface OnItemClickListener {
        void onItemClicked(ArticleItem article);
    }

    static abstract class AbstractViewHolder extends RecyclerView.ViewHolder {
        AbstractViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class ArticleViewHolder extends AbstractViewHolder {
        private ImageView mThumbnail;
        private TextView mTitle;
        private TextView mCategory;

        ArticleViewHolder(View itemView) {
            super(itemView);

            mThumbnail = itemView.findViewById(R.id.thumbnail);
            mTitle = itemView.findViewById(R.id.article_title);
            mCategory = itemView.findViewById(R.id.article_category);
        }
    }

    static class ProgressViewHolder extends AbstractViewHolder {
        ProgressViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_PROGRESS = 1;

    private List<ArticleItem> mArticleItemList = new ArrayList<>();
    private OnItemClickListener mItemClickListener;
    private boolean mPinned;
    private int mScreenWidth;
    private boolean mLoading;

    public ArticleListAdapter(boolean pinned, int screenWidth, OnItemClickListener itemClickListener) {
        mPinned = pinned;
        mScreenWidth = screenWidth;
        mItemClickListener = itemClickListener;
    }

    public void setLoading() {
        if (mLoading) {
            return;
        }
        mLoading = true;
        notifyItemInserted(mArticleItemList.size());
    }

    private void setLoaded() {
        if (!mLoading) {
            return;
        }
        mLoading = false;
        notifyItemRemoved(mArticleItemList.size());
    }

    public void addArticleList(List<ArticleItem> articleItemList) {
        setLoaded();

        int start = mArticleItemList.size();
        mArticleItemList.addAll(articleItemList);
        notifyItemRangeInserted(start, articleItemList.size());
    }

    public List<ArticleItem> getArticleItemList() {
        return mArticleItemList;
    }

    public void setArticleList(List<ArticleItem> articleList) {
        setLoaded();
        mArticleItemList = articleList;
        notifyDataSetChanged();
    }

    public void clearArticles() {
        setLoaded();
        mArticleItemList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_article, parent, false);
            ArticleViewHolder holder = new ArticleViewHolder(view);
            if (mPinned) {
                view.setBackgroundColor(parent.getContext().getResources().getColor(R.color.pinned_bg_color));
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = (int) (mScreenWidth * 0.8);
            }
            view.setOnClickListener(v -> {
                mItemClickListener.onItemClicked(mArticleItemList.get(holder.getAdapterPosition()));
            });
            return holder;
        }

        return new ProgressViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_progress, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder holder, int position) {
        if (holder instanceof ArticleViewHolder) {
            ArticleViewHolder articleViewHolder = (ArticleViewHolder) holder;
            ArticleItem articleItem = mArticleItemList.get(position);
            articleViewHolder.mTitle.setText(articleItem.getTitle());
            articleViewHolder.mCategory.setText(articleItem.getCategory());
            Picasso.get().load(articleItem.getThumbnailUrl()).into(articleViewHolder.mThumbnail);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mArticleItemList.size()) {
            return VIEW_TYPE_ITEM;
        }
        return VIEW_TYPE_PROGRESS;
    }

    @Override
    public int getItemCount() {
        if (mArticleItemList.isEmpty()) {
            return 0;
        }
        // 1 for progress
        return mPinned || !mLoading ? mArticleItemList.size() : mArticleItemList.size() + 1;
    }
}
