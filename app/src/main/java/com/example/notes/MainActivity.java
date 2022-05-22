package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NoteListFragment noteListFragment = new NoteListFragment();

        // скрываем  actionBar на ландшафтной ориентации
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            Objects.requireNonNull(getSupportActionBar()).hide();
        else
            Objects.requireNonNull(getSupportActionBar()).show();

        //показываем фрагмент с основным списком заметок
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, noteListFragment)
                .commit();
    }
}