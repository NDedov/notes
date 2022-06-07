package com.example.notes;

import android.view.View;

public interface NotesListClickListener {
    void onClick(Note note);
    void onFavoriteClick(Note note, View item);
}
