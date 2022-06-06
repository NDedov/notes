package com.example.notes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.text.SimpleDateFormat;
import java.util.List;

public class NoteTextFragment extends Fragment implements Constants, DeleteDialogListener,OnBackPressedListener {

    Note note;//заметка
    TextView dateTimeView; // поле для даты/времени
    AppCompatButton favoriteButton; // кнопка Избранное
    AppCompatButton deleteButton; // кнопка Удалить
    AppCompatButton saveButton; // кнопка сохранить

    TextView titleView; // заголовок
    Spinner categorySpinner; // список категорий
    TextView textView; // текст заметки

    TextViewUndoRedo helperTextView;

    boolean flagForSpinner = false;// флаг для вызова обработчика только по нажатию,
    // что бы не срабатывал при инициализации

    boolean noteIsChanged = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            requireActivity().getSupportFragmentManager().popBackStack();
    }

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


        List<Fragment> fList = requireActivity().getSupportFragmentManager().getFragments();
        for (Fragment item: fList) {//находим ненужные фрагменты и удаляем, возникают при повороте
            if (!item.equals(this) && item instanceof NoteTextFragment){
                requireActivity().getSupportFragmentManager().beginTransaction().remove(item).commit();
            }
        }

        Bundle arguments = getArguments();
        if (arguments != null){// получаем из бандл текущую заметку
            note = arguments.getParcelable(LIST_TO_NOTE_INDEX);
            noteIsChanged = arguments.getBoolean(NOTE_IS_CHANGED_TAG);

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
                            .format(note.getDateTimeModify().getTime()));
                    updateNoteList();// обновляем основной список заметок
                });

        if (!isLandscape())
            setActionBar(view);


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (noteIsChanged)
            updateNoteList();
        super.onSaveInstanceState(outState);

    }

    private void updateNoteList() {//метод для обновления основного списка заметок (NoteListFragment)
        Bundle result = new Bundle();
        if (note != null){
            note.setTitle(titleView.getText().toString());
            note.setText(textView.getText().toString());
            note.setCategoryID(categorySpinner.getSelectedItemPosition());
            result.putParcelable(NOTE_CHANGE_INDEX, note);
            result.putBoolean(NOTE_IS_CHANGED_TAG, noteIsChanged);
            getParentFragmentManager().setFragmentResult(NOTE_CHANGED, result);
        }
    }

    private void initViews(View view) {
        titleView = view.findViewById(R.id.titleTextView);
        dateTimeView = view.findViewById(R.id.dateTimeView);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        textView = view.findViewById(R.id.textView);
        favoriteButton = view.findViewById(R.id.favoriteButton);
        deleteButton = view.findViewById(R.id.deleteButton);
        saveButton = view.findViewById(R.id.saveButton);
        printValues();

        initButtons();
        initListeners();
        InitEditListeners(view);
    }


    private void InitEditListeners(View view) {// обработчик изменений едитов и спиннера
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                noteIsChanged = true;
                setIconMenu();
                setSaveButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        titleView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                noteIsChanged = true;
                setIconMenu();
                setSaveButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (flagForSpinner) {
                    noteIsChanged = true;
                    setIconMenu();
                    setSaveButton();
                }
                flagForSpinner = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void displayToast(String text) {//кастомизированный тоаст
        Toast toast = Toast.makeText(getContext(),
                text,
                Toast.LENGTH_SHORT);

        View toastView = toast.getView();
        toastView.setBackground(ResourcesCompat.getDrawable(getResources(),
                R.drawable.rounded_corner_toast, null));
        toast.show();
    }

    private void initListeners() {//обработчики кнопок в нижнем "меню"
        dateTimeView.setOnClickListener(view -> showDateTimeFragment(note));

        deleteButton.setOnClickListener(view -> {//обработка кнопки удалить
            DeleteNoteDialogFragment deleteNoteDialogFragment = new DeleteNoteDialogFragment();
            deleteNoteDialogFragment.setListener(NoteTextFragment.this);
            deleteNoteDialogFragment.show(requireActivity().getSupportFragmentManager(),
                    DELETE_NOTE_DIALOG_TAG);
        });

        saveButton.setOnClickListener(view -> {//обработка кнопки сохранить
            if (noteIsChanged){
                updateNoteList();
                if (isLandscape()){
                    displayToast(getString(R.string.save_ok));
                    helperTextView = new TextViewUndoRedo(textView);
                    noteIsChanged = false;
                    setSaveButton();

                    //setIconMenu();
                }
                else {
                    displayToast(getString(R.string.save_ok));
                    noteIsChanged = false;
                    helperTextView = new TextViewUndoRedo(textView);
                    setIconMenu();
                    setSaveButton();
                    requireActivity().getSupportFragmentManager().popBackStack();

                }
            }
        });

        favoriteButton.setOnClickListener(view -> {//обработка кнопки Избранное
            note.setFavourite(!note.isFavourite());
            showFavoriteButton();
            Bundle result = new Bundle();
            result.putParcelable(NOTE_CHANGE_INDEX, note);
            getParentFragmentManager().setFragmentResult(NOTE_CHANGED, result);
        });
    }

    private void setActionBar(@NonNull View view) {//обработчик кнопки выхода в toolBar
        Toolbar toolbar = view.findViewById(R.id.toolbarNoteText);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> {//при нажатии на выход в тулбаре
            View view1 = requireActivity().getCurrentFocus();
            if (view1 != null) {//скрытие клавиатуры при выходе
                InputMethodManager imm = (InputMethodManager)requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            }

            if (noteIsChanged)
                updateNoteList();

            requireActivity().getSupportFragmentManager().popBackStack();
        });
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.text_menu, menu);
        setIconMenu();
    }

    private void setIconMenu() {//метод раскрашивания иконок меню
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbarNoteText);
        if (toolbar != null) {
            Menu menu = toolbar.getMenu();
            MenuItem itemSave = menu.findItem(R.id.action_text_save);
            MenuItem itemUndo = menu.findItem(R.id.action_text_undo);
            MenuItem itemRedo = menu.findItem(R.id.action_text_redo);
            if (itemSave != null) {
                if (noteIsChanged) {
                    menu.findItem(R.id.action_text_save).setIcon(R.drawable.ic_save);
                    menu.findItem(R.id.action_text_save).setEnabled(true);
                } else {
                    menu.findItem(R.id.action_text_save).setIcon(R.drawable.ic_save_grey);
                    menu.findItem(R.id.action_text_save).setEnabled(false);
                }
            }
            if (itemRedo != null) {
                if (helperTextView.getCanRedo()) {
                    menu.findItem(R.id.action_text_redo).setIcon(R.drawable.ic_redo_new);
                    menu.findItem(R.id.action_text_redo).setEnabled(true);
                } else {
                    menu.findItem(R.id.action_text_redo).setIcon(R.drawable.ic_redo_new_grey);
                    menu.findItem(R.id.action_text_redo).setEnabled(false);
                }
            }
            if (itemUndo != null) {
                if (helperTextView.getCanUndo()) {
                    menu.findItem(R.id.action_text_undo).setIcon(R.drawable.ic_undo_new);
                    menu.findItem(R.id.action_text_undo).setEnabled(true);
                } else {
                    menu.findItem(R.id.action_text_undo).setIcon(R.drawable.ic_undo_new_grey);
                    menu.findItem(R.id.action_text_undo).setEnabled(false);
                }
            }
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//обработчик меню
        switch (item.getItemId()){
            case R.id.action_text_save:
                if (noteIsChanged){ //отрабатываем сохранение если заметка изменилась
                    helperTextView = new TextViewUndoRedo(textView);
                    updateNoteList();
                    displayToast(getString(R.string.save_ok));
                    noteIsChanged = false;
                    setIconMenu();
                    setSaveButton();
                }
                return true;
            case R.id.action_text_redo:
                helperTextView.redo();
                setIconMenu();
                return true;
            case R.id.action_text_undo:
                helperTextView.undo();
                setIconMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("SimpleDateFormat")
    private void printValues() {
        titleView.setText(note.getTitle());
        textView.setText(note.getText());
        dateTimeView.setText(new SimpleDateFormat("dd MMMM yyyy  HH:mm")
                .format(note.getDateTimeModify().getTime()));

        @SuppressLint("ResourceType")
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, Note.categories);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(note.getCategoryID());

        helperTextView = new TextViewUndoRedo(textView);
    }


    private void initButtons() {
        showFavoriteButton();
        deleteButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_trash, 0, 0);
        setSaveButton();
    }

    private void setSaveButton() {
        if (noteIsChanged) {
            saveButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_save3, 0, 0);
            saveButton.setEnabled(true);
        }
        else{
            saveButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_save3_grey, 0, 0);
            saveButton.setEnabled(false);
        }

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

    @Override
    public void onDelete() {//обработчик кнопки удалить
        if (isLandscape()){
            Bundle result = new Bundle();
            result.putParcelable(NOTE_CHANGE_INDEX, note);
            getParentFragmentManager().setFragmentResult(NOTE_DELETE, result);
            requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            displayToast(getString(R.string.toast_note_delete));

        }
        else {
            Bundle result = new Bundle();
            result.putParcelable(NOTE_CHANGE_INDEX, note);
            getParentFragmentManager().setFragmentResult(NOTE_DELETE, result);
            requireActivity().getSupportFragmentManager().popBackStack();
            displayToast(getString(R.string.toast_note_delete));
        }
    }

    @Override
    public void onNo() {

    }

    @Override
    public void onBackPressed() {
        if (noteIsChanged)
            updateNoteList();
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
