package com.example.notes.fragments.notes

import androidx.recyclerview.widget.RecyclerView
import com.example.notes.fragments.notes.NoteListAdapter.NoteListViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import android.widget.ImageView
import com.example.notes.add.Constants
import com.example.notes.R
import java.text.SimpleDateFormat

class NoteListAdapter : RecyclerView.Adapter<NoteListViewHolder>(), Constants {
    private var list: List<Note>? = null
    private var listener: NotesListClickListener? = null
    fun setListener(listener: NotesListClickListener?) {
        this.listener = listener
    }

    fun setList(list: List<Note>?) {
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteListViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_list_note, parent, false)
        return NoteListViewHolder(v)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: NoteListViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.titleItemListNoteTextView).text = list!![position].getTitle()
        holder.itemView.findViewById<TextView>(R.id.textItemListNoteTextView).text = preview(list!![position].getText())
        holder.itemView.findViewById<TextView>(R.id.dateItemListNoteTextView).text = SimpleDateFormat("dd MMMM yyyy  HH:mm")
                .format(list!![position].dateTimeModify!!.time)
        if (list!![position].isFavourite()) holder.itemView.findViewById<ImageView>(R.id.favoriteImageItemListNote)
                .setImageResource(R.drawable.ic_favorite_yes) else holder.itemView.findViewById<ImageView>(
            R.id.favoriteImageItemListNote
        )
                .setImageResource(R.drawable.ic_favorite_no)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    inner class NoteListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemView: View
            get() = itemView

        init {
            //this.itemView = itemView
            itemView.findViewById<View>(R.id.linearCardView).setOnLongClickListener { view: View? ->
                val note = list!![adapterPosition]
                listener!!.onLongClick(note, view, adapterPosition)
                true
            }
            itemView.findViewById<View>(R.id.linearCardView).setOnClickListener {
                val note = list!![adapterPosition]
                listener!!.onClick(note)
            }
            itemView.findViewById<View>(R.id.favoriteLayout).setOnClickListener {
                val note = list!![adapterPosition]
                listener!!.onFavoriteClick(note, itemView, adapterPosition)
            }
        }
    }

    /**
     * Метод для создания превью текста заметки для списка заметок
     * @param text входной текст
     * @return обрезанный текст
     */
    private fun preview(text: String?): String {
        var text = text
        text = text!!.replace("\n", " ")
        text = text.replace("\t", " ")
        text = text.replace("\r", " ")
        return if (text.length < Constants.PREVIEW_LIST_LENGTH) text else text.substring(0,
            Constants.PREVIEW_LIST_LENGTH
        ) + "..."
    }
}