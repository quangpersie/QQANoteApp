package com.example.noteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecycleBinActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener,
        AdapterView.OnItemSelectedListener, View.OnClickListener {

    RecyclerView view_recycle_bin;
    private NoteAdapter noteAdapter;
    private Notes selectedNote;
    private List<Notes> notes = new ArrayList<>();
    private RoomDB database;
    TextView empty_bin;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userMail = user.getEmail();
    LinearLayout backToMain;
    DatabaseReference noteDbRef = FirebaseDatabase.getInstance().getReference().child("Notes");
    Spinner spinner_timeDel;
    Button btn_set_delTime, btn_empty_bin;
    List<String> listTimeDel = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_bin);

        view_recycle_bin = findViewById(R.id.view_recycle_bin);
        empty_bin = findViewById(R.id.empty_bin);
        backToMain = findViewById(R.id.backToMain);
        spinner_timeDel = findViewById(R.id.spinner_timeDel);
        btn_set_delTime = findViewById(R.id.btn_set_delTime);
        btn_empty_bin = findViewById(R.id.btn_empty_bin);

        backToMain.setOnClickListener(view -> finish());

        btn_empty_bin.setOnClickListener(view -> {
            database.noteDAO().deleteAllDelNote(userMail);
            notes.clear();
            noteAdapter.notifyDataSetChanged();
            updateNotify();
        });

        btn_set_delTime.setOnClickListener(this);

        database = RoomDB.getInstance(this);
//        notes = database.noteDAO().getAllDeletedNote(userMail);
        notes = database.noteDAO().showNoteDelInOrder(userMail);
        updateNotify();
        ListLayout(notes);

        if(listTimeDel.size() == 0) {
//            listTimeDel.add("Hẹn xóa");
            listTimeDel.add("1 phút");
            listTimeDel.add("1 ngày");
            listTimeDel.add("7 ngày");
        }

        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(RecycleBinActivity.this,
                android.R.layout.simple_list_item_1, listTimeDel);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_timeDel.setAdapter(mAdapter);
        spinner_timeDel.setOnItemSelectedListener(RecycleBinActivity.this);

        switch (database.defaultDAO().getSettingById(1).getDelete_default()) {
            case "1 phút":
                spinner_timeDel.setSelection(0);
                break;
            case "1 ngày":
                spinner_timeDel.setSelection(1);
                break;
            case "7 ngày":
                spinner_timeDel.setSelection(2);
                break;
        }
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
                database.noteDAO().updateNoteOrderDel(selectedNote.getId(), 0);

                database.noteDAO().recoverNoteDel(selectedNote.getId());
                selectedNote.setDelete(false);
                noteDbRef.push().setValue(selectedNote);
                notes.clear();
                notes.addAll(database.noteDAO().showNoteDelInOrder(userMail));
                noteAdapter.notifyDataSetChanged();
                updateNotify();
                List<Notes> ln = database.noteDAO().getAllDeletedNote(userMail);
                for(Notes l:ln){
                    Log.e("ORDER",l.getTitle()+", "+l.getOrderNoteDel());
                }
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getItemAtPosition(i).toString()) {
            case "1 phút":
                database.defaultDAO().updateTimeDelAuto("1 phút");
                break;
            case "1 ngày":
                database.defaultDAO().updateTimeDelAuto("1 ngày");
                break;
            case "7 ngày":
                database.defaultDAO().updateTimeDelAuto("7 ngày");
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_set_delTime:

        }
    }

    /*private int getBroadcastCode() {
        return (int) new Date().getTime();
    }*/
}