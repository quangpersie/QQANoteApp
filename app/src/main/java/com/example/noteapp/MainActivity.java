package com.example.noteapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.SearchView;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private Notes selectedNote;
    private FloatingActionButton add_note;
    private TextView empty_notify;
    private SearchView search_bar;
    private ImageView list_display, grid_display;
    private List<Notes> notes = new ArrayList<>();
    private RoomDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerview);
        add_note = findViewById(R.id.add_note);
        empty_notify = findViewById(R.id.empty_notify);
        search_bar = findViewById(R.id.search_bar);
        list_display = findViewById(R.id.list_display);
        grid_display = findViewById(R.id.grid_display);

        database = RoomDB.getInstance(this);
        notes = database.noteDAO().getAll();

        GridLayout(notes);

        if(database.noteDAO().getCount() > 0) {
            empty_notify.setVisibility(View.GONE);
        }

        add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                startActivityForResult(intent, 10);
            }
        });

        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterSearch(s);
                if(s.equals("")) {
                    empty_notify.setText("Chưa có ghi chú nào\n Bấm vào dấu cộng để thêm ghi chú mới");
                    empty_notify.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                return false;
            }
        });

        DividerItemDecoration divider = new DividerItemDecoration(MainActivity.this,
                LinearLayoutManager.VERTICAL);

        list_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteAdapter = new NoteAdapter(MainActivity.this, notes, noteClickListener);

                LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this,
                        LinearLayoutManager.VERTICAL,false);

                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(manager);
                if(recyclerView.getItemDecorationCount() == 0) {
                    recyclerView.addItemDecoration(divider);
                }
                recyclerView.setAdapter(noteAdapter);
            }
        });

        grid_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GridLayout(notes);
                recyclerView.removeItemDecoration(divider);
            }
        });
    }

    private void filterSearch(String s) {
        List<Notes> containList = new ArrayList<>();
        for(Notes note:notes) {
            if(note.getTitle().toLowerCase(Locale.ROOT).contains(s.toLowerCase())
                    || note.getContent().toLowerCase(Locale.ROOT).contains(s.toLowerCase())) {
                containList.add(note);
            }
        }
        noteAdapter.filterList(containList);
        if(containList.size() == 0) {
            empty_notify.setVisibility(View.VISIBLE);
            empty_notify.setText("Không tìm thấy");
        }
        else {
            empty_notify.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 10) {
            if(resultCode == Activity.RESULT_OK) {
                Notes new_note = (Notes) data.getSerializableExtra("note");
                database.noteDAO().insert(new_note);
                notes.clear();
                notes.addAll(database.noteDAO().getAll());
                noteAdapter.notifyDataSetChanged();
            }
        }
        else if(requestCode == 11) {
            if(resultCode == Activity.RESULT_OK) {
                Notes new_note = (Notes) data.getSerializableExtra("note");
                database.noteDAO().update(new_note.getId(), new_note.getTitle(), new_note.getContent());
                notes.clear();
                notes.addAll(database.noteDAO().getAll());
                noteAdapter.notifyDataSetChanged();
            }
        }
    }

    private void GridLayout(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        noteAdapter = new NoteAdapter(MainActivity.this, notes, noteClickListener);
        recyclerView.setAdapter(noteAdapter);
    }

    private final NoteClickListener noteClickListener = new NoteClickListener() {
        @Override
        public void onClick(Notes notes) {
            Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
            intent.putExtra("old_note", notes);
            intent.putExtra("date_create", notes.getDate_create());
            startActivityForResult(intent, 11);
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
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        boolean pinned = false;
        switch (item.getItemId()) {
            case R.id.pin_note:
                if(selectedNote.isPinned()) {
                    database.noteDAO().pin(selectedNote.getId(), false);
                    Toast.makeText(this, "Đã bỏ ghim", Toast.LENGTH_SHORT).show();
                }
                else {
                    database.noteDAO().pin(selectedNote.getId(), true);
                    Toast.makeText(this, "Đã ghim", Toast.LENGTH_SHORT).show();
                    pinned = true;
                }
                if(pinned) {
                    notes.clear();
                    notes.addAll(database.noteDAO().getAll());
                    for(Notes note:notes) {
                        if(note.isPinned()) {
                            notes.remove(note);
                            notes.add(0,note);
                            break;
                        }
                    }
                }
                notes.clear();
                notes.addAll(database.noteDAO().getAllForPin());
                noteAdapter.notifyDataSetChanged();
                return true;
            case R.id.delete_note:
                database.noteDAO().delete(selectedNote);
                notes.remove(selectedNote);
                noteAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Đã xóa ghi chú", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }
}