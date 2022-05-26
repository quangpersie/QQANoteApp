package com.example.noteapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.MyViewHolder> {
    private int id;
    private Context context;
    private List<Label> listLabel;
    private LabelClickListener listener;
    RoomDB database;

    public LabelAdapter(Context context, List<Label> listNode, LabelClickListener listener) {
        this.context = context;
        this.listLabel = listNode;
        this.listener = listener;
        database = RoomDB.getInstance(context);
    }

    public void getIdNote(int id) {
        this.id = id;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.label_row,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Label label = listLabel.get(position);
        holder.label_name.setText(label.getName());
        String name = holder.label_name.getText().toString().toLowerCase();
//        Log.e("-----------id lay duoc---------",""+id);
//        Log.e("-Name:",name);

        String labelList = database.noteDAO().getNoteById(id).getLabel();
        if(labelList.toLowerCase().contains(name)) {
            label.setChecked(true);
            holder.label_check.setChecked(true);
        }
        else {
            label.setChecked(false);
            holder.label_check.setChecked(false);
        }
//        Log.e("END","-----------------\n");

        holder.label_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    holder.label_check.setChecked(true);
                    label.setChecked(true);
//                    Toast.makeText(context, "checked: "+label.getName(), Toast.LENGTH_SHORT).show();
                }
                else {
                    holder.label_check.setChecked(false);
                    label.setChecked(false);
//                    Toast.makeText(context, "unchecked: "+label.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.linear_label.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(listLabel.get(holder.getAdapterPosition()), holder.linear_label);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listLabel.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        LinearLayout linear_label;
        CheckBox label_check;
        TextView label_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            linear_label = itemView.findViewById(R.id.linear_label);
            label_check = itemView.findViewById(R.id.label_check);
            label_name = itemView.findViewById(R.id.label_name);
        }
    }
}
