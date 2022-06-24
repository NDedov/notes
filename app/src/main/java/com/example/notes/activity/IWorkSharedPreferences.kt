package com.example.notes.activity

import com.example.notes.fragments.notes.Notes
import com.example.notes.fragments.settings.Settings

interface IWorkSharedPreferences {
    fun saveNotes(notes: Notes?)
    fun restoreNotes(): Notes?
    fun saveSettings(settings: Settings?)
    fun restoreSettings(): Settings?
}