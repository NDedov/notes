package com.example.notes;

public interface IWorkSharedPreferences {
    void saveNotes(Notes notes);
    Notes restoreNotes();
    void saveSettings(Settings settings);
    Settings restoreSettings();

}
