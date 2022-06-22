package com.example.notes;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class NoteTextFragment extends Fragment implements Constants, DeleteDialogListener,OnBackPressedListener {

    private Note note;//заметка
    private TextView dateTimeView; // поле для даты/времени
    private AppCompatButton favoriteButton; // кнопка Избранное
    private AppCompatButton deleteButton; // кнопка Удалить
    private AppCompatButton saveButton; // кнопка сохранить
    private AppCompatButton shareButton; // кнопка сохранить
    private TextView titleView; // заголовок
    private Spinner categorySpinner; // список категорий
    private TextView textView; // текст заметки
    private TextViewUndoRedo helperTextView; //вспомогательный объект для обработки undo/redo
    private boolean flagForSpinner = false;// флаг для вызова обработчика только по нажатию,
    // что бы не срабатывал при инициализации
    private boolean noteIsChanged = false; //флаг фиксирующий, что заметка изменилась

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
        shareButton = view.findViewById(R.id.shareButton);
        printValues();
        initButtons();
        initListeners();
        InitEditListeners();
    }

    private void InitEditListeners() {// обработчик изменений едитов и спиннера

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                noteIsChangedActions();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        titleView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                noteIsChangedActions();
            }

            @Override
            public void afterTextChanged(Editable editable){}
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (flagForSpinner)
                    noteIsChangedActions();
                flagForSpinner = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView){}
        });
    }

    public void noteIsChangedActions(){
        noteIsChanged = true;
        setIconMenu();
        setSaveButton();
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

    private void initListeners() {//обработчики кнопок
        initDateTimeViewListener();
        initDeleteButtonListener();
        initShareButtonListener();
        initSaveButtonListener();
        initFavoriteButtonListener();
    }

    private void initDeleteButtonListener() {
        deleteButton.setOnClickListener(view -> {//обработка кнопки удалить
            hideKeyBoard();
            DeleteNoteDialogFragment deleteNoteDialogFragment = new DeleteNoteDialogFragment();
            deleteNoteDialogFragment.setListener(NoteTextFragment.this);
            deleteNoteDialogFragment.show(requireActivity().getSupportFragmentManager(),
                    DELETE_NOTE_DIALOG_TAG);
        });
    }

    private void initShareButtonListener() {
        shareButton.setOnClickListener(view -> {//обработка кнопки поделиться
            String shareBody = textView.getText().toString();
            String shareSub = titleView.getText().toString();
            if (shareBody.equals("") && shareSub.equals(""))
                displayToast(getString(R.string.message_nothing_to_share));
            else {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
                intent.putExtra(Intent.EXTRA_TEXT,shareSub + "\n" + shareBody);
                startActivity(Intent.createChooser(intent,getString(R.string.share_message)));
            }
        });
    }

    private void initSaveButtonListener() {
        saveButton.setOnClickListener(view -> {//обработка кнопки сохранить
            hideKeyBoard();
            if (noteIsChanged){
                updateNoteList();
                if (isLandscape()){
                    displayToast(getString(R.string.save_ok));
                    helperTextView = new TextViewUndoRedo(textView);
                    noteIsChanged = false;
                    setSaveButton();
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
    }

    private void initFavoriteButtonListener() {
        favoriteButton.setOnClickListener(view -> {//обработка кнопки Избранное
            note.setFavourite(!note.isFavourite());
            showFavoriteButton();
            Bundle result = new Bundle();
            result.putParcelable(NOTE_CHANGE_INDEX, note);
            getParentFragmentManager().setFragmentResult(NOTE_CHANGED, result);
        });
    }

    private void initDateTimeViewListener() { //обработка едита с датой временем
        dateTimeView.setOnClickListener(view -> {
            hideKeyBoard();
            showDateTimeDialog(note);
        });
    }

    private void showDateTimeDialog(Note note) {//диалог по смене "даты". Меняет только визуально

        new DatePickerDialog(requireContext(), 0, (datePicker, i, i1, i2) -> {
            Calendar tmpCalendar = new GregorianCalendar();
            tmpCalendar.set(Calendar.YEAR, datePicker.getYear());
            tmpCalendar.set(Calendar.MONTH, datePicker.getMonth());
            tmpCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
            dateTimeView.setText(new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                    .format(tmpCalendar.getTime()));

        }, note.getDateTimeModify().get(Calendar.YEAR),
                note.getDateTimeModify().get(Calendar.MONTH),
                note.getDateTimeModify().get(Calendar.DAY_OF_MONTH)).show();
    }

    void hideKeyBoard(){
        View view1 = requireActivity().getCurrentFocus();
        if (view1 != null) {//скрытие клавиатуры при выходе
            InputMethodManager imm = (InputMethodManager)requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
    }

    private void setActionBar(@NonNull View view) {//обработчик кнопки выхода в toolBar
        Toolbar toolbar = view.findViewById(R.id.toolbarNoteText);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> {//при нажатии на выход в тулбаре
            hideKeyBoard();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//обработчик меню
        switch (item.getItemId()){
            case (R.id.action_text_save):
                if (noteIsChanged){ //отрабатываем сохранение если заметка изменилась
                    helperTextView = new TextViewUndoRedo(textView);
                    updateNoteList();
                    displayToast(getString(R.string.save_ok));
                    noteIsChanged = false;
                    setIconMenu();
                    setSaveButton();
                }
                return true;
            case (R.id.action_text_redo):
                helperTextView.redo();
                setIconMenu();
                return true;
            case (R.id.action_text_undo):
                helperTextView.undo();
                setIconMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void printValues() {
        titleView.setText(note.getTitle());
        textView.setText(note.getText());
        dateTimeView.setText(new SimpleDateFormat("dd MMMM yyyy  HH:mm", Locale.getDefault())
                .format(note.getDateTimeModify().getTime()));
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, Note.categories);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(note.getCategoryID());
        helperTextView = new TextViewUndoRedo(textView);
    }

    private void initButtons() {
        showFavoriteButton();
        deleteButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_trash, 0, 0);
        shareButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_share, 0, 0);
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

    public static NoteTextFragment newInstance(Note note) {
        NoteTextFragment noteTextFragment = new NoteTextFragment();
        Bundle args = new Bundle();
        args.putParcelable(LIST_TO_NOTE_INDEX, note);
        noteTextFragment.setArguments(args);
        return noteTextFragment;
    }

    @Override
    public void onDelete() {//обработчик кнопки удалить
        Bundle result = new Bundle();
        result.putParcelable(NOTE_CHANGE_INDEX, note);
        getParentFragmentManager().setFragmentResult(NOTE_DELETE, result);
        if (isLandscape())
            requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        else
            requireActivity().getSupportFragmentManager().popBackStack();
        displayToast(getString(R.string.toast_note_delete));
    }

    @Override
    public void onNo() {}

    @Override
    public void onBackPressed() {
        if (noteIsChanged)
            updateNoteList();
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
