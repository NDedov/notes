package com.example.notes;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;

public class Notes implements Parcelable {
    private final ArrayList<Note> notes;
    private Note currentNote;

    public Notes() {
        notes = new ArrayList<>();
        currentNote = null;

    }

    protected Notes(Parcel in) {
        notes = in.createTypedArrayList(Note.CREATOR);
        currentNote = in.readParcelable(Note.class.getClassLoader());
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

    public Note getCurrentNote() {
        return currentNote;
    }

    public int getSize(){
        return notes.size();
    }

    public void setCurrentNote(Note currentNote) {

        this.currentNote = currentNote;
        Collections.sort(notes, Note::compareTo);
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public Note get(int position){
        return notes.get(position);
    }

    public void delete(Note note){
        notes.remove(note);
        if (currentNote != null && notes.size() > 0)
            if (currentNote.equals(note))
                    this.currentNote = notes.get(0);
    }

    public void add(Note note){
        notes.add(0, note);
        currentNote = note;
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
        parcel.writeParcelable(currentNote, i);
    }
}
