package com.example.notes;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity implements Constants {

    long backPressedTime;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
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
            NoteListFragment noteListFragment = (NoteListFragment) getSupportFragmentManager()
                    .findFragmentByTag(FRAGMENT_TAG);

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
        if (isLandscape()){//скрываем тулбар на списке заявок для ланндшафтной
            toolbar.setVisibility(View.GONE);
        }
        else {//показываем для портретной
            setSupportActionBar(toolbar);
            initDrawer(toolbar);


        }
    }

   @SuppressLint("NonConstantResourceId")
    private void initDrawer(Toolbar toolbar) {//инициализация навигационного меню
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
                case R.id.action_drawer_settings:
                    // todo фрагмент настроек
                    return true;
            }
            return false;
        });
    }

    private void openAboutFragment() {//Вывод фрагмента О программе
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("")
                .replace(R.id.fragmentContainer, new AboutFragment()).commit();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void displayToast(String text) {//кастомизированный тоаст
        Toast toast = Toast.makeText(getBaseContext(),
                text,
                Toast.LENGTH_SHORT);

        View toastView = toast.getView();
        toastView.setBackground(ResourcesCompat.getDrawable(getResources(),
                R.drawable.rounded_corner_toast, null));
        toast.show();
    }

    @Override
    public void onBackPressed() {//обработчик нажатия на назад
         if (isLandscape()) {
             if (backPressedTime + 2000 > System.currentTimeMillis()) {
                 super.onBackPressed();
             } else {
                 displayToast(getString(R.string.press_again_to_exit));
                 Toast.makeText(getBaseContext(), R.string.press_again_to_exit, Toast.LENGTH_SHORT).show();
             }
             backPressedTime = System.currentTimeMillis();
         }

         else {
             FragmentManager fm = getSupportFragmentManager();
             OnBackPressedListener backPressedListener = null;
             for (Fragment fragment: fm.getFragments()) {
                 if (fragment instanceof  OnBackPressedListener) {
                     backPressedListener = (OnBackPressedListener) fragment;
                     break;
                 }
             }

             if (backPressedListener != null) {
                 backPressedListener.onBackPressed();
             } else {
                 if (backPressedTime + 2000 > System.currentTimeMillis()) {
                     super.onBackPressed();
                 } else {
                     displayToast(getString(R.string.press_again_to_exit));
                 }
                 backPressedTime = System.currentTimeMillis();
             }
         }
    }
}

