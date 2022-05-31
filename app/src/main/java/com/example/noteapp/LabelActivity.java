package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.SnapshotHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LabelActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private RecyclerView recyclerView;
    private List<Label> labels = new ArrayList<>();
    private RoomDB database;
    private LabelAdapter labelAdapter;
    private Label labelSelected;
    Button delete_allLabel, create_newLabel, save_check, confirm_create;
    EditText edt_newName;
    LinearLayout zone_create;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userMail = user.getEmail();
    DatabaseReference noteDbRef, labelDbRed;
    int idNoteCopy;
    List<String> mKeys = new ArrayList<>();
    TextView notify_label_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);

        recyclerView = findViewById(R.id.rview_label);
        delete_allLabel = findViewById(R.id.delete_allLabel);
        create_newLabel = findViewById(R.id.create_newLabel);
        edt_newName = findViewById(R.id.edt_newName);
        save_check = findViewById(R.id.save_check);
        confirm_create = findViewById(R.id.confirm_create);
        zone_create = findViewById(R.id.zone_create);
        notify_label_empty = findViewById(R.id.notify_label_empty);
        noteDbRef = FirebaseDatabase.getInstance().getReference().child("Notes");
        labelDbRed = FirebaseDatabase.getInstance().getReference().child("Labels");
        Label label1 = new Label();
        label1.setName("Work");
        Label label2 = new Label();
        label2.setName("Personal");
        Label label3 = new Label();
        label3.setName("Family");

        database = RoomDB.getInstance(this);
        if(database.labelDAO().getCountLabel() == 0) {
            database.labelDAO().insert(label1);
            database.labelDAO().insert(label2);
            database.labelDAO().insert(label3);
        }

        labels.addAll(database.labelDAO().getAllLabel());
        ListLayout(labels);

        delete_allLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(LabelActivity.this);
                alert.setTitle("Bạn muốn xóa tất cả nhãn?");
                alert.setMessage("Nếu bạn xóa mà chưa tạo mới thì sau đó hệ thống sẽ tự tạo 3 nhãn ban đầu để bạn quản lý ghi chú của mình");

                alert.setPositiveButton("Tiếp tục", (dialogInterface, i) -> {
                    database.labelDAO().deleteAllLabel();
                    updateAdapter();
                    updateNotify();
                });
                alert.setNegativeButton("Hủy bỏ", (dialog, whichButton) -> {

                });
                alert.show();
            }
        });

        create_newLabel.setOnClickListener(view -> {
            if(zone_create.getVisibility() == View.GONE) {
                zone_create.setVisibility(View.VISIBLE);
                zone_create.requestFocus();
            }
            else {
                zone_create.setVisibility(View.GONE);
            }
        });

        confirm_create.setOnClickListener(view -> {
            Label label_new = new Label();
            String input_name = edt_newName.getText().toString();
            String nameAvailable = "";
            for(Label label:labels) {
                nameAvailable += label.getName().toLowerCase();
            }
            if(input_name.equals("")) {
                edt_newName.setError("Vui lòng nhập tên cho nhãn mới");
                edt_newName.requestFocus();
            }
            else if(input_name.equals("")) {
                edt_newName.setError("Tên nhãn không được chứa khoảng trắng");
                edt_newName.requestFocus();
            }
            else if(nameAvailable.contains(input_name.toLowerCase())) {
                edt_newName.setError("Tên nhãn đã tồn tại, vui lòng đặt nhãn với tên khác");
                edt_newName.requestFocus();
            }
            else {
                label_new.setName(input_name);
                database.labelDAO().insert(label_new);
                updateAdapter();
                zone_create.setVisibility(View.GONE);
                edt_newName.setText("");
            }
        });

        int idNote = (int) getIntent().getSerializableExtra("id_note");
        idNoteCopy = idNote;
        labelAdapter.getIdNote(idNote);
//        Log.e("id - LabelActivity",""+idNote);

        save_check.setOnClickListener(view -> {
            for(Label label: labels) {
                Log.e("CHK",""+label.isChecked());
                if(label.isChecked()) {
                    database.labelDAO().updateCheck(label.getId(),true);
                    String nameUpdate = database.noteDAO().getNoteById(idNote).getLabel().toLowerCase();
                    String labelName = label.getName().toLowerCase();
                    if(!nameUpdate.contains(labelName)) {
                        nameUpdate = nameUpdate + labelName;
                    }
                    database.noteDAO().updateCheckLabel(idNote,nameUpdate);
                }
                else {
                    database.labelDAO().updateCheck(label.getId(),false);

                    String nameLabel = label.getName().toLowerCase();
                    String allLabelOfId = database.noteDAO().getNoteById(idNote).getLabel().toLowerCase();

                    if(allLabelOfId.contains(nameLabel)) {
                        String nameUpdate = allLabelOfId.replace(nameLabel,"");
                        database.noteDAO().updateCheckLabel(idNote, nameUpdate);
                    }
                }
                labelDbRed.push().setValue(label);
            }
            finish();
            updateAdapter();
            String labelStr = database.noteDAO().getNoteById(idNote).getLabel();

            for(Label label:labels) {
                if (labelStr.contains(label.getName().toLowerCase())) {
                    label.setChecked(true);
                }
                else {
                    label.setChecked(false);
                }
            }
            Notes note = database.noteDAO().getNoteById(idNote);
            note.setLabel(labelStr);
            noteDbRef.push().setValue(note);

            labelAdapter.notifyDataSetChanged();
        });

    }

    private void updateNotify() {
        if(database.noteDAO().getAllDeletedNote(userMail).size() != 0) {
            notify_label_empty.setVisibility(View.GONE);
        }
        else {
            notify_label_empty.setVisibility(View.VISIBLE);
        }
    }

    private void getData()
    {
        noteDbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds: snapshot.getChildren())
                    {
                        mKeys.add(ds.getKey());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }
    private void ListLayout(List<Label> labels) {
        labelAdapter = new LabelAdapter(LabelActivity.this, labels, labelClickListener);

        LinearLayoutManager manager = new LinearLayoutManager(LabelActivity.this,
                LinearLayoutManager.VERTICAL,false);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(labelAdapter);
    }

    private final LabelClickListener labelClickListener = new LabelClickListener() {
        @Override
        public void onLongClick(Label label, LinearLayout layout) {
            labelSelected = new Label();
            labelSelected = label;
            showPopUp(layout);
        }
    };

    private void showPopUp(LinearLayout layout) {
        PopupMenu popupMenu = new PopupMenu(this,layout);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_label);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rename_label:
                AlertDialog.Builder alert = new AlertDialog.Builder(LabelActivity.this);
                final EditText edt = new EditText(this);
                edt.setPadding(32,32,32,32);
                alert.setTitle("Đổi tên nhãn");
                alert.setMessage("Nhập tên nhãn mới để đổi");
                alert.setView(edt);

                alert.setPositiveButton("Đổi tên", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newName = edt.getText().toString();
                        boolean flag = false;
                        for(Label label:database.labelDAO().getAllLabel()) {
                            if (label.getName().toLowerCase().equals(newName.toLowerCase())) {
                                flag = true;
                                break;
                            }
                        }
                        if(flag) {
                            Toast.makeText(LabelActivity.this, "Không được đặt tên trùng với nhãn đã có", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            database.labelDAO().updateName(labelSelected.getId(),newName);
                            updateAdapter();
                        }
                    }
                });

                alert.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert.show();
                return true;
            case R.id.delete_label:
                database.labelDAO().deleteLabel(labelSelected);
                Log.e("idCopy",""+idNoteCopy);
                String nameLabel = labelSelected.getName().toLowerCase();
                String allLabelOfId = database.noteDAO().getNoteById(idNoteCopy).getLabel().toLowerCase();

                if(allLabelOfId.contains(nameLabel)) {
                    String nameUpdate = allLabelOfId.replace(nameLabel,"");
                    database.noteDAO().updateCheckLabel(idNoteCopy, nameUpdate);
                }
                updateAdapter();
                return true;
            default:
                return false;
        }
    }

    private void updateAdapter() {
        labels.clear();
        labels.addAll(database.labelDAO().getAllLabel());
        labelAdapter.notifyDataSetChanged();
    }
}