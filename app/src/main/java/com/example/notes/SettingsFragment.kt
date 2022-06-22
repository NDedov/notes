package com.example.notes

import android.content.Context
import android.content.res.Configuration

import com.google.android.material.switchmaterial.SwitchMaterial
import android.widget.RadioButton
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.fragment.app.Fragment
import java.util.*

class SettingsFragment : Fragment(), OnBackPressedListener, Constants {
    var settings: Settings? = null
    private lateinit var buttonSettingsSave: Button
    private lateinit var switchMaterial: SwitchMaterial
    private lateinit var radioButtonRus: RadioButton
    private lateinit  var radioButtonEng: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arguments = arguments
        if (arguments != null) {
            settings = arguments.getParcelable(Constants.SETTINGS_TAG)
            if (settings != null) {
                initViews(view)
                initSwitch()
                initButton()
                hideKeyBoard()
            }
        }
    }

    private fun initViews(view: View) {
        buttonSettingsSave = view.findViewById(R.id.buttonSettingsSave)
        switchMaterial = view.findViewById(R.id.switchTheme)
        radioButtonRus = view.findViewById(R.id.radioButtonRus)
        radioButtonEng = view.findViewById(R.id.radioButtonEng)
    }

    private fun initButton() {
        buttonSettingsSave.setOnClickListener {
            val result = Bundle()
            if (radioButtonRus.isChecked) settings!!.language = Settings.RUSSIAN
            if (radioButtonEng.isChecked) settings!!.language = Settings.ENGLISH
            applyLanguage()
            result.putParcelable(Constants.SETTINGS_TAG, settings)
            parentFragmentManager.setFragmentResult(Constants.SETTINGS_CHANGED_TAG, result)
            recoverContainer()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initSwitch() {
        switchMaterial.isChecked = settings!!.nightMode == Settings.NIGHT_MODE_YES
        if (settings!!.language == Settings.RUSSIAN) {
            radioButtonRus.isChecked = true
            radioButtonEng.isChecked = false
        }
        if (settings!!.language == Settings.ENGLISH) {
            radioButtonRus.isChecked = false
            radioButtonEng.isChecked = true
        }
        initSwitchListener()
    }

    private fun initSwitchListener() {
        switchMaterial.setOnClickListener {
            if (switchMaterial.isChecked) settings!!.nightMode = Settings.NIGHT_MODE_YES else settings!!.nightMode = Settings.NIGHT_MODE_NO
            val result = Bundle()
            result.putParcelable(Constants.SETTINGS_TAG, settings)
            parentFragmentManager.setFragmentResult(Constants.SETTINGS_CHANGED_TAG, result)
            recoverContainer()
            requireActivity().supportFragmentManager.popBackStack()
            if (settings!!.nightMode == Settings.NIGHT_MODE_YES) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun applyLanguage() {
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        if (settings!!.language == Settings.ENGLISH) conf.setLocale(Locale("en")) // API 17+ only.
        if (settings!!.language == Settings.RUSSIAN) conf.setLocale(Locale("ru")) // API 17+ only.
        res.updateConfiguration(conf, dm)
        (requireActivity() as IDrawerFromFragment).updateDrawer()
    }

    override fun onBackPressed() {
        recoverContainer()
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun hideKeyBoard() {
        val view1 = requireActivity().currentFocus
        if (view1 != null) { //скрытие клавиатуры при выходе
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view1.windowToken, 0)
        }
    }

    private fun recoverContainer() {
        if (resources.configuration.orientation
                == Configuration.ORIENTATION_LANDSCAPE) //восстанавливаем фрагмент для заметок
        // для ландшафтной ориентации
            requireActivity().findViewById<View>(R.id.fragmentNoteContainer).visibility = View.VISIBLE
        requireActivity().supportFragmentManager.popBackStack()
    }

    companion object {
        @JvmStatic
        fun newInstance(settings: Settings?): SettingsFragment {
            val settingsFragment = SettingsFragment()
            val args = Bundle()
            args.putParcelable(Constants.SETTINGS_TAG, settings)
            settingsFragment.arguments = args
            return settingsFragment
        }
    }
}