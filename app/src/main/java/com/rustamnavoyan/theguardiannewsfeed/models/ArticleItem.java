package com.rustamnavoyan.theguardiannewsfeed.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ArticleItem implements Parcelable {
    private String mId;
    private String mThumbnailUrl;
    private String mTitle;
    private String mCategory;
    private String mApiUrl;
    private boolean mPinned;

    public ArticleItem(String id) {
        mId = id;
    }

    protected ArticleItem(Parcel in) {
        mId = in.readString();
        mThumbnailUrl = in.readString();
        mTitle = in.readString();
        mCategory = in.readString();
        mApiUrl = in.readString();
        mPinned = in.readByte() != 0;
    }

    public static final Creator<ArticleItem> CREATOR = new Creator<ArticleItem>() {
        @Override
        public ArticleItem createFromParcel(Parcel in) {
            return new ArticleItem(in);
        }

        @Override
        public ArticleItem[] newArray(int size) {
            return new ArticleItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mThumbnailUrl);
        parcel.writeString(mTitle);
        parcel.writeString(mCategory);
        parcel.writeString(mApiUrl);
        parcel.writeByte((byte) (mPinned ? 1 : 0));
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        mThumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getApiUrl() {
        return mApiUrl;
    }

    public void setApiUrl(String apiUrl) {
        mApiUrl = apiUrl;
    }

    public boolean isPinned() {
        return mPinned;
    }

    public void setPinned(boolean pinned) {
        mPinned = pinned;
    }
}
