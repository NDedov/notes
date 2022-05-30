package com.example.notes;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.List;

public class NoteListFragment extends Fragment implements Constants {

    Notes notes;
    Button filterButton,filterFavoriteButton;
    Filter filter;

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
        return inflater.inflate(R.layout.fragment_note_list, container, false);
    }

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
        initFragmentResultListeners(view);

        MainActivity ma = (MainActivity) getActivity();
        assert ma != null;
        ma.initToolbarAndDrawer();//связываем для бутерброда

        if (isLandscape())
            showLandNotes(notes.get(notes.getCurrentPosition()));
    }

    private void setActionBar(@NonNull View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbarNoteList);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
    }



    private void initFragmentResultListeners(View view) {
        //прописываем Листенер, отлавливаем изменения в заметке из NoteTextFragment, обновляем список (превью)
        getParentFragmentManager().setFragmentResultListener(NOTE_CHANGED, this,
                (key, bundle) -> {
                    notes.replaceCurrent(bundle.getParcelable(NOTE_CHANGE_INDEX));
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

    private void initButtons(View view) {
        filterButton = view.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(view1 -> {
            List<Fragment> fragmentList = getChildFragmentManager().getFragments();
            boolean isFilterShow = false;
            for (Fragment fragment: fragmentList)//проверяем есть ли уже открытый фрагмент с фильтром
                if (fragment instanceof FilterFragment)
                    isFilterShow = true;

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

        filterFavoriteButton = view.findViewById(R.id.filterFavoriteButton);
        filterFavoriteButton.setOnClickListener(view1 -> {
            filter.setFavoriteShow(!filter.isFavoriteShow());
            if (filter.isFavoriteShow())
                filterFavoriteButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_yes, 0, 0);
            else
                filterFavoriteButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_no, 0, 0);
            initListNotes(view);

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

            if (filter.isShow(notes.get(i))){
                @SuppressLint("InflateParams") TextView tv =
                        (TextView)getLayoutInflater().inflate(R.layout.tvtemplate, null);// добавляем
                // view для заметок по шаблону

                //выводим информацию по заметке в view
                tv.setText(Html.fromHtml("<strong>" + notes.get(i).getTitle() +
                        "</strong><small><br/><br/>"+ preview(notes.get(i).getText()) +
                        "</small><br/><small>" + new SimpleDateFormat("dd MMMM yyyy  HH:mm")
                        .format(notes.get(i).getDateTimeCreation().getTime())));

                //корректируем параметры view - отступы
                LinearLayout.LayoutParams textViewLayoutParams =
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                textViewLayoutParams.setMargins(16,12,16,12);
                tv.setLayoutParams(textViewLayoutParams);

                if (notes.get(i).isFavourite())//рисуем звездочку на избранном
                    drawFavorite(tv);

                layout.addView(tv);

                //прописываем Листенеры для вью
                final Note note_position = notes.get(i);
                final int position = i;
                tv.setOnClickListener(v -> {
                    notes.setCurrentPosition(position);
                    showNotes(note_position);
                });

                //прописываем попап меню
                initPopupMenu(tv,layout, note_position, position);
            }
        }
    }

    private void initPopupMenu(TextView tv, LinearLayout layout, Note note_position, int position) {
        tv.setOnLongClickListener(view1 -> {
            Activity activity = requireActivity();
            PopupMenu popupMenu = new PopupMenu(activity, view1);
            activity.getMenuInflater().inflate(R.menu.list_popup, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case (R.id.action_popup_edit):
                            notes.setCurrentPosition(position);
                            showNotes(note_position);
                            return true;
                        case (R.id.action_popup_to_favorite):
                            notes.get(position).setFavourite(true);
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
                }
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
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right)
                .replace(R.id.fragmentNoteContainer, noteTextFragment)
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
        outState.putParcelable(FILTER_INDEX,filter);
        super.onSaveInstanceState(outState);
    }
}


