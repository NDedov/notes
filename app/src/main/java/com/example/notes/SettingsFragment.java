package com.example.notes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Locale;

public class SettingsFragment extends Fragment implements OnBackPressedListener,Constants {

    Settings settings;

    Button buttonSettingsSave;
    SwitchMaterial switchMaterial;
    RadioButton radioButtonRus;
    RadioButton radioButtonEng;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);

    }
    @SuppressLint("ResourceType")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null){
            settings = arguments.getParcelable(SETTINGS_TAG);

        buttonSettingsSave = view.findViewById(R.id.buttonSettingsSave);
        switchMaterial = view.findViewById(R.id.switchTheme);
        radioButtonRus = view.findViewById(R.id.radioButtonRus);
        radioButtonEng = view.findViewById(R.id.radioButtonEng);

        initSwitch();
        initButton();
        hideKeyBoard();
        }
    }

    private void initButton() {
        buttonSettingsSave.setOnClickListener(view1 -> {
            Bundle result = new Bundle();
            if (radioButtonRus.isChecked())
                settings.setLanguage(Settings.RUSSIAN);
            if (radioButtonEng.isChecked())
                settings.setLanguage(Settings.ENGLISH);
            applyLanguage();

            result.putParcelable(SETTINGS_TAG, settings);
            getParentFragmentManager().setFragmentResult(SETTINGS_CHANGED_TAG, result);
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void initSwitch() {
        switchMaterial.setChecked(settings.getNightMode().equals(Settings.NIGHT_MODE_YES));
        if (settings.getLanguage().equals(Settings.RUSSIAN)) {
            radioButtonRus.setChecked(true);
            radioButtonEng.setChecked(false);
        }
        if (settings.getLanguage().equals(Settings.ENGLISH) ){
            radioButtonRus.setChecked(false);
            radioButtonEng.setChecked(true);
        }

        switchMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchMaterial.isChecked())
                    settings.setNightMode(Settings.NIGHT_MODE_YES);
                else
                    settings.setNightMode(Settings.NIGHT_MODE_NO);
                Bundle result = new Bundle();
                result.putParcelable(SETTINGS_TAG, settings);
                getParentFragmentManager().setFragmentResult(SETTINGS_CHANGED_TAG, result);
                requireActivity().getSupportFragmentManager().popBackStack();

                if (settings.getNightMode().equals(Settings.NIGHT_MODE_YES))
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

    }

    private void applyLanguage() {
        Resources res = getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        if (settings.getLanguage().equals(Settings.ENGLISH))
            conf.setLocale(new Locale("en")); // API 17+ only.
        // Use conf.locale = new Locale(...) if targeting lower versions
        if (settings.getLanguage().equals(Settings.RUSSIAN))
            conf.setLocale(new Locale("ru")); // API 17+ only.
        res.updateConfiguration(conf, dm);

        ((IDrawerFromFragment)requireActivity()).updateDrawer();

    }

    public static SettingsFragment newInstance(Settings settings){
        SettingsFragment settingsFragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putParcelable(SETTINGS_TAG, settings);
        settingsFragment.setArguments(args);
        return settingsFragment;
    }

    @Override
    public void onBackPressed() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    void hideKeyBoard(){
        View view1 = requireActivity().getCurrentFocus();
        if (view1 != null) {//скрытие клавиатуры при выходе
            InputMethodManager imm = (InputMethodManager)requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
    }
}
