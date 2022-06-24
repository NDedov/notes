package com.example.notes.fragments.filter

import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator
import com.example.notes.fragments.notes.Note
import java.util.*

class Filter : Parcelable {
    var currentFilterCategory: Int
    var isFavoriteShow: Boolean
    private var searchString: String?
    private val defaultFilterCategory = Note.categories.size

    constructor(currentFilterCategory: Int, isFavoriteShow: Boolean, searchString: String?) {
        this.currentFilterCategory = currentFilterCategory
        this.isFavoriteShow = isFavoriteShow
        this.searchString = searchString
    }

    protected constructor(`in`: Parcel) {
        currentFilterCategory = `in`.readInt()
        isFavoriteShow = `in`.readByte().toInt() != 0
        searchString = `in`.readString()
    }

    /**
     * Метод проверяющий подходит ли заметка под условия фильтра
     * @param note заметка
     * @return да/нет
     */
    fun isShow(note: Note): Boolean {
        var fFilter = false
        var fFavorite = false
        var fSearch = false

        if (note.getCategoryID() == currentFilterCategory ||
                currentFilterCategory == defaultFilterCategory) fFilter = true
        if (!isFavoriteShow || note.isFavourite()) fFavorite = true
        if (searchString != null) {
            if (note.getText()!!.lowercase(Locale.getDefault()).contains(searchString!!.lowercase(Locale.getDefault()))
                    || note.getTitle()!!.lowercase(Locale.getDefault()).contains(searchString!!.lowercase(Locale.getDefault()))) fSearch = true
        } else fSearch = true
        return fFilter && fFavorite && fSearch
    }

    /**
     * Метод проверяющий текущие настройки филььтра, что то фильтруют?
     * @return да/нет
     */
    val isFilterActive: Boolean
        get() {
            var fSearchString = false
            if (searchString != null) if (searchString != "") fSearchString = true
            return (currentFilterCategory != defaultFilterCategory || isFavoriteShow
                    || fSearchString)
        }

    fun setSearchString(searchString: String?) {
        this.searchString = searchString
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeInt(currentFilterCategory)
        parcel.writeByte((if (isFavoriteShow) 1 else 0).toByte())
        parcel.writeString(searchString)
        parcel.writeInt(defaultFilterCategory)
    }

    companion object {
        @JvmField
        val CREATOR: Creator<Filter?> = object : Creator<Filter?> {
            override fun createFromParcel(`in`: Parcel): Filter? {
                return Filter(`in`)
            }

            override fun newArray(size: Int): Array<Filter?> {
                return arrayOfNulls(size)
            }
        }
    }
}