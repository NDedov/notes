package com.example.notes;

interface Constants {
    String LIST_TO_NOTE_INDEX = "LIST_TO_NOTE_INDEX"; //ключ для передачи note из списка в NoteText
    int PREVIEW_LIST_LENGTH = 20; // количество символов в превью в списке записок
    String NOTES_LIST = "NOTES_LIST"; // ключ для сохранения списка заметок
    String FRAGMENT_TAG = "NoteListFragment";
    String NOTE_CHANGED = "NOTE_CHANGED"; // ключ для отлавливания изменения заметки
    String NOTE_CHANGE_INDEX = "NOTE_CHANGE_INDEX"; //индекя для передачи измененой заметки
    String FILTER_INDEX = "FILTER_EXIT_INDEX"; //ключ для передачи значения выбранной категории
    String NOTE_DELETE = "NOTE_DELETE"; // ключ для передачи заметки на удаление
    String RESULT_OK_FILTER_EXIT_INDEX = "RESULT_OK_FILTER_EXIT_INDEX"; //индекс для положительного
    // возврата из выбора категорий в фильтре
    String DELETE_NOTE_DIALOG_TAG = "DELETE_NOTE_DIALOG_TAG";// тэг для диалогового окна удаления заметки
    // сохранения
    String NOTE_IS_CHANGED_TAG = "NOTE_IS_CHANGED_TAG"; //тэг для сохранения флага об изменении заметки
    String SETTINGS_TAG = "SETTINGS_TAG"; //тэг для передачи настроек
    String SETTINGS_CHANGED_TAG = "SETTINGS_CHANGED_TAG"; //тэг отмечающий изменение настроек

    String NOTES_SHARED_P_KEY_NOTES = "NOTES_SHARED_P_KEY_NOTES";// ключ для хранения заметок
    String NOTES_SHARED_P_KEY_SETTINGS = "NOTES_SHARED_P_KEY_SETTINGS"; // ключ для хранения настроек
    String NOTES_SHARED_P = "NOTES_SHARED_P";// файл для хранения
}
