package com.example.notes;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AboutFragment extends Fragment implements OnBackPressedListener{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout layout = view.findViewById(R.id.aboutLayout);
        layout.setOnClickListener(view1 -> { //закрываем на нажатие
            if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE)//восстанавливаем фрагмент для заметок
                // для ландшафтной ориентации
                requireActivity().findViewById(R.id.fragmentNoteContainer).setVisibility(View.VISIBLE);
            requireActivity().getSupportFragmentManager().popBackStack();//закрываем
        });
        hideKeyBoard();
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
