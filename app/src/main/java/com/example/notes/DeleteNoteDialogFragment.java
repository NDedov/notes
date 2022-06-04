package com.example.notes;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DeleteNoteDialogFragment extends DialogFragment implements Constants {

    private DeleteDialogListener listener;

    public void setListener(DeleteDialogListener listener) {
        this.listener = listener;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Activity activity = requireActivity();
        return new AlertDialog.Builder(activity)
                .setTitle(R.string.delete_note_question)
                .setPositiveButton(R.string.yes_delete, (dialogInterface, i) -> listener.onDelete())
                .setNegativeButton(R.string.no_delete, (dialogInterface, i) -> listener.onNo())
                .create();
    }


}
