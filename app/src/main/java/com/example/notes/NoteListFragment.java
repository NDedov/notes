package com.example.notes;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class NoteListFragment extends Fragment implements Constants {

    Notes notes;
    Button filterButton,filterFavoriteButton;
    FloatingActionButton addButton;

    Settings settings;
    Filter filter;
    EditText textSearchView;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //В момент создания нового фрагмента мы проверяем, создается ли этот фрагмент впервые, и
        //если да, то просто удаляем его из бэкстека.
        FragmentManager fragmentManager =
                requireActivity().getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (!(fragment instanceof NoteListFragment))
                fragmentManager.beginTransaction().remove(fragment).commit();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_note_list, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_list_settings) {
            showSettingsFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSettingsFragment() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, SettingsFragment.newInstance(settings))
                .addToBackStack("")
                .commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            notes = savedInstanceState.getParcelable(NOTES_LIST);
            filter = savedInstanceState.getParcelable(FILTER_INDEX);
            settings = savedInstanceState.getParcelable(SETTINGS_TAG);
        }

        Bundle arguments = getArguments();//получаем настройки при открытии фрагмента
        if (arguments != null){
            settings = arguments.getParcelable(SETTINGS_TAG);
        }

        if (notes == null) {//заполнение тестовыми заметками при первом запуске
            notes = new Notes();
            filter = new Filter(Filter.defaultFilterCategory, false, null);
            notes.testFillNotes();
        }

        initButtons(view);
        initListNotes(view);
        initSearch(view);
        initFragmentResultListeners(view);
        setHasOptionsMenu(true);

        if (isLandscape())
            showLandNotes(notes.getCurrentNote());
        else
            ((IDrawerFromFragment)requireActivity()).initDrawer();

    }

    private void initSearch(View view) {
        textSearchView = view.findViewById(R.id.textSearchView);
        textSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = textSearchView.getText().toString();
                filter.setSearchString(text);
                initListNotes(view);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initFragmentResultListeners(View view) {
        //прописываем Листенер, отлавливаем изменения в заметке из NoteTextFragment, обновляем список (превью)
        getParentFragmentManager().setFragmentResultListener(NOTE_CHANGED, this,
                (key, bundle) -> {
                    notes.setCurrentNote(bundle.getParcelable(NOTE_CHANGE_INDEX));
                    initListNotes(view);
                });

        //листенер на фла удаления заметки
        getParentFragmentManager().setFragmentResultListener(NOTE_DELETE, this,
                (key, bundle) -> {
                    notes.delete(bundle.getParcelable(NOTE_CHANGE_INDEX));

                    initListNotes(view);
                });
        //листенер, обрабатывающий изменение в фильтре категорий
        getChildFragmentManager().setFragmentResultListener(RESULT_OK_FILTER_EXIT_INDEX, this,
                (requestKey, result) -> {
                    filter.setCurrentFilterCategory(result.getInt(FILTER_INDEX, Filter.defaultFilterCategory));
                    initListNotes(view);
                });
        //листенер, обрабатывающий изменение в настройках
//        getParentFragmentManager().setFragmentResultListener(SETTINGS_CHANGED_TAG, this,
//                (key, bundle) -> {
//                    applySettings();
//                });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initButtons(View view) {
        filterButton = view.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(view1 -> {
            List<Fragment> fragmentList = getChildFragmentManager().getFragments();
            boolean isFilterShow = false;
            for (Fragment fragment: fragmentList)//проверяем есть ли уже открытый фрагмент с фильтром
                if (fragment instanceof FilterFragment) {
                    isFilterShow = true;
                    break;
                }

            if (isFilterShow)
                getChildFragmentManager().popBackStack();
            else
                getChildFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right)
                        .replace(R.id.filterChildLayout, FilterFragment
                                .newInstance(filter.getCurrentFilterCategory()))
                        .addToBackStack("")
                        .commit();
        });

        addButton = view.findViewById(R.id.addFab);
        addButton.setOnClickListener(view1 -> {
            notes.add(new Note("", "", new GregorianCalendar(), 0, false));
            showNotes(notes.getCurrentNote());
        });

        filterFavoriteButton = view.findViewById(R.id.filterFavoriteButton);
        filterFavoriteButton.setOnClickListener(view1 -> {
            filter.setFavoriteShow(!filter.isFavoriteShow());
            showFavoriteIcon(filter.isFavoriteShow());
            initListNotes(view);
        });
        showFavoriteIcon(filter.isFavoriteShow());
    }

    private void showFavoriteIcon(boolean favoriteShow) {
        if (favoriteShow)
            filterFavoriteButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_yes, 0, 0);
        else
            filterFavoriteButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_no, 0, 0);
    }

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initListNotes(View view) {
        ArrayList<Note> list = new ArrayList<>();

        for (Note note: notes.getNotes()){//заполняем список по фильтру
            if (filter.isShow(note))
                list.add(note);
        }

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewNoteList);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);

        NoteListAdapter adapter = new NoteListAdapter();
        adapter.setList(list);
        recyclerView.setAdapter(adapter);
        adapter.setListener(new NotesListClickListener() {//реализация интерфейса для обработки адаптера recycleView
            @Override
            public void onClick(Note note) {
                notes.setCurrentNote(note);
                showNotes(note);
            }

            @Override
            public void onFavoriteClick(Note note, View itemView) {//при нажатии на звездочку добавляем или удаляем из избранного
                note.setFavourite(!note.isFavourite());
                if (note.isFavourite())
                    itemView.<ImageView>findViewById(R.id.favoriteImageItemListNote)
                            .setImageResource(R.drawable.ic_favorite_yes);
                else
                    itemView.<ImageView>findViewById(R.id.favoriteImageItemListNote)
                            .setImageResource(R.drawable.ic_favorite_no);
            }

            @Override
            public void onLongClick(Note note, View view, int position) {

                Activity activity = requireActivity();
                PopupMenu popupMenu = new PopupMenu(activity, view);
                activity.getMenuInflater().inflate(R.menu.list_popup, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(menuItem -> {//инициализация пунктов меню
                    switch (menuItem.getItemId()){
                        case (R.id.action_popup_edit):
                            notes.setCurrentNote(note);
                            showNotes(note);
                            return true;
                        case (R.id.action_popup_to_favorite):
                            note.setFavourite(true);
                            adapter.notifyItemChanged(position);
                            return true;

                        case (R.id.action_popup_delete):
                            notes.delete(note);
                            adapter.notifyItemRemoved(position);
                            if (isLandscape())
                                showLandNotes(notes.getCurrentNote());
                            return true;
                    }
                    return true;
                });
                popupMenu.show();

            }
        });
    }
    /**
     * Создает текствью в layout и выводит сообщение о том, что список пуст
     * @param layout входной layout
     */
    private void showEmptyList(LinearLayout layout) {
        TextView tv = new TextView(getContext());
        tv.setText("Список пуст...");
        LinearLayout.LayoutParams textViewLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        textViewLayoutParams.setMargins(16,24,16,12);
        tv.setLayoutParams(textViewLayoutParams);
        tv.setTextSize(18);
        layout.addView(tv);
    }


    public static NoteListFragment newInstance(Settings settings) {
        NoteListFragment noteListFragment = new NoteListFragment();
        Bundle args = new Bundle();
        args.putParcelable(SETTINGS_TAG, settings);
        noteListFragment.setArguments(args);
        return noteListFragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showNotes(Note note) {
        if (isLandscape())
            showLandNotes(note);
        else
            showPortNotes(note);
    }

    // метод вызывающий показ фрагмента заметки для ланшафтной ориентации
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showLandNotes(Note note) {

        if (note == null && notes.getSize() > 0) {//показываем первый по умолчанию
            notes.setCurrentNote(notes.get(0));
            note = notes.getCurrentNote();
        }

        if (note != null){
            NoteTextFragment noteTextFragment =
                    NoteTextFragment.newInstance(note);
            FragmentManager fragmentManager =
                    requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            // добавляем фрагмент
            fragmentTransaction
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right)
                    .replace(R.id.fragmentNoteContainer, noteTextFragment)
                    .commit();

        }
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
        outState.putParcelable(FILTER_INDEX,filter);
        outState.putParcelable(SETTINGS_TAG, settings);
        super.onSaveInstanceState(outState);
    }
}


