package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements Constants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // скрываем  actionBar на ландшафтной ориентации
//        if (isLandscape())
//            Objects.requireNonNull(getSupportActionBar()).hide();
//        else
//            Objects.requireNonNull(getSupportActionBar()).show();



        if (savedInstanceState == null) {
//            Objects.requireNonNull(getSupportActionBar()).hide();
            getSupportFragmentManager()// первый раз делаем новый фрагмент
                    // со списком и добавляем
                    .beginTransaction()
                    .add(R.id.fragmentContainer, new NoteListFragment(), FRAGMENT_TAG).commit();

            if (isLandscape()) {//скрываем контейнер с заметкой для первого запуска
                FrameLayout fl = findViewById(R.id.fragmentNoteContainer);
                fl.setVisibility(View.GONE);
            }

            //показываем первый раз экран приветствия
            getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .add(R.id.fragmentContainer, new StartScreenFragment(),FRAGMENT_TAG)
                    .addToBackStack("").commit();
        }

        else{// пытаемся восстановить по тэгу FRAGMENT_TAG, при пересоздании активити
            NoteListFragment noteListFragment = (NoteListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);

            if (noteListFragment == null) // на всякий случай
                noteListFragment = new NoteListFragment();

            getSupportFragmentManager()//показываем восстановленный
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, noteListFragment, FRAGMENT_TAG).commit();
        }
    }
    private boolean isLandscape() {
        return getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }

    protected void initToolbarAndDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbarNoteList);
        setSupportActionBar(toolbar);
        initDrawer(toolbar);
    }

    @SuppressLint("NonConstantResourceId")
    private void initDrawer(Toolbar toolbar) {
// Находим DrawerLayout
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
// Создаем ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

// Обработка навигационного меню
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.action_drawer_about:
                    if (isLandscape()) {//скрываем контейнер с заметкой фрагмента about
                        FrameLayout fl = findViewById(R.id.fragmentNoteContainer);
                        fl.setVisibility(View.GONE);
                    }
                    openAboutFragment();
                    drawer.close();
                    return true;
                case R.id.action_drawer_exit:
                    finish();
                    return true;
            }
            return false;
        });
    }

    private void openAboutFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("")
                .replace(R.id.fragmentContainer, new AboutFragment()).commit();
    }



}

