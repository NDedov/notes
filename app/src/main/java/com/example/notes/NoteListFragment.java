package com.example.notes;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

public class NoteListFragment extends Fragment implements Constants {

    ArrayList<Note> notes = new ArrayList<>();//список для хранения заметок
    private Note currentNote;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //В момент создания нового фрагмента мы проверяем, создается ли этот фрагмент впервые, и
       //если да, то просто удаляем его из бэкстека.
        if (savedInstanceState != null) {
            requireActivity().getSupportFragmentManager().popBackStack();
        }
        else{
            testFillNotes();//заполнение тестовыми заметками
            currentNote = notes.get(0);
        }


    }

  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null){
            currentNote = savedInstanceState.getParcelable(CURRENT_NOTE);
        }

        initListNotes(view);
        if (isLandscape()){
            showLandNotes(currentNote);
        }

    }

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }


    @SuppressLint("SimpleDateFormat")
    private void initListNotes(View view) {
        LinearLayout layout = view.findViewById(R.id.linearListView);
        layout.removeAllViews();

        for (int i = 0; i < notes.size(); i++){
            @SuppressLint("InflateParams") TextView tv =
                    (TextView)getLayoutInflater().inflate(R.layout.tvtemplate, null);// добавляем
            // view для заметок по шаблону

            //выводим информацию по заметке в view
            tv.setText(Html.fromHtml("<strong>" + notes.get(i).getTitle() + "</strong><small><br/><br/>"+
                    preview(notes.get(i).getText()) + "</small><br/><small>" +
                    new SimpleDateFormat("dd MMMM yyyy  HH:mm").format(notes.get(i).getDateTimeCreation().getTime())));

            //корректируем параметры view - отступы
            LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textViewLayoutParams.setMargins(16,12,16,12);
            tv.setLayoutParams(textViewLayoutParams);

            layout.addView(tv);

            //прописываем Листенеры для вью
            final Note note_position = notes.get(i);
            tv.setOnClickListener(v -> {
                currentNote = note_position;
                showNotes(note_position);
            });
        }
    }

    /**
     * Метод для создания превью текста заметки для списка заметок
     * @param text входной текст
     * @return обрезанный текст
     */
    private String preview(String text) {
        text = text.replace("\n"," ");
        text = text.replace("\t"," ");
        text = text.replace("\r"," ");
        if (text.length() < PREVIEW_LIST_LENGTH)
            return text;
        return text.substring(0,PREVIEW_LIST_LENGTH) + "...";
    }

    private void showNotes(Note note) {
        if (isLandscape())
            showLandNotes(note);
        else
            showPortNotes(note);
    }

    // метод вызывающий показ фрагмента заметки для ланшафтной ориентации
    private void showLandNotes(Note note) {
        NoteTextFragment noteTextFragment =
                NoteTextFragment.newInstance(note);
        FragmentManager fragmentManager =
                requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();

        // добавляем фрагмент
        fragmentTransaction
                .replace(R.id.fragmentNoteContainer, noteTextFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

    }

    // метод вызывающий показ фрагмента заметки для портретной ориентации
    private void showPortNotes(Note note) {
        NoteTextFragment noteTextFragment =
                NoteTextFragment.newInstance(note);
        FragmentManager fragmentManager =
                requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();

        // добавляем фрагмент
        fragmentTransaction
                .replace(R.id.fragmentContainer, noteTextFragment)
                .addToBackStack("")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void testFillNotes() {
        notes.add(new Note("Первая заметка", "Добрый день, \tкак дела?\nПривет", new GregorianCalendar(), 0, false));
        notes.add(new Note("Покупки", "Молоко, хлеб\nМасло\nМолоко", new GregorianCalendar(), 2, true));
        notes.add(new Note("Третья заметка", "Добрый день опять, как дела?\nПривет", new GregorianCalendar(), 1, false));
        notes.add(new Note("Новая заметка", "Добрый день, как дела?\n Привет", new GregorianCalendar(), 0, false));
        notes.add(new Note("Что надо сделать срочно", "Молоко, хлеб\n Масло", new GregorianCalendar(), 2, true));
        notes.add(new Note("Пароли", "Добрый день опять, как дела?\n Привет", new GregorianCalendar(), 4, false));
        notes.add(new Note("Прочее", "Добрый день, как дела?\n Привет", new GregorianCalendar(), 0, false));
        notes.add(new Note("Покупки", "Молоко, хлеб\n Масло", new GregorianCalendar(), 2, true));
        notes.add(new Note("Третья заметка", "Добрый день опять, как дела?\n Привет", new GregorianCalendar(), 0, false));
        notes.add(new Note("Первая заметка", "Добрый день, как дела?\n Привет", new GregorianCalendar(), 0, false));
        notes.add(new Note("Покупки", "Молоко, хлеб\n Масло", new GregorianCalendar(), 2, true));
        notes.add(new Note("Третья заметка", "Добрый день опять, как дела?\n Привет", new GregorianCalendar(), 0, false));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(CURRENT_NOTE, currentNote);
        super.onSaveInstanceState(outState);
    }
}


