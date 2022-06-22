package com.example.notes

import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator
import java.util.*

class Note : Parcelable, Comparable<Any?> {
    private var title: String?
    private var text: String?
    var dateTimeModify: Calendar? = null
        private set
    private var categoryID: Int
    private var isFavourite: Boolean

    constructor(title: String?, text: String?, dateTimeModify: Calendar?, categoryID: Int, isFavourite: Boolean) {
        this.title = title
        this.text = text
        this.categoryID = categoryID
        this.isFavourite = isFavourite
        this.dateTimeModify = dateTimeModify
    }

    private constructor(`in`: Parcel) {
        title = `in`.readString()
        text = `in`.readString()
        categoryID = `in`.readInt()
        isFavourite = `in`.readByte().toInt() != 0
    }

    private fun updateTime() { //обновление даты последней модификации
        dateTimeModify = GregorianCalendar()
    }

    fun getTitle(): String? {
        return title
    }

    fun getText(): String? {
        return text
    }

    fun setTitle(title: String?) {
        this.title = title
        updateTime()
    }

    fun setText(text: String?) {
        this.text = text
        updateTime()
    }

    fun setCategoryID(categoryID: Int) {
        this.categoryID = categoryID
        updateTime()
    }

    fun setFavourite(favourite: Boolean) {
        isFavourite = favourite
        updateTime()
    }

    fun getCategoryID(): Int {
        return categoryID
    }

    fun isFavourite(): Boolean {
        return isFavourite
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(title)
        parcel.writeString(text)
        parcel.writeInt(categoryID)
        parcel.writeByte((if (isFavourite) 1 else 0).toByte())
    }

    override fun compareTo(other: Any?): Int {
        val note = other as Note?
        return note!!.dateTimeModify!!.time.time.compareTo(dateTimeModify!!.time.time)
    }

    companion object {
        //класс для работы с заметкой
        @JvmField
        var categories = arrayOf("Общее",
                "Работа",
                "Семья",
                "Личное",
                "Путешествия",
                "Развлечения",
                "Отдых",
                "Мероприятия",
                "Спорт")
        @JvmField
        val CREATOR: Creator<Note?> = object : Creator<Note?> {
            override fun createFromParcel(`in`: Parcel): Note? {
                return Note(`in`)
            }

            override fun newArray(size: Int): Array<Note?> {
                return arrayOfNulls(size)
            }
        }
    }
}