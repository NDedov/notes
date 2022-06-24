package com.example.notes.activity

import androidx.appcompat.app.AppCompatActivity
import android.content.res.Configuration
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDelegate
import android.view.MenuItem
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentTransaction
import com.example.notes.*
import com.example.notes.add.Constants
import com.example.notes.fragments.AboutFragment
import com.example.notes.fragments.StartScreenFragment
import com.example.notes.fragments.notes.NoteListFragment
import com.example.notes.fragments.notes.Notes
import com.example.notes.fragments.settings.Settings
import com.example.notes.fragments.settings.SettingsFragment
import com.google.gson.GsonBuilder
import java.util.*

open class MainActivity : AppCompatActivity(), Constants, IDrawerFromFragment,
    IWorkSharedPreferences {
    private var backPressedTime // счетчик времени для выхода из активити
            : Long = 0
    private var settings //настройки
            : Settings? = null
  /*  private val sharedPreferences // для сохранения настроек
            : SharedPreferences? = null*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            settings = restoreSettings()
            if (settings == null) settings = Settings(Settings.RUSSIAN, Settings.NIGHT_MODE_NO) //инициализируем настройки
        } else settings = savedInstanceState.getParcelable(Constants.SETTINGS_TAG)
        applySettings()
        initFragments(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(Constants.SETTINGS_TAG, settings)
        super.onSaveInstanceState(outState)
    }

    /**
     * Показываем фрагменты при старте Активити
     * @param savedInstanceState сохраненные параметры
     */
    private fun initFragments(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            openNoteListFragment()
            if (isLandscape) { //скрываем контейнер с заметкой для первого запуска
                val fl = findViewById<FrameLayout>(R.id.fragmentNoteContainer)
                fl.visibility = View.GONE
            }
            //   openStartScreenFragment(); //показываем первый раз экран приветствия
        } else { // пытаемся восстановить по тэгу FRAGMENT_TAG, при пересоздании активити
            var noteListFragment = supportFragmentManager
                    .findFragmentByTag(Constants.FRAGMENT_TAG) as NoteListFragment?
            if (noteListFragment == null) // на всякий случай
                noteListFragment = NoteListFragment()
            supportFragmentManager //показываем восстановленный
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, noteListFragment, Constants.FRAGMENT_TAG).commit()
        }
    }

    private fun openNoteListFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    NoteListFragment.newInstance(settings),
                    Constants.FRAGMENT_TAG
                )
                .commit()
    }

    private fun openStartScreenFragment() {
        supportFragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .replace(R.id.fragmentContainer, StartScreenFragment())
                .addToBackStack("").commit()
    }

    /** Метод меняющий настройки активити
     * язык и Ночной режим
     */
    private fun applySettings() {
        if (settings!!.nightMode == Settings.NIGHT_MODE_YES) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        applyLanguage()
        updateDrawer()
    }

    /**
     * метод замены языка в зависитмости от текущих настроек
     */
    private fun applyLanguage() {
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        if (settings!!.language == Settings.ENGLISH) conf.setLocale(Locale("en")) // API 17+ only.
        if (settings!!.language == Settings.RUSSIAN) conf.setLocale(Locale("ru")) // API 17+ only.
        res.updateConfiguration(conf, dm)
    }

    /**
     * метод проверки ориентации экрана
     * @return истина если ландшафтный
     */
    private val isLandscape: Boolean
        get() = (resources.configuration.orientation
                == Configuration.ORIENTATION_LANDSCAPE)

    /**
     * Инициализация тулбара и меню DrawerNavigation
     */
    private fun initToolbarAndDrawer() {
        val toolbar = findViewById<Toolbar>(R.id.toolbarNoteList)
        if (isLandscape) //скрываем тулбар на списке заявок для ландшафтной
            toolbar.visibility = View.GONE
        setSupportActionBar(toolbar)
        initDrawer(toolbar)
    }

    /**
     * Инициализация Drawer на тулбаре
     * @param toolbar тулбар
     */
    private fun initDrawer(toolbar: Toolbar) { //инициализация навигационного меню
        // Находим DrawerLayout
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        // Создаем ActionBarDrawerToggle
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        initNavigationListener(drawer)
    }

    /**
     * Настройка Listeners для навигационного меню
     * @param drawer drawerLayout
     */
    private fun initNavigationListener(drawer: DrawerLayout) {
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_drawer_about -> {
                    if (isLandscape) { //скрываем контейнер с заметкой фрагмента about
                        val fl = findViewById<FrameLayout>(R.id.fragmentNoteContainer)
                        fl.visibility = View.GONE
                    }
                    openAboutFragment()
                    drawer.close()
                    return@setNavigationItemSelectedListener true
                }
                R.id.action_drawer_exit -> {
                    finish()
                    return@setNavigationItemSelectedListener true
                }
                R.id.action_drawer_settings -> {
                    if (isLandscape) { //скрываем контейнер с заметкой фрагмента about
                        val fl = findViewById<FrameLayout>(R.id.fragmentNoteContainer)
                        fl.visibility = View.GONE
                    }
                    openSettingsFragment()
                    drawer.close()
                    return@setNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    private fun openSettingsFragment() { // Открытие фрагмента настроек
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, SettingsFragment.newInstance(settings))
                .addToBackStack("")
                .commit()
    }

    private fun openAboutFragment() { //Вывод фрагмента О программе
        supportFragmentManager
                .beginTransaction()
                .addToBackStack("")
                .replace(R.id.fragmentContainer, AboutFragment()).commit()
    }

    /**
     * Кастомизированный тоаст
     * @param text текст
     */
    private fun displayToast(text: String?) { //кастомизированный тоаст
        val toast = Toast.makeText(baseContext,
                text,
                Toast.LENGTH_SHORT)
        val toastView = toast.view
        toastView!!.background = ResourcesCompat.getDrawable(resources,
            R.drawable.rounded_corner_toast, null)
        toast.show()
    }

    override fun onBackPressed() { //обработчик нажатия на назад
        if (isLandscape) {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                finish()
            } else {
                displayToast(getString(R.string.press_again_to_exit))
            }
            backPressedTime = System.currentTimeMillis()
        } else {
            val fm = supportFragmentManager
            var backPressedListener: OnBackPressedListener? = null
            for (fragment in fm.fragments) {
                if (fragment is OnBackPressedListener) {
                    backPressedListener = fragment
                    break
                }
            }
            if (backPressedListener != null) {
                backPressedListener.onBackPressed()
            } else {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    super.onBackPressed()
                } else {
                    displayToast(getString(R.string.press_again_to_exit))
                }
                backPressedTime = System.currentTimeMillis()
            }
        }
    }

    override fun onStop() {
        saveSettings(settings)
        super.onStop()
    }

    override fun initDrawer() {
        initToolbarAndDrawer()
    }

    override fun updateDrawer() { //используется при замене языка
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.menu.clear()
        navigationView.inflateMenu(R.menu.drawer_menu)
    }

    override fun saveNotes(notes: Notes?) {}
    override fun restoreNotes(): Notes? {
        return null
    }

    override fun saveSettings(settings: Settings?) {
        val sharedPreferences = getSharedPreferences(
            Constants.NOTES_SHARED_P,
                MODE_PRIVATE)
        val jsonSettings = GsonBuilder().create().toJson(settings)
        sharedPreferences.edit().putString(Constants.NOTES_SHARED_P_KEY_SETTINGS, jsonSettings).apply()
    }

    override fun restoreSettings(): Settings? {
        val sharedPreferences = getSharedPreferences(
            Constants.NOTES_SHARED_P,
                MODE_PRIVATE)
        val savedSettings = sharedPreferences.getString(Constants.NOTES_SHARED_P_KEY_SETTINGS, null)
        return if (savedSettings != null) GsonBuilder().create().fromJson(savedSettings, Settings::class.java) else null
    }
}