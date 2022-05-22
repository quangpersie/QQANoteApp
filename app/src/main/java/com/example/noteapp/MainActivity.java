package com.example.noteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private MaterialButton add_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add_note = findViewById(R.id.add_note);

        add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CreateNoteActivity.class));
            }
        });


//        RecyclerView recyclerView = findViewById(R.id.recyclerview);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        NoteAdapter myAdapter = new NoteAdapter(getApplicationContext(),notesList);
//        recyclerView.setAdapter(myAdapter);

        /*notesList.addChangeListener(new RealmChangeListener<RealmResults<NoteÌno>>() {
            @Override
            public void onChange(RealmResults<NoteInfo> notes) {
                myAdapter.notifyDataSetChanged();
            }
        });*/
    }
}