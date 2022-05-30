package com.example.notes;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class AboutFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);

    }
    @SuppressLint("ResourceType")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout layout = view.findViewById(R.id.aboutLayout);
        layout.setOnClickListener(view1 -> {

            MainActivity ma = (MainActivity) getActivity();//восстанавливаем ActionBar
            assert ma != null;

            if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE)//восстанавливаем фрагмент для заметок
                // для ландшафтной ориентации
                ma.findViewById(R.id.fragmentNoteContainer).setVisibility(View.VISIBLE);
     //       else
//                Objects.requireNonNull(ma.getSupportActionBar()).show();//восстанавливаем actionBar
                // для портретной ориентации

                requireActivity().getSupportFragmentManager().popBackStack();//закрываем
        });

    }
}
