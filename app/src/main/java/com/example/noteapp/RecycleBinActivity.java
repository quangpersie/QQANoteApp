package com.example.noteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class RecycleBinActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    RecyclerView view_recycle_bin;
    private NoteAdapter noteAdapter;
    private Notes selectedNote;
    private List<Notes> notes = new ArrayList<>();
    private RoomDB database;
    TextView empty_bin;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userMail = user.getEmail();
    LinearLayout backToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_bin);

        view_recycle_bin = findViewById(R.id.view_recycle_bin);
        empty_bin = findViewById(R.id.empty_bin);
        backToMain = findViewById(R.id.backToMain);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecycleBinActivity.this, MainActivity.class));
                finish();
            }
        });

        database = RoomDB.getInstance(this);
        notes = database.noteDAO().getAllDeletedNote(userMail);

        updateNotify();
        ListLayout(notes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNotify();
    }

    private void ListLayout(List<Notes> notes) {
        noteAdapter = new NoteAdapter(RecycleBinActivity.this, notes, noteClickListener);

        LinearLayoutManager manager = new LinearLayoutManager(RecycleBinActivity.this,
                LinearLayoutManager.VERTICAL,false);

        view_recycle_bin.setHasFixedSize(true);
        view_recycle_bin.setLayoutManager(manager);
        view_recycle_bin.setAdapter(noteAdapter);
    }

    private final NoteClickListener noteClickListener = new NoteClickListener() {
        @Override
        public void onClick(Notes notes) {

        }

        @Override
        public void onLongClick(Notes notes, LinearLayout layout) {
            selectedNote = new Notes();
            selectedNote = notes;
            showPopUp(layout);
        }
    };

    private void showPopUp(LinearLayout layout) {
        PopupMenu popupMenu = new PopupMenu(this,layout);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu_rbin);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.recover_note:
                database.noteDAO().recoverNoteDel(selectedNote.getId());
                notes.clear();
                notes.addAll(database.noteDAO().getAllDeletedNote(userMail));
                noteAdapter.notifyDataSetChanged();
                updateNotify();
                return true;
            case R.id.deleteP_note:
                database.noteDAO().delete(selectedNote);
                notes.clear();
                notes.addAll(database.noteDAO().getAllDeletedNote(userMail));
                noteAdapter.notifyDataSetChanged();
                updateNotify();
                return true;
            default:
                return false;
        }
    }

    private void updateNotify() {
        if(database.noteDAO().getAllDeletedNote(userMail).size() != 0) {
            empty_bin.setVisibility(View.GONE);
        }
        else {
            empty_bin.setVisibility(View.VISIBLE);
        }
    }
}