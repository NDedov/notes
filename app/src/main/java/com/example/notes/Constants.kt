package com.example.notes

internal interface Constants {
    companion object {
        const val LIST_TO_NOTE_INDEX = "LIST_TO_NOTE_INDEX" //ключ для передачи note из списка в NoteText
        const val PREVIEW_LIST_LENGTH = 20 // количество символов в превью в списке записок
        const val NOTES_LIST = "NOTES_LIST" // ключ для сохранения списка заметок
        const val FRAGMENT_TAG = "NoteListFragment"
        const val NOTE_CHANGED = "NOTE_CHANGED" // ключ для отлавливания изменения заметки
        const val NOTE_CHANGE_INDEX = "NOTE_CHANGE_INDEX" //индекя для передачи измененой заметки
        const val FILTER_INDEX = "FILTER_EXIT_INDEX" //ключ для передачи значения выбранной категории
        const val NOTE_DELETE = "NOTE_DELETE" // ключ для передачи заметки на удаление
        const val RESULT_OK_FILTER_EXIT_INDEX = "RESULT_OK_FILTER_EXIT_INDEX" //индекс для положительного

        // возврата из выбора категорий в фильтре
        const val DELETE_NOTE_DIALOG_TAG = "DELETE_NOTE_DIALOG_TAG" // тэг для диалогового окна удаления заметки

        // сохранения
        const val NOTE_IS_CHANGED_TAG = "NOTE_IS_CHANGED_TAG" //тэг для сохранения флага об изменении заметки
        const val SETTINGS_TAG = "SETTINGS_TAG" //тэг для передачи настроек
        const val SETTINGS_CHANGED_TAG = "SETTINGS_CHANGED_TAG" //тэг отмечающий изменение настроек
        const val NOTES_SHARED_P_KEY_NOTES = "NOTES_SHARED_P_KEY_NOTES" // ключ для хранения заметок
        const val NOTES_SHARED_P_KEY_SETTINGS = "NOTES_SHARED_P_KEY_SETTINGS" // ключ для хранения настроек
        const val NOTES_SHARED_P = "NOTES_SHARED_P" // файл для хранения
    }
}