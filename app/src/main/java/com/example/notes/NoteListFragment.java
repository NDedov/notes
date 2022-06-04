package com.example.notes;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

public class NoteListFragment extends Fragment implements Constants {

    Notes notes;
    Button filterButton,filterFavoriteButton;
    FloatingActionButton addButton;


    Filter filter;
    EditText textSearchView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //В момент создания нового фрагмента мы проверяем, создается ли этот фрагмент впервые, и
        //если да, то просто удаляем его из бэкстека.
        if (savedInstanceState != null)
          requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_note_list, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater) {
        MenuItem item = menu.findItem(R.id.action_text_redo);//скрываем ненужные пункты меню
        if (item != null) {
            item.setVisible(false);
        }
        item = menu.findItem(R.id.action_text_undo);
        if (item != null) {
            item.setVisible(false);
        }
        item = menu.findItem(R.id.action_text_save);
        if (item != null) {
            item.setVisible(false);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            notes = savedInstanceState.getParcelable(NOTES_LIST);
            filter = savedInstanceState.getParcelable(FILTER_INDEX);

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
        ((MainActivity)requireActivity()).initToolbarAndDrawer(); //инициализация для Toolbar и Drawer

        if (isLandscape())
            showLandNotes(notes.getCurrentNote());
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
    @SuppressLint("SimpleDateFormat")
    private void initListNotes(View view) {
        LinearLayout layout = view.findViewById(R.id.linearListView);
        layout.removeAllViews();
        int tvAmount = 0;
        for (int i = 0; i < notes.size(); i++){

            if (filter.isShow(notes.get(i))){
                @SuppressLint("InflateParams") TextView tv =
                        (TextView)getLayoutInflater().inflate(R.layout.tvtemplate, null);// добавляем
                // view для заметок по шаблону

                //выводим информацию по заметке в view
                tv.setText(Html.fromHtml("<strong>" + notes.get(i).getTitle() +
                        "</strong><small><br/><br/>"+ preview(notes.get(i).getText()) +
                        "</small><br/><small>" + new SimpleDateFormat("dd MMMM yyyy  HH:mm")
                        .format(notes.get(i).getDateTimeModify().getTime())));

                //корректируем параметры view - отступы
                LinearLayout.LayoutParams textViewLayoutParams =
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                textViewLayoutParams.setMargins(16,12,16,12);
                tv.setLayoutParams(textViewLayoutParams);

                if (notes.get(i).isFavourite())//рисуем звездочку на избранном
                    drawFavorite(tv);

                layout.addView(tv);
                tvAmount++;

                //прописываем Листенеры для вью
                final Note note_position = notes.get(i);
//                final int position = i;
                tv.setOnClickListener(v -> {
                    notes.setCurrentNote(note_position);
                    showNotes(note_position);
                });

                //прописываем попап меню
                initPopupMenu(tv,layout, note_position);
            }
        }
        if (tvAmount == 0){//если нет заметок, то выводим информацию о том, что список пуст
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
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initPopupMenu(TextView tv, LinearLayout layout, Note note_position) {
        tv.setOnLongClickListener(view1 -> {
            Activity activity = requireActivity();
            PopupMenu popupMenu = new PopupMenu(activity, view1);
            activity.getMenuInflater().inflate(R.menu.list_popup, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {//инициализация пунктов меню
                switch (menuItem.getItemId()){
                    case (R.id.action_popup_edit):
                       // notes.setCurrentPosition(position);
                        notes.setCurrentNote(note_position);
                        showNotes(note_position);
                        return true;
                    case (R.id.action_popup_to_favorite):
                        note_position.setFavourite(true);
                        drawFavorite(tv);
                        return true;
                    case (R.id.action_popup_delete):
                        notes.delete(note_position);
                        tv.animate() //анимация на удаление
                                .translationXBy(1000)
                                .setDuration(150)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        layout.removeView(tv);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {
                                    }
                                })
                                .start();
                        return true;
                }
                return true;
            });
            popupMenu.show();
            return true;

        });

    }

    private void drawFavorite(TextView tv) {
        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_favorite_yes, 0);
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
        super.onSaveInstanceState(outState);
    }
}


