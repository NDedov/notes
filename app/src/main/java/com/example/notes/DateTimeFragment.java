package com.example.notes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class DateTimeFragment extends Fragment implements Constants {

    Note note, noteTmp;
    Button buttonExit;
    DatePicker datePicker;
    TimePicker timePicker;

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
        return inflater.inflate(R.layout.fragment_date_time, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null){
            note = arguments.getParcelable(NOTE_TO_DATE_TIME_INDEX);
            noteTmp = new Note(note.getTitle(),//создаем временный объект для корректировки даты
                    note.getText(),
                    note.getDateTimeCreation(),
                    note.getCategoryID(),
                    note.isFavourite());
            initViews(view);
        }
    }

    private void initViews(View view) {
        buttonExit = view.findViewById(R.id.buttonExit);
        datePicker = view.findViewById(R.id.datePicker);
        timePicker = view.findViewById(R.id.timePicker);

        initDatePicker(datePicker);
        initTimePicker(timePicker);
        initButtonExit(buttonExit);
    }

    private void initButtonExit(Button buttonExit) {
        buttonExit.setOnClickListener(view -> {

            Bundle result = new Bundle();
            note = noteTmp;
            result.putParcelable(DATE_EXIT_INDEX, note);
            getParentFragmentManager().setFragmentResult(RESULT_OK_DATE_EXIT_INDEX, result);
            getParentFragmentManager().popBackStack();
        });
    }

    private void initTimePicker(TimePicker timePicker) {
        timePicker.setHour(noteTmp.getDateTimeCreation().get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(noteTmp.getDateTimeCreation().get(Calendar.MINUTE));
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener((timePicker1, i, i1) -> setDateTime(timePicker1));
    }

    private void initDatePicker(DatePicker datePicker) {
        datePicker.init(noteTmp.getDateTimeCreation().get(Calendar.YEAR),
                noteTmp.getDateTimeCreation().get(Calendar.MONTH),
                noteTmp.getDateTimeCreation().get(Calendar.DAY_OF_MONTH),
                (datePicker1, i, i1, i2) -> setDateTime(datePicker1));
    }

    private void setDateTime(TimePicker timePicker) {
        Calendar tmpCalendar = new GregorianCalendar();
        tmpCalendar.set(Calendar.YEAR, datePicker.getYear());
        tmpCalendar.set(Calendar.MONTH, datePicker.getMonth());
        tmpCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        tmpCalendar.set(Calendar.HOUR_OF_DAY,timePicker.getHour());
        tmpCalendar.set(Calendar.MINUTE,timePicker.getMinute());
        noteTmp.setDateTimeCreation(tmpCalendar);
    }

    private void setDateTime(DatePicker datePicker) {
        Calendar tmpCalendar = new GregorianCalendar();
        tmpCalendar.set(Calendar.YEAR, datePicker.getYear());
        tmpCalendar.set(Calendar.MONTH, datePicker.getMonth());
        tmpCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        tmpCalendar.set(Calendar.HOUR_OF_DAY,timePicker.getHour());
        tmpCalendar.set(Calendar.MINUTE,timePicker.getMinute());
        noteTmp.setDateTimeCreation(tmpCalendar);
    }


    public static DateTimeFragment newInstance(Note note){
        DateTimeFragment dateTimeFragment = new DateTimeFragment();
        Bundle args = new Bundle();
        args.putParcelable(NOTE_TO_DATE_TIME_INDEX, note);
        dateTimeFragment.setArguments(args);
        return dateTimeFragment;
    }
}


