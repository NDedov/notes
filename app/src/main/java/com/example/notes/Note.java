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
    private Calendar dateTimeModify;
    private int categoryID;
    private boolean isFavourite;

    public Note(String title, String text, Calendar dateTimeModify, int categoryID, boolean isFavourite) {
        this.title = title;
        this.text = text;
        this.categoryID = categoryID;
        this.isFavourite = isFavourite;
        this.dateTimeModify = dateTimeModify;
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
        return Long.compare(note.dateTimeModify.getTime().getTime(), dateTimeModify.getTime().getTime());
    }
}
