package com.example.notes;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.Objects;


public class NoteTextFragment extends Fragment implements Constants {

    Note note;//заметка
    TextView dateTimeView; // поле для даты/времени
    AppCompatButton favoriteButton; // кнопка Избранное
    AppCompatButton undoButton; // кнопка UNDO
    AppCompatButton saveButton; // кнопка сохранить

    TextView titleView; // заголовок
    Spinner categorySpinner; // список категорий
    TextView textView; // текст заметки


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_text, container, false);

    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null){// получаем из бандл текущую заметку
            note = arguments.getParcelable(LIST_TO_NOTE_INDEX);
            if (note != null)
                initViews(view);
        }

        // настраиваем FragmentResultListener при успешной смене даты в DateTimeFragment, обновляем
        // дату в текущем фрагменте
        getParentFragmentManager().setFragmentResultListener(RESULT_OK_DATE_EXIT_INDEX, this,
                (key, bundle) -> {
                    note = bundle.getParcelable(DATE_EXIT_INDEX);
                    dateTimeView.setText(new SimpleDateFormat("dd MMMM yyyy  HH:mm")//обновляем
                            // выводимое значение даты в заметке
                            .format(note.getDateTimeCreation().getTime()));
                    updateNoteList();// обновляем основной список заметок
                });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        updateNoteList();
        super.onSaveInstanceState(outState);

    }

    private void updateNoteList() {//метод для обновления основного списка заметок (NoteListFragment)
        Bundle result = new Bundle();
        note.setTitle(titleView.getText().toString());
        note.setText(textView.getText().toString());
        note.setCategoryID(categorySpinner.getSelectedItemPosition());
        result.putParcelable(NOTE_CHANGE_INDEX, note);
        getParentFragmentManager().setFragmentResult(NOTE_CHANGED, result);
    }

    private void initViews(View view) {
        titleView = view.findViewById(R.id.titleTextView);
        dateTimeView = view.findViewById(R.id.dateTimeView);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        textView = view.findViewById(R.id.textView);
        favoriteButton = view.findViewById(R.id.favoriteButton);
        undoButton = view.findViewById(R.id.undoButton);
        saveButton = view.findViewById(R.id.saveButton);
        printValues();
        initButtons();
        initListeners();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void displayToast(String text) {
        Toast toast = Toast.makeText(getContext(),
                text,
                Toast.LENGTH_SHORT);

        View toastView = toast.getView();
        toastView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_corner_toast, null));

        toast.show();

    }

    private void initListeners() {
        dateTimeView.setOnClickListener(view -> showDateTimeFragment(note));

        undoButton.setOnClickListener(view -> {
            textView.onTextContextMenuItem(android.R.id.undo);
            titleView.onTextContextMenuItem(android.R.id.undo);
        });

        saveButton.setOnClickListener(view -> {
            updateNoteList();
            if (isLandscape()){
                displayToast("Успешно сохранено");

               // Toast.makeText(getContext(),"Успешно сохранено",Toast.LENGTH_SHORT).show();
            }
            else {
                displayToast("Успешно сохранено");
               // Toast.makeText(getContext(),"Успешно сохранено",Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        favoriteButton.setOnClickListener(view -> {
            note.setFavourite(!note.isFavourite);
            showFavoriteButton();
            Bundle result = new Bundle();
            result.putParcelable(NOTE_CHANGE_INDEX, note);
            getParentFragmentManager().setFragmentResult(NOTE_CHANGED, result);
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void printValues() {
        titleView.setText(note.getTitle());
        textView.setText(note.getText());
        dateTimeView.setText(new SimpleDateFormat("dd MMMM yyyy  HH:mm")
                .format(note.getDateTimeCreation().getTime()));

        @SuppressLint("ResourceType")
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, Note.categories);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(note.getCategoryID());
    }


    private void initButtons() {
        showFavoriteButton();
        undoButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_undo2, 0, 0);
        saveButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_save2, 0, 0);
    }

    private void showFavoriteButton() {
        if (note.isFavourite())
            favoriteButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_yes, 0, 0);
        else
            favoriteButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_no, 0, 0);

    }

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }

    private void showDateTimeFragment(Note note) {
        if (isLandscape())
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
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
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
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack("")
                .commit();

    }

    public static NoteTextFragment newInstance(Note note) {
        NoteTextFragment noteTextFragment = new NoteTextFragment();
        Bundle args = new Bundle();
        args.putParcelable(LIST_TO_NOTE_INDEX, note);
        noteTextFragment.setArguments(args);
        return noteTextFragment;
    }
}
