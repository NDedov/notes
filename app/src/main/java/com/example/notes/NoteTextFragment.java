package com.example.notes;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class NoteTextFragment extends Fragment implements Constants {

    Note note;
    TextView dateTimeView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_text, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null){
            note = arguments.getParcelable(LIST_TO_NOTE_INDEX);
            if (note != null)
                initViews(view);
        }

        // настраиваем FragmentResultListener при успешной смене даты в DateTimeFragment, обновляем
        // дату в текущем фрагменте
        getParentFragmentManager().setFragmentResultListener(RESULT_OK_DATE_EXIT_INDEX, this,
                new FragmentResultListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onFragmentResult(@NonNull String key, @NonNull Bundle bundle) {
                note = bundle.getParcelable(DATE_EXIT_INDEX);
                dateTimeView.setText(new SimpleDateFormat("dd MMMM yyyy  HH:mm")
                        .format(note.getDateTimeCreation().getTime()));
            }
        });
    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putParcelable(LIST_TO_NOTE_INDEX, note);
//    }





    @SuppressLint({"SimpleDateFormat", "UseCompatLoadingForDrawables"})
    private void initViews(View view) {
        TextView titleView = view.findViewById(R.id.titleTextView);
        dateTimeView = view.findViewById(R.id.dateTimeView);
        Spinner categorySpinner = view.findViewById(R.id.categorySpinner);
        TextView textView = view.findViewById(R.id.textView);
        AppCompatButton favoriteButton = view.findViewById(R.id.favoriteButton);


        titleView.setText(note.getTitle());
        textView.setText(note.getText());
        dateTimeView.setText(new SimpleDateFormat("dd MMMM yyyy  HH:mm")
                .format(note.getDateTimeCreation().getTime()));

        @SuppressLint("ResourceType")
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, Note.categories);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(note.getCategoryID());

        if (note.isFavourite())
            favoriteButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_yes, 0, 0);
        else
            favoriteButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_no, 0, 0);

        dateTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimeFragment(note);
           }
        });

    }

    private void showDateTimeFragment(Note note) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            showLandDateTime(note);
        else
            showPortDateTime(note);
    }

    private void showLandDateTime(Note note) {
        DateTimeFragment dateTimeFragment =
                DateTimeFragment.newInstance(note);
        FragmentManager fragmentManager =
                requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
// добавляем фрагмент
        fragmentTransaction
                .replace(R.id.fragmentNoteContainer, dateTimeFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("")
                .commit();
    }

    private void showPortDateTime(Note note) {
        DateTimeFragment dateTimeFragment =
                DateTimeFragment.newInstance(note);
        FragmentManager fragmentManager =
                requireActivity().getSupportFragmentManager();

        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
// добавляем фрагмент
        fragmentTransaction
                .replace(R.id.fragmentContainer, dateTimeFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("")
                .commit();

    }

    public static NoteTextFragment newInstance(Note note){
        NoteTextFragment noteTextFragment = new NoteTextFragment();
        Bundle args = new Bundle();
        args.putParcelable(LIST_TO_NOTE_INDEX, note);
        noteTextFragment.setArguments(args);
        return noteTextFragment;
}


}
