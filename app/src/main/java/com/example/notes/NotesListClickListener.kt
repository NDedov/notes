package com.example.notes

import android.view.View

interface NotesListClickListener {
    fun onClick(note: Note?)
    fun onFavoriteClick(note: Note?, item: View?, position: Int)
    fun onLongClick(note: Note?, view: View?, position: Int)
}