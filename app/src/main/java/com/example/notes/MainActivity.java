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
  //  NoteListFragment noteListFragment;

    private static final String FRAGMENT_TAG = "NoteListFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // скрываем  actionBar на ландшафтной ориентации
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            Objects.requireNonNull(getSupportActionBar()).hide();
        else
            Objects.requireNonNull(getSupportActionBar()).show();


        if (savedInstanceState == null) getSupportFragmentManager()// первый раз делаем новый фрагмент
                // со списком и добавляем
                .beginTransaction()
                .add(R.id.fragmentContainer, new NoteListFragment(), FRAGMENT_TAG).commit();
        else{// пытаемся восстановить по тэгу FRAGMENT_TAG, при пересоздании активити
            NoteListFragment noteListFragment = (NoteListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);

            if (noteListFragment == null) // на всякий случай
                noteListFragment = new NoteListFragment();

            getSupportFragmentManager()//показываем восстановленный
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, noteListFragment, FRAGMENT_TAG).commit();

        }

    }
}