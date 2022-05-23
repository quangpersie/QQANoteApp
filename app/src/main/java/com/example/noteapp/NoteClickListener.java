package com.example.noteapp;

import android.widget.LinearLayout;

public interface NoteClickListener {
    void onClick(Notes notes);
    void onLongClick(Notes notes, LinearLayout layout);
}
