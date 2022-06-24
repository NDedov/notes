package com.example.notes.fragments.notes

import android.os.Bundle
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.notes.add.Constants
import com.example.notes.R

class DeleteNoteDialogFragment : DialogFragment(), Constants {
    private var listener: DeleteDialogListener? = null
    fun setListener(listener: DeleteDialogListener?) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity: Activity = requireActivity()
        return AlertDialog.Builder(activity)
                .setTitle(R.string.delete_note_question)
                .setPositiveButton(R.string.yes_delete) { _: DialogInterface?, _: Int -> listener!!.onDelete() }
                .setNegativeButton(R.string.no_delete) { _: DialogInterface?, _: Int -> listener!!.onNo() }
                .create()
    }
}