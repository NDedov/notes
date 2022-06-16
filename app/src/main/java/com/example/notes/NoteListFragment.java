package com.example.notes;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


public class NoteListFragment extends Fragment implements Constants {

    private Notes notes;
    private Settings settings;
    private Filter filter;

    private Button filterButton,filterFavoriteButton, filterClearButton;
    private FloatingActionButton addButton;
    private RecyclerView recyclerView;
    private FrameLayout messageLayout;
    private EditText textSearchView;

    /**
     * Реализация интерфейса сохранения и восстановления списка заметок в SharedPreferences
     */
    private final IWorkSharedPreferences workSharedPreferences = new IWorkSharedPreferences() {

        private SharedPreferences sharedPreferences;

        @Override
        public void saveNotes(Notes notes) {
            sharedPreferences = requireActivity().getSharedPreferences(NOTES_SHARED_P,
                    Context.MODE_PRIVATE);
            String jsonNotes = new GsonBuilder().create().toJson(notes);
            sharedPreferences.edit().putString(NOTES_SHARED_P_KEY_NOTES, jsonNotes).apply();
        }

        @Override
        public Notes restoreNotes() {
            sharedPreferences = requireActivity().getSharedPreferences(NOTES_SHARED_P,
                    Context.MODE_PRIVATE);
            String savedNotes = sharedPreferences.getString(NOTES_SHARED_P_KEY_NOTES, null);
            if (savedNotes != null)
                return new GsonBuilder().create().fromJson(savedNotes, Notes.class);
            return null;
        }

        @Override
        public void saveSettings(Settings settings) {

        }

        @Override
        public Settings restoreSettings() {

            return null;
        }
    };

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
        return inflater.inflate(R.layout.fragment_note_list, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_list_settings)
            showSettingsFragment();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null)
            getParcelables(savedInstanceState);

        Bundle arguments = getArguments();//получаем настройки при открытии фрагмента
        if (arguments != null)
            settings = arguments.getParcelable(SETTINGS_TAG);

        if (notes == null) {//заполнение тестовыми заметками при первом запуске
            notes = workSharedPreferences.restoreNotes();
            if (notes == null) {
                notes = new Notes();
                notes.testFillNotes();
            }
            filter = new Filter(Filter.defaultFilterCategory, false, null);
            if (notes.getSize() > 0){
                notes.setCurrentNote(notes.get(0));
            }
        }

        initViews(view);
        initButtons(view);
        initListNotes(view);
        initSearch(view);
        initFragmentResultListeners(view);
        setHasOptionsMenu(true);

        if (isLandscape())
            showLandNotes(notes.getCurrentNote(), false);

        ((IDrawerFromFragment)requireActivity()).initDrawer();
    }

    private void showSettingsFragment() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, SettingsFragment.newInstance(settings))
                .addToBackStack("")
                .commit();
    }

    private void initViews(View view) {
        textSearchView = view.findViewById(R.id.textSearchView);
        filterClearButton = view.findViewById(R.id.filterClearButton);
        filterFavoriteButton = view.findViewById(R.id.filterFavoriteButton);
        addButton = view.findViewById(R.id.addFab);
        filterButton = view.findViewById(R.id.filterButton);
        recyclerView = view.findViewById(R.id.recyclerViewNoteList);
        messageLayout = view.findViewById(R.id.messageLayout);
    }

    private void getParcelables(Bundle savedInstanceState) {
        notes = savedInstanceState.getParcelable(NOTES_LIST);
        filter = savedInstanceState.getParcelable(FILTER_INDEX);
        settings = savedInstanceState.getParcelable(SETTINGS_TAG);
    }

    private void initSearch(View view) {
        textSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = textSearchView.getText().toString();
                filter.setSearchString(text);
                initListNotes(view);
                showClearFilterIcon();
            }

            @Override
            public void afterTextChanged(Editable editable){}
        });
    }

    private void initFragmentResultListeners(View view) {
        //прописываем Листенер, отлавливаем изменения в заметке из NoteTextFragment, обновляем список (превью)
        getParentFragmentManager().setFragmentResultListener(NOTE_CHANGED, this,
                (key, bundle) -> {
                    notes.setCurrentNote(bundle.getParcelable(NOTE_CHANGE_INDEX));
                    initListNotes(view);
                });

        //листенер на флаг удаления заметки
        getParentFragmentManager().setFragmentResultListener(NOTE_DELETE, this,
                (key, bundle) -> {
                    notes.delete(bundle.getParcelable(NOTE_CHANGE_INDEX));
                    initListNotes(view);
                    if (isLandscape())
                        showNotes(notes.getCurrentNote(), true);
                });
        //листенер, обрабатывающий изменение в фильтре категорий
        getChildFragmentManager().setFragmentResultListener(RESULT_OK_FILTER_EXIT_INDEX, this,
                (requestKey, result) -> {
                    filter.setCurrentFilterCategory(result.getInt(FILTER_INDEX, Filter.defaultFilterCategory));
                    initListNotes(view);
                    showClearFilterIcon();
                });
    }

    void hideKeyBoard(){//метод скрытия клавиатуры
        View view1 = requireActivity().getCurrentFocus();
        if (view1 != null) {//скрытие клавиатуры при выходе
            InputMethodManager imm = (InputMethodManager)requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
    }

    private void initButtons(View view) {
        initFilterButton();
        initAddButton();
        initFavoriteButton(view);
        initFilterClearButton();
    }

    private void initFilterClearButton() {
        showClearFilterIcon();
        filterClearButton.setOnClickListener(view1 -> {
            filter = new Filter(Filter.defaultFilterCategory, false, null);
            textSearchView.setText("");
            showFavoriteIcon();
            showClearFilterIcon();
            hideKeyBoard();
        });
    }

    private void initFavoriteButton(View view) {
        filterFavoriteButton.setOnClickListener(view1 -> {
            filter.setFavoriteShow(!filter.isFavoriteShow());
            showFavoriteIcon();
            initListNotes(view);
            showClearFilterIcon();
        });
        showFavoriteIcon();
    }

    private void initAddButton() {
        addButton.setOnClickListener(view1 -> {
            notes.add(new Note("", "", new GregorianCalendar(), 0, false));
            showNotes(notes.getCurrentNote(),true);
        });
    }

    private void initFilterButton() {
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
                showFilterChildFragment();
        });
    }

    private void showFilterChildFragment() {
        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right)
                .replace(R.id.filterChildLayout, FilterFragment
                        .newInstance(filter.getCurrentFilterCategory()))
                .addToBackStack("")
                .commit();
    }

    private void showClearFilterIcon() {
        if (filter.isFilterActive()) {
            filterClearButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_cancel, 0, 0);
            filterClearButton.setEnabled(true);
        }
        else {
            filterClearButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_cancel_grey, 0, 0);
            filterClearButton.setEnabled(false);
        }
    }

    private void showFavoriteIcon() {
        if (filter.isFavoriteShow())
            filterFavoriteButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_yes, 0, 0);
        else
            filterFavoriteButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_no, 0, 0);
    }

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * Метод фильтрации списка заметок, для показа
     * @return список для показа
     */
    public ArrayList<Note> getListToShow(){
        ArrayList<Note> list = new ArrayList<>();
        for (Note note: notes.getNotes()){//заполняем список по фильтру
            if (filter.isShow(note))
                list.add(note);
        }
        return list;
    }

    private void initListNotes(View view) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        NoteListAdapter adapter = new NoteListAdapter();
        adapter.setList(getListToShow());
        recyclerView.setAdapter(adapter);
        ((FrameLayout)view.findViewById(R.id.messageLayout)).removeAllViews();
        if (getListToShow().size() == 0)
            showEmptyList();
        initAdapterListener(adapter);
    }

    private void initAdapterListener(NoteListAdapter adapter) {
        adapter.setListener(new NotesListClickListener() {//реализация интерфейса для обработки адаптера recycleView
            @Override
            public void onClick(Note note) {
                if (isLandscape()){
                    if (!note.equals(notes.getCurrentNote())) {//отрабатываем только при смене
                        notes.setCurrentNote(note);
                        showNotes(note, true);
                    }
                }
                else{
                    notes.setCurrentNote(note);
                    showNotes(note, true);
                }
            }

            @Override
            public void onFavoriteClick(Note note, View itemView, int position) {//при нажатии на звездочку добавляем или удаляем из избранного
                note.setFavourite(!note.isFavourite());
                if (note.isFavourite())
                    itemView.<ImageView>findViewById(R.id.favoriteImageItemListNote)
                            .setImageResource(R.drawable.ic_favorite_yes);
                else
                    itemView.<ImageView>findViewById(R.id.favoriteImageItemListNote)
                            .setImageResource(R.drawable.ic_favorite_no);
                if (filter.isFavoriteShow()){
                    adapter.setList(getListToShow());
                    adapter.notifyDataSetChanged();
                }
                if (isLandscape()){
                    notes.setCurrentNote(note);
                    showNotes(note,false);
                }
            }

            @Override
            public void onLongClick(Note note, View view, int position) {//popup меню на заметке

                Activity activity = requireActivity();
                PopupMenu popupMenu = new PopupMenu(activity, view);
                activity.getMenuInflater().inflate(R.menu.list_popup, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(menuItem -> {//инициализация пунктов меню
                    switch (menuItem.getItemId()){
                        case (R.id.action_popup_edit):
                            notes.setCurrentNote(note);
                            showNotes(note,true);
                            return true;
                        case (R.id.action_popup_to_favorite):
                            note.setFavourite(true);
                            adapter.notifyItemChanged(position);
                            return true;
                        case (R.id.action_popup_delete):
                            notes.delete(note);
                            adapter.notifyItemRemoved(position);
                            adapter.setList(getListToShow());
                            if (getListToShow().size() == 0)
                                showEmptyList();
                            if (isLandscape())
                                showLandNotes(notes.getCurrentNote(),false);
                            return true;
                    }
                    return true;
                });
                popupMenu.show();
            }
        });

    }

    /**
     * Создает текствью в messageLayout и выводит сообщение о том, что список пуст
     */
    private void showEmptyList() {

        TextView tv = new TextView(getContext());
        tv.setText(R.string.message_empty_list);
        LinearLayout.LayoutParams textViewLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        textViewLayoutParams.setMargins(16,24,16,12);
        tv.setLayoutParams(textViewLayoutParams);
        tv.setTextSize(18);
        messageLayout.addView(tv);
    }


    public static NoteListFragment newInstance(Settings settings) {
        NoteListFragment noteListFragment = new NoteListFragment();
        Bundle args = new Bundle();
        args.putParcelable(SETTINGS_TAG, settings);
        noteListFragment.setArguments(args);
        return noteListFragment;
    }

    private void showNotes(Note note, boolean anim) {
        if (isLandscape())
            showLandNotes(note, anim);
        else
            showPortNotes(note);
    }

    // метод вызывающий показ фрагмента заметки для ланшафтной ориентации
    private void showLandNotes(Note note, boolean anim) {

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
            if (anim)
            fragmentTransaction
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right)
                    .replace(R.id.fragmentNoteContainer, noteTextFragment)
                    .commit();
            else
                fragmentTransaction
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

    @Override
    public void onStop() {
        workSharedPreferences.saveNotes(notes);
        super.onStop();
    }
}


