package com.example.notes

interface IWorkSharedPreferences {
    fun saveNotes(notes: Notes?)
    fun restoreNotes(): Notes?
    fun saveSettings(settings: Settings?)
    fun restoreSettings(): Settings?
}