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

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ArticleViewHolder> {

    public interface OnItemClickListener {
        void onItemClicked(ArticleItem article);
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder {
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

    private List<ArticleItem> mArticleItemList = new ArrayList<>();
    private OnItemClickListener mItemClickListener;

    public ArticleListAdapter(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void addArticleList(List<ArticleItem> articleItemList) {
        int start = mArticleItemList.size();
        mArticleItemList.addAll(articleItemList);
        notifyItemRangeInserted(start, articleItemList.size());
    }

    public List<ArticleItem> getArticleItemList() {
        return mArticleItemList;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_article, parent, false);
        ArticleViewHolder holder = new ArticleViewHolder(view);
        view.setOnClickListener(v -> {
            mItemClickListener.onItemClicked(mArticleItemList.get(holder.getAdapterPosition()));
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        ArticleItem articleItem = mArticleItemList.get(position);
        holder.mTitle.setText(articleItem.getTitle());
        holder.mCategory.setText(articleItem.getCategory());
        Picasso.get().load(articleItem.getThumbnailUrl()).into(holder.mThumbnail);
    }

    @Override
    public int getItemCount() {
        return mArticleItemList.size();
    }
}
