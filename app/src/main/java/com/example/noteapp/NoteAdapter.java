package com.example.noteapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
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
import java.util.List;

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

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Notes note = listNode.get(position);

        holder.note_title.setText(note.getTitle());
        holder.note_title.setSelected(true);
        holder.note_desc.setText(note.getContent());

        switch (note.getFontSize()){
            case "Nhỏ":
                holder.note_desc.setTextSize(14);
                break;
            case "Lớn":
                holder.note_desc.setTextSize(22);
                break;
            case "Rất lớn":
                holder.note_desc.setTextSize(26);
                break;
            case "Cực đại":
                holder.note_desc.setTextSize(30);
                break;
            default:
                holder.note_desc.setTextSize(18);
        }

        switch (note.getFontStyle()){
            case "Rokkit":
                holder.note_desc.setTypeface(context.getResources().getFont(R.font.rokkit));
                break;
            case "Librebodoni":
                holder.note_desc.setTypeface(context.getResources().getFont(R.font.librebodoni));
                break;
            case "RobotoSlab":
                holder.note_desc.setTypeface(context.getResources().getFont(R.font.robotoslab));
                break;
            case "Texturina":
                holder.note_desc.setTypeface(context.getResources().getFont(R.font.texturina));
                break;
            default:
                holder.note_desc.setTypeface(Typeface.DEFAULT);
        }

        switch (note.getAlign()){
            case "right":
                holder.note_desc.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                break;
            case "center":
                holder.note_desc.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                break;
            case "justify":
                holder.note_desc.setTextAlignment(View.TEXT_ALIGNMENT_INHERIT);
                break;
            default:
                holder.note_desc.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }

        switch (note.getColor_text()){
            case "red":
                holder.note_desc.setTextColor(context.getResources().getColor(R.color.red_text));
                break;
            case "blue":
                holder.note_desc.setTextColor(context.getResources().getColor(R.color.blue_text));
                break;
            case "green":
                holder.note_desc.setTextColor(context.getResources().getColor(R.color.green_text));
                break;
            case "purple":
                holder.note_desc.setTextColor(context.getResources().getColor(R.color.purple_text));
                break;
            default:
                holder.note_desc.setTextColor(context.getResources().getColor(R.color.black));
        }

        if(note.getOrderNoteDel() != 0) {
            holder.time_create.setText(note.getDate_delete());
        }
        else {
            holder.time_create.setText(note.getDate_create());
        }
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

        if(!note.getTime_remind().equals("")) {
            holder.has_remind.setVisibility(View.VISIBLE);
        }
        else {
            holder.has_remind.setVisibility(View.GONE);
        }

        switch (note.getColor_code()) {
            case "red":
                holder.layout_root.setBackgroundResource(R.drawable.mbg_note_red);
                break;
            case "yellow":
                holder.layout_root.setBackgroundResource(R.drawable.mbg_note_yellow);
                break;
            case "blue":
                holder.layout_root.setBackgroundResource(R.drawable.mbg_note_blue);
                break;
            case "green":
                holder.layout_root.setBackgroundResource(R.drawable.mbg_note_green);
                break;
            default:
                holder.layout_root.setBackgroundResource(R.drawable.row_style_bg);
        }

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
        ImageView pin_note, has_label, has_pass, has_remind;
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
            has_remind = itemView.findViewById(R.id.has_remind);
        }
    }
}