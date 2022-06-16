package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.GsonBuilder;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements Constants,IDrawerFromFragment,IWorkSharedPreferences {

    private long backPressedTime; // счетчик времени для выхода из активити
    private Settings settings;//настройки
    private SharedPreferences sharedPreferences;// для сохранения настроек

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            settings = restoreSettings();
            if (settings == null)
                settings = new Settings(Settings.RUSSIAN, Settings.NIGHT_MODE_NO);//инициализируем настройки
        }
        else
             settings = savedInstanceState.getParcelable(SETTINGS_TAG);
        applySettings();

        initFragments(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
         outState.putParcelable(SETTINGS_TAG, settings);
        super.onSaveInstanceState(outState);
    }

    /**
     * Показываем фрагменты при старте Активити
     * @param savedInstanceState сохраненные параметры
     */
    private void initFragments(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            openNoteListFragment();
            if (isLandscape()) {//скрываем контейнер с заметкой для первого запуска
                FrameLayout fl = findViewById(R.id.fragmentNoteContainer);
                fl.setVisibility(View.GONE);
            }
         //   openStartScreenFragment(); //показываем первый раз экран приветствия
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

    private void openNoteListFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, NoteListFragment.newInstance(settings), FRAGMENT_TAG)
                .commit();
    }

    private void openStartScreenFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .replace(R.id.fragmentContainer, new StartScreenFragment())
                .addToBackStack("").commit();
    }

    /** Метод меняющий настройки активити
     * язык и Ночной режим
     */
    private void applySettings() {
        if (settings.getNightMode().equals(Settings.NIGHT_MODE_YES))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        applyLanguage();
        updateDrawer();
    }

    /**
     * метод замены языка в зависитмости от текущих настроек
     */
    private void applyLanguage() {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        if (settings.getLanguage().equals(Settings.ENGLISH))
            conf.setLocale(new Locale("en")); // API 17+ only.
        if (settings.getLanguage().equals(Settings.RUSSIAN))
            conf.setLocale(new Locale("ru")); // API 17+ only.
        res.updateConfiguration(conf, dm);
    }

    /**
     * метод проверки ориентации экрана
     * @return истина если ландшафтный
     */
    private boolean isLandscape() {
        return getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * Инициализация тулбара и меню DrawerNavigation
     */
    protected void initToolbarAndDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbarNoteList);
        if (isLandscape())//скрываем тулбар на списке заявок для ландшафтной
            toolbar.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        initDrawer(toolbar);
    }


    /**
     * Инициализация Drawer на тулбаре
     * @param toolbar тулбар
     */
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
        initNavigationListener(drawer);
    }

    /**
     * Настройка Listeners для навигационного меню
     * @param drawer drawerLayout
     */
    private void initNavigationListener(DrawerLayout drawer) {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case (R.id.action_drawer_about):
                    if (isLandscape()) {//скрываем контейнер с заметкой фрагмента about
                        FrameLayout fl = findViewById(R.id.fragmentNoteContainer);
                        fl.setVisibility(View.GONE);
                    }
                    openAboutFragment();
                    drawer.close();
                    return true;
                case (R.id.action_drawer_exit):
                    finish();
                    return true;
                case (R.id.action_drawer_settings):
                    if (isLandscape()) {//скрываем контейнер с заметкой фрагмента about
                        FrameLayout fl = findViewById(R.id.fragmentNoteContainer);
                        fl.setVisibility(View.GONE);
                    }
                    openSettingsFragment();
                    drawer.close();
                    return true;
            }
            return false;
        });
    }

    private void openSettingsFragment() {// Открытие фрагмента настроек
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, SettingsFragment.newInstance(settings))
                .addToBackStack("")
                .commit();
    }

    private void openAboutFragment() {//Вывод фрагмента О программе
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack("")
                    .replace(R.id.fragmentContainer, new AboutFragment()).commit();
    }

    /**
     * Кастомизированный тоаст
     * @param text текст
     */
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
                 finish();
             } else {
                 displayToast(getString(R.string.press_again_to_exit));
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

    @Override
    protected void onStop() {
        saveSettings(settings);
        super.onStop();
    }

    @Override
    public void initDrawer() {
        initToolbarAndDrawer();
    }

    @Override
    public void updateDrawer() {//используется при замене языка
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.drawer_menu);
    }

    @Override
    public void saveNotes(Notes notes) {

    }

    @Override
    public Notes restoreNotes() {
        return null;
    }

    @Override
    public void saveSettings(Settings settings) {
        sharedPreferences = getSharedPreferences(NOTES_SHARED_P,
                Context.MODE_PRIVATE);
        String jsonSettings = new GsonBuilder().create().toJson(settings);
        sharedPreferences.edit().putString(NOTES_SHARED_P_KEY_SETTINGS, jsonSettings).apply();
    }

    @Override
    public Settings restoreSettings() {
        sharedPreferences = getSharedPreferences(NOTES_SHARED_P,
                Context.MODE_PRIVATE);
        String savedSettings = sharedPreferences.getString(NOTES_SHARED_P_KEY_SETTINGS, null);
        if (savedSettings != null)
            return new GsonBuilder().create().fromJson(savedSettings, Settings.class);
        return null;
    }
}

