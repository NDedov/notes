package com.example.notes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.appcompat.app.AppCompatDelegate;

public class Settings implements Parcelable {
    public static String ENGLISH = "ENGLISH";
    public static String RUSSIAN = "RUSSIAN";
    public static String NIGHT_MODE_YES = "MODE_NIGHT_YES";
    public static String NIGHT_MODE_NO = "MODE_NIGHT_NO";
    private String language;
    private String nightMode;

    protected Settings(Parcel in) {
        language = in.readString();
        nightMode = in.readString();
    }

    public Settings(String language, String nightMode) {
        this.language = language;
        this.nightMode = nightMode;
    }

    public static final Creator<Settings> CREATOR = new Creator<Settings>() {
        @Override
        public Settings createFromParcel(Parcel in) {
            return new Settings(in);
        }

        @Override
        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setNightMode(String nightMode) {
        this.nightMode = nightMode;
    }

    public String getNightMode() {
        return nightMode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(language);
        parcel.writeString(nightMode);
    }
}
