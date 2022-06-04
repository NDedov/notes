package com.example.notes;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Calendar;
import java.util.GregorianCalendar;



public class Note implements Parcelable, Comparable{//класс для работы с заметкой

    public static String[] categories = {"Общее",
            "Работа",
            "Семья",
            "Личное",
            "Путешествия",
            "Развлечения",
            "Отдых",
            "Мероприятия",
            "Спорт"};
    private String title;
    private String text;
    private Calendar dateTimeCreation;
    private Calendar dateTimeModify;
    private int categoryID;
    private boolean isFavourite;

    public Note(String title, String text, Calendar dateTimeCreation, int categoryID, boolean isFavourite) {
        this.title = title;
        this.text = text;
        this.dateTimeCreation = dateTimeCreation;
        this.categoryID = categoryID;
        this.isFavourite = isFavourite;
        this.dateTimeModify = dateTimeCreation;

    }

    protected Note(Parcel in) {
        title = in.readString();
        text = in.readString();
        categoryID = in.readInt();
        isFavourite = in.readByte() != 0;
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    private void updateTime(){//обновление даты последней модификации
        dateTimeModify = new GregorianCalendar();
    }

    public static String[] getCategories() {
        return categories;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public Calendar getDateTimeModify() {
        return dateTimeModify;
    }

    public void setTitle(String title) {
        this.title = title;
        updateTime();
    }

    public void setText(String text) {
        this.text = text;
        updateTime();
    }

    public void setDateTimeModify(Calendar dateTimeModify) {
        this.dateTimeModify = dateTimeModify;
        updateTime();
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
        updateTime();
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
        updateTime();
    }

    public int getCategoryID() {
        return categoryID;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(text);
        parcel.writeInt(categoryID);
        parcel.writeByte((byte) (isFavourite ? 1 : 0));
    }

    @Override
    public int compareTo(Object o) {
        Note note = (Note)o;
        if (dateTimeModify.getTime().getTime() > note.dateTimeModify.getTime().getTime())
            return -1;
        if (dateTimeModify.getTime().getTime() == note.dateTimeModify.getTime().getTime())
            return 0;
        return 1;

    }
}
