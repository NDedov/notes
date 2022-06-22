package com.example.notes

import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator
import java.util.*

class Notes :Parcelable{
    var notes: ArrayList<Note> = ArrayList()
    private var currentNote: Note? = null

    constructor(){
        currentNote = null
    }

    fun getCurrentNote(): Note? {
        return currentNote
    }

    val size: Int
        get() = notes.size

    constructor(parcel: Parcel) : this() {
        currentNote = parcel.readParcelable(Note::class.java.classLoader)
    }

    fun setCurrentNote(currentNote: Note?) {
        this.currentNote = currentNote
        notes.sortWith { obj: Note, o: Note? -> obj.compareTo(o) }
    }

    operator fun get(position: Int): Note {
        return notes[position]
    }

    fun delete(note: Note) {
        notes.remove(note)
        if (currentNote != null && notes.size > 0) if (currentNote == note) currentNote = notes[0]
    }

    fun add(note: Note) {
        notes.add(0, note)
        currentNote = note
    }

    fun testFillNotes() {
        notes.add(Note("Первая заметка", "Добрый день, \tкак дела?\nПривет",
                GregorianCalendar(), 0, false))
        notes.add(Note("Покупки", """
     Молоко, хлеб
     Масло
     Молоко
     Масло
     Молоко"Молоко, хлеб
     Масло
     Молоко
     Масло
     Молоко"Молоко, хлеб
     Масло
     Молоко
     Масло
     Молоко"Молоко, хлеб
     Масло
     Молоко
     Масло
     Молоко"Молоко, хлеб
     Масло
     Молоко
     Масло
     МолокоМолоко, хлеб
     Масло
     Молоко
     Масло
     Молоко"Молоко, хлеб
     Масло
     Молоко
     Масло
     Молоко"Молоко, хлеб
     Масло
     Молоко
     Масло
     Молоко"Молоко, хлеб
     Масло
     Молоко
     Масло
     Молоко"Молоко, хлеб
     Масло
     Молоко
     Масло
     Молоко
     """.trimIndent(),
                GregorianCalendar(), 2, true))
        notes.add(Note("Третья заметка", "Добрый день опять, как дела?\nПривет",
                GregorianCalendar(), 1, false))
        notes.add(Note("Новая заметка", "Добрый день, как дела?\n Привет",
                GregorianCalendar(), 0, false))
        notes.add(Note("Что надо сделать срочно", "Молоко, хлеб\n Масло",
                GregorianCalendar(), 2, true))
        notes.add(Note("Пароли", "Добрый день опять, как дела?\n Привет",
                GregorianCalendar(), 4, false))
        notes.add(Note("Прочее", "Добрый день, как дела?\n Привет",
                GregorianCalendar(), 0, false))
        notes.add(Note("Покупки", "Молоко, хлеб\n Масло",
                GregorianCalendar(), 2, true))
        notes.add(Note("Третья заметка", "Добрый день опять, как дела?\n Привет",
                GregorianCalendar(), 0, false))
        notes.add(Note("Первая заметка", "Добрый день, как дела?\n Привет",
                GregorianCalendar(), 0, false))
        notes.add(Note("Покупки", "Молоко, хлеб\n Масло",
                GregorianCalendar(), 2, true))
        notes.add(Note("Третья заметка", "Добрый день опять, как дела?\n Привет",
                GregorianCalendar(), 0, false))
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(currentNote, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<Notes> {
        override fun createFromParcel(parcel: Parcel): Notes {
            return Notes(parcel)
        }

        override fun newArray(size: Int): Array<Notes?> {
            return arrayOfNulls(size)
        }
    }


}