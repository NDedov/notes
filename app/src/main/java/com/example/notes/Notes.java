package com.example.notes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class Notes implements Parcelable {
    private ArrayList<Note> notes;
    private int currentPosition;

    public Notes() {
        notes = new ArrayList<>();
        currentPosition = 0;
    }

    protected Notes(Parcel in) {
        notes = in.createTypedArrayList(Note.CREATOR);
        currentPosition = in.readInt();
    }

    public static final Creator<Notes> CREATOR = new Creator<Notes>() {
        @Override
        public Notes createFromParcel(Parcel in) {
            return new Notes(in);
        }

        @Override
        public Notes[] newArray(int size) {
            return new Notes[size];
        }
    };

    public Note getCurrent(){
        return notes.get(currentPosition);
    }

    public void replaceCurrent(Note note){
        notes.set(currentPosition, note);
    }

    public Note get(int position){
        return notes.get(position);
    }

    public void delete(int position){
        notes.remove(position);
    }

    public void delete(Note note){
        notes.remove(note);
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
    public int size(){
        return notes.size();
    }
    public int getCurrentPosition() {
        return currentPosition;
    }

    public void testFillNotes() {
        notes.add(new Note("Первая заметка", "Добрый день, \tкак дела?\nПривет",
                new GregorianCalendar(), 0, false));
        notes.add(new Note("Покупки", "Молоко, хлеб\nМасло\nМолоко\nМасло\nМолоко\"Молоко," +
                " хлеб\nМасло\nМолоко\nМасло\nМолоко\"" +
                "Молоко, хлеб\nМасло\nМолоко\nМасло\nМолоко\"Молоко, хлеб\nМасло\nМолоко\nМасло\n" +
                "Молоко\"Молоко, хлеб\nМасло\nМолоко\nМасло\nМолокоМолоко, хлеб\nМасло\nМолоко\nМасло" +
                "\nМолоко\"Молоко, хлеб\nМасло\nМолоко\nМасло\nМолоко\"Молоко, хлеб\nМасло\nМолоко\nМасло" +
                "\nМолоко\"Молоко, хлеб\nМасло\nМолоко\nМасло\nМолоко\"Молоко, хлеб\nМасло\nМолоко\n" +
                "Масло\nМолоко",
                new GregorianCalendar(), 2, true));
        notes.add(new Note("Третья заметка", "Добрый день опять, как дела?\nПривет",
                new GregorianCalendar(), 1, false));
        notes.add(new Note("Новая заметка", "Добрый день, как дела?\n Привет",
                new GregorianCalendar(), 0, false));
        notes.add(new Note("Что надо сделать срочно", "Молоко, хлеб\n Масло",
                new GregorianCalendar(), 2, true));
        notes.add(new Note("Пароли", "Добрый день опять, как дела?\n Привет",
                new GregorianCalendar(), 4, false));
        notes.add(new Note("Прочее", "Добрый день, как дела?\n Привет",
                new GregorianCalendar(), 0, false));
        notes.add(new Note("Покупки", "Молоко, хлеб\n Масло",
                new GregorianCalendar(), 2, true));
        notes.add(new Note("Третья заметка", "Добрый день опять, как дела?\n Привет",
                new GregorianCalendar(), 0, false));
        notes.add(new Note("Первая заметка", "Добрый день, как дела?\n Привет",
                new GregorianCalendar(), 0, false));
        notes.add(new Note("Покупки", "Молоко, хлеб\n Масло",
                new GregorianCalendar(), 2, true));
        notes.add(new Note("Третья заметка", "Добрый день опять, как дела?\n Привет",
                new GregorianCalendar(), 0, false));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(notes);
        parcel.writeInt(currentPosition);
    }
}
