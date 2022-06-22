package com.example.notes;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

public class StartScreenFragment extends Fragment implements OnBackPressedListener, Constants {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start_screen, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConstraintLayout layout = view.findViewById(R.id.startScreenLayout);
        layout.setOnClickListener(view1 -> {//по клику закрываем текущий фрагмент
            if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE)//восстанавливаем фрагмент для заметок
                // для ландшафтной ориентации
                requireActivity().findViewById(R.id.fragmentNoteContainer).setVisibility(View.VISIBLE);

            requireActivity().getSupportFragmentManager().popBackStack();//закрываем
        });
        showAnimatedText(view);
    }

    private void showAnimatedText(View view) {
        TextView textView = view.findViewById(R.id.textContinueScreenView);
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(700); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        textView.startAnimation(anim);
    }

    @Override
    public void onBackPressed() {
        requireActivity().getSupportFragmentManager().popBackStack();//закрываем
    }
}