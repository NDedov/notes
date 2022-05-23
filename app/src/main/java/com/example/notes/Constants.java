package com.example.notes;

interface Constants {
    String LIST_TO_NOTE_INDEX = "LIST_TO_NOTE_INDEX"; //ключ для передачи note из списка в NoteText
    String NOTE_TO_DATE_TIME_INDEX = "NOTE_TO_DATE_TIME_INDEX";//ключ для передачи note из NoteText
    // на корректировку времени/даты
    String DATE_EXIT_INDEX = "DATE_EXIT_INDEX"; //  ключ для возврата результа по корректировке времени/даты
    String RESULT_OK_DATE_EXIT_INDEX = "RESULT_OK_DATE_EXIT_INDEX"; // индекс для положительного
    // возврата из корректировки времени/даты, меняем итого
    int PREVIEW_LIST_LENGTH = 20; // количество символов в превью в списке записок
    String CURRENT_NOTE = "CURRENT_NOTE"; // ключ для текущей заметки
    String NOTES_LIST = "NOTES_LIST"; // ключ для сохранения списка заметок
    String FRAGMENT_TAG = "NoteListFragment";
    String NOTE_CHANGED = "NOTE_CHANGED"; // ключ для отлавливания изменения заметки
    String NOTE_CHANGE_INDEX = "NOTE_CHANGE_INDEX"; //индекя для передачи измененой заметки

}
