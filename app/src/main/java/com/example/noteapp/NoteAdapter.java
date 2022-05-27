package com.example.noteapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder>{
    private Context context;
    private List<Notes> listNode;
    private NoteClickListener listener;

    public NoteAdapter(Context context, List<Notes> listNode, NoteClickListener listener) {
        this.context = context;
        this.listNode = listNode;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.note_row,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Notes note = listNode.get(position);

        holder.note_title.setText(note.getTitle());
        holder.note_title.setSelected(true);

        holder.note_desc.setText(note.getContent());

        holder.time_create.setText(note.getDate_create());
        holder.time_create.setSelected(true);

        if(note.isPinned()) {
            holder.pin_note.setVisibility(View.VISIBLE);
        }
        else {
            holder.pin_note.setVisibility(View.GONE);
        }

        if(note.getLabel().trim().equals("")) {
            holder.has_label.setVisibility(View.GONE);
        }
        else {
            holder.has_label.setVisibility(View.VISIBLE);
        }

        if(note.getPassword().equals("")) {
            holder.has_pass.setVisibility(View.GONE);
        }
        else {
            holder.has_pass.setVisibility(View.VISIBLE);
        }

//        holder.layout_root.setBackgroundColor(Color.parseColor(color_code));

        holder.layout_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(listNode.get(holder.getAdapterPosition()));
            }
        });

        holder.layout_root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(listNode.get(holder.getAdapterPosition()), holder.layout_root);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listNode.size();
    }

    public void filterList(List<Notes> containList) {
        listNode = containList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        LinearLayout layout_root;
        TextView note_title, note_desc, time_create;
        ImageView pin_note, has_label, has_pass;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_root = itemView.findViewById(R.id.layout_root);
            note_title = itemView.findViewById(R.id.note_title);
            note_desc = itemView.findViewById(R.id.note_desc);
            time_create = itemView.findViewById(R.id.time_create);
            pin_note = itemView.findViewById(R.id.pin_note);
            has_label = itemView.findViewById(R.id.has_label);
            has_pass = itemView.findViewById(R.id.has_pass);
        }
    }
}