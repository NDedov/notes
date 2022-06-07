package com.example.notes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment implements OnBackPressedListener,Constants {

    Settings settings;

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


        Button buttonSettingsSave = view.findViewById(R.id.buttonSettingsSave);
        SwitchMaterial switchMaterial = view.findViewById(R.id.switchTheme);
        RadioButton radioButtonRus = view.findViewById(R.id.radioButtonRus);
        RadioButton radioButtonEng = view.findViewById(R.id.radioButtonEng);

        switchMaterial.setChecked(settings.getNightMode().equals(Settings.NIGHT_MODE_YES));
        if (settings.getLanguage().equals(Settings.RUSSIAN)) {
            radioButtonRus.setChecked(true);
            radioButtonEng.setChecked(false);
        }
        if (settings.getLanguage().equals(Settings.ENGLISH) ){
            radioButtonRus.setChecked(false);
            radioButtonEng.setChecked(true);

        }

        buttonSettingsSave.setOnClickListener(view1 -> {
            Bundle result = new Bundle();
            if (switchMaterial.isChecked())
                settings.setNightMode(Settings.NIGHT_MODE_YES);
            else
                settings.setNightMode(Settings.NIGHT_MODE_NO);

            if (radioButtonRus.isChecked())
                settings.setLanguage(Settings.RUSSIAN);
            if (radioButtonEng.isChecked())
                settings.setLanguage(Settings.ENGLISH);

            result.putParcelable(SETTINGS_TAG, settings);
            getParentFragmentManager().setFragmentResult(SETTINGS_CHANGED_TAG, result);
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        }
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
}
