package com.example.notes;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;


public class FilterFragment  extends Fragment implements Constants{

    int currentFilterPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            requireActivity().getSupportFragmentManager().popBackStack();//удаляем лишние
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null){
            currentFilterPosition = arguments.getInt(FILTER_INDEX);
            initViews(view);
        }
    }

    private void initViews(View view) {
        LinearLayout layout = view.findViewById(R.id.categoryLayout);
        //layout.removeAllViews();

        for (int i = 0; i < Note.categories.length + 1; i++) {//заполняем радиобаттонами категорий
            RadioButton rb = new RadioButton(getContext());

            if (i == Note.categories.length)
                rb.setText("Показать все");//для последнего радиобаттона
            else
                rb.setText(Note.categories[i]);

            if (i == currentFilterPosition)
                rb.setChecked(true);

            layout.addView(rb);

            //прописываем Листенеры для вью
            final int positionCategory = i;
            rb.setOnClickListener(v -> {
                Bundle result = new Bundle();
                result.putInt(FILTER_INDEX, positionCategory);
                getParentFragmentManager().setFragmentResult(RESULT_OK_FILTER_EXIT_INDEX, result);
                getParentFragmentManager().popBackStack();
            });
        }
    }

    public static FilterFragment newInstance(int currentFilterPosition){
        FilterFragment filterFragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putInt(FILTER_INDEX, currentFilterPosition);
        filterFragment.setArguments(args);
        return filterFragment;
    }
}