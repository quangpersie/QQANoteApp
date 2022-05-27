package com.example.noteapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Notes note = listNode.get(position);

        holder.note_title.setText(note.getTitle());
        holder.note_title.setSelected(true);
        switch (note.getFontSize()){
            case "Nhỏ":
                holder.note_desc.setTextSize(15);
                break;
            case "Bình thường":
                holder.note_desc.setTextSize(20);
                break;
            case "Lớn":
                holder.note_desc.setTextSize(25);
                break;
            case "Cực đại":
                holder.note_desc.setTextSize(30);
                break;
        }
        switch (note.getFontStyle()){
            case "Mặc định":
                Typeface typeface = Typeface.DEFAULT;
                holder.note_desc.setTypeface(typeface);
                break;
            case "Rokkit":
                Typeface typeface1 = context.getResources().getFont(R.font.rokkit);
                holder.note_desc.setTypeface(typeface1);
                break;
            case "Librebodoni":
                Typeface typeface2 = context.getResources().getFont(R.font.librebodoni);
                holder.note_desc.setTypeface(typeface2);
                break;
            case "RobotoSlab":
                Typeface typeface3 = context.getResources().getFont(R.font.robotoslab);
                holder.note_desc.setTypeface(typeface3);
                break;
            case "Texturina":
                Typeface typeface4 = context.getResources().getFont(R.font.texturina);
                holder.note_desc.setTypeface(typeface4);
                break;
        }
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

        if(!note.getImg_path().equals("")) {
            holder.note_img.setImageBitmap(BitmapFactory.decodeFile(note.getImg_path()));
            holder.img_field.setVisibility(View.VISIBLE);
        }
        else {
            holder.img_field.setVisibility(View.GONE);
        }
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

        LinearLayout layout_root, img_field;
        TextView note_title, note_desc, time_create;
        ImageView pin_note, has_label, has_pass;
        ShapeableImageView note_img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_root = itemView.findViewById(R.id.layout_root);
            img_field = itemView.findViewById(R.id.img_field);
            note_title = itemView.findViewById(R.id.note_title);
            note_desc = itemView.findViewById(R.id.note_desc);
            time_create = itemView.findViewById(R.id.time_create);
            pin_note = itemView.findViewById(R.id.pin_note);
            has_label = itemView.findViewById(R.id.has_label);
            has_pass = itemView.findViewById(R.id.has_pass);
            note_img = itemView.findViewById(R.id.note_img);
        }
    }
}