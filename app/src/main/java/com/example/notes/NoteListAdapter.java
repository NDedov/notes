package com.example.notes;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteListViewHolder> {

    private List<Note> list;

    private NotesListClickListener listener;

    public void setListener(NotesListClickListener listener) {
        this.listener = listener;
    }

    public List<Note> getList() {
        return list;
    }

    public void setList(List<Note> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public NoteListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_note, parent, false);
        return new NoteListViewHolder(v);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull NoteListViewHolder holder, int position) {

        holder.getItemView().<TextView>findViewById(R.id.titleItemListNoteTextView)
                .setText(list.get(position).getTitle());
        holder.getItemView().<TextView>findViewById(R.id.textItemListNoteTextView)
                .setText(list.get(position).getText());
        holder.getItemView().<TextView>findViewById(R.id.dateItemListNoteTextView)
                .setText(new SimpleDateFormat("dd MMMM yyyy  HH:mm")
                        .format(list.get(position).getDateTimeModify().getTime()));

        if (list.get(position).isFavourite())
            holder.getItemView().<ImageView>findViewById(R.id.favoriteImageItemListNote)
                    .setImageResource(R.drawable.ic_favorite_yes);
        else
            holder.getItemView().<ImageView>findViewById(R.id.favoriteImageItemListNote)
                    .setImageResource(R.drawable.ic_favorite_no);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NoteListViewHolder extends RecyclerView.ViewHolder {

        private View itemView;

        public NoteListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            /*itemView.findViewById(R.id.cardViewListNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Note note = list.get(getAdapterPosition());
                    listener.onClick(note);
                }
            });*/
            itemView.findViewById(R.id.linearCardView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Note note = list.get(getAdapterPosition());
                    listener.onClick(note);
                }
            });

/*
            itemView.findViewById(R.id.textItemListNoteTextView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Note note = list.get(getAdapterPosition());
                    listener.onClick(note);
                }
            });

           itemView.findViewById(R.id.titleItemListNoteTextView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Note note = list.get(getAdapterPosition());
                    listener.onClick(note);
                }


            });
            itemView.findViewById(R.id.dateItemListNoteTextView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Note note = list.get(getAdapterPosition());
                    listener.onClick(note);
                }
            });*/

            itemView.findViewById((R.id.favoriteLayout)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Note note = list.get(getAdapterPosition());

                    listener.onFavoriteClick(note, itemView);
                }
            });



        }

        public View getItemView() {
            return itemView;
        }
    }
}
