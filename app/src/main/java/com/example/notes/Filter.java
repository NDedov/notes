package com.example.notes;

import android.os.Parcel;
import android.os.Parcelable;

public class Filter implements Parcelable {
    private int currentFilterCategory;
    private boolean isFavoriteShow;
    private String searchString;
    static int defaultFilterCategory = Note.categories.length;

    public Filter(int currentFilterCategory, boolean isFavoriteShow, String searchString) {
        this.currentFilterCategory = currentFilterCategory;
        this.isFavoriteShow = isFavoriteShow;
        this.searchString = searchString;
    }

    protected Filter(Parcel in) {
        currentFilterCategory = in.readInt();
        isFavoriteShow = in.readByte() != 0;
        searchString = in.readString();
    }
    public boolean isShow(Note note){
        boolean fFilter = false;
        boolean fFavorite = false;
        boolean fSearch = true;
        if (note.getCategoryID() == currentFilterCategory || currentFilterCategory == defaultFilterCategory)
            fFilter = true;
        if (!isFavoriteShow || note.isFavourite())
            fFavorite = true;
        //todo search

        return fFilter && fFavorite && fSearch;
    }

    public static final Creator<Filter> CREATOR = new Creator<Filter>() {
        @Override
        public Filter createFromParcel(Parcel in) {
            return new Filter(in);
        }

        @Override
        public Filter[] newArray(int size) {
            return new Filter[size];
        }
    };

    public void setFavoriteShow(boolean favoriteShow) {
        isFavoriteShow = favoriteShow;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public void setCurrentFilterCategory(int currentFilterCategory) {
        this.currentFilterCategory = currentFilterCategory;
    }

    public int getCurrentFilterCategory() {
        return currentFilterCategory;
    }

    public boolean isFavoriteShow() {
        return isFavoriteShow;
    }

    public String getSearchString() {
        return searchString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(currentFilterCategory);
        parcel.writeByte((byte) (isFavoriteShow ? 1 : 0));
        parcel.writeString(searchString);
    }
}
