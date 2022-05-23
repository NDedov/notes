package com.example.notes;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
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

    Notes notes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //В момент создания нового фрагмента мы проверяем, создается ли этот фрагмент впервые, и
       //если да, то просто удаляем его из бэкстека.
        if (savedInstanceState != null) {
          requireActivity().getSupportFragmentManager().popBackStack();
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


        if (savedInstanceState != null)
            notes = savedInstanceState.getParcelable(NOTES_LIST);

        if (notes == null) {
            notes = new Notes();
            notes.testFillNotes();//заполнение тестовыми заметками
        }
        
        initListNotes(view);

        if (isLandscape()) {
            showLandNotes(notes.get(notes.getCurrentPosition()));
        }

        //отлавливаем изменения в заметке, обновляем список (превью)
        getParentFragmentManager().setFragmentResultListener(NOTE_CHANGED, this,
                new FragmentResultListener() {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onFragmentResult(@NonNull String key, @NonNull Bundle bundle) {
                        Note note;
                        note = bundle.getParcelable(NOTE_CHANGE_INDEX);
                        notes.replaceCurrent(note);
                        initListNotes(view);
                    }
                });

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
            final int position = i;
            tv.setOnClickListener(v -> {
                notes.setCurrentPosition(position);
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



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(NOTES_LIST, notes);
        super.onSaveInstanceState(outState);
    }
}


