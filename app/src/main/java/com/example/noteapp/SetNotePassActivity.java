package com.example.noteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Set;

public class SetNotePassActivity extends AppCompatActivity {

    LinearLayout set_field, change_field;
    Button skip_pass_note, confirm_pass_note, cancel_pass_mode;
    EditText set_pass_note, set_pass_note2, current_pass_note, change_pass_note, change_pass_note2;
    RoomDB db;
    DatabaseReference noteDbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_note_pass);

        set_field = findViewById(R.id.set_field);
        change_field = findViewById(R.id.change_field);
        skip_pass_note = findViewById(R.id.skip_pass_note);
        confirm_pass_note = findViewById(R.id.confirm_pass_note);
        set_pass_note = findViewById(R.id.set_pass_note);
        set_pass_note2 = findViewById(R.id.set_pass_note2);
        current_pass_note = findViewById(R.id.current_pass_note);
        change_pass_note = findViewById(R.id.change_pass_note);
        change_pass_note2 = findViewById(R.id.change_pass_note2);
        cancel_pass_mode = findViewById(R.id.cancel_pass_mode);
        db = RoomDB.getInstance(this);
        noteDbRef = FirebaseDatabase.getInstance().getReference().child("Notes");

        int idNote = (int) getIntent().getSerializableExtra("idNote");
        String passUserCur = db.noteDAO().getNoteById(idNote).getPassword();
        if(passUserCur.equals("")) {
            set_field.setVisibility(View.VISIBLE);
            change_field.setVisibility(View.GONE);
        }
        else {
            set_field.setVisibility(View.GONE);
            change_field.setVisibility(View.VISIBLE);
        }

        skip_pass_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        confirm_pass_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(set_field.getVisibility() == View.VISIBLE) {
                    String pass = set_pass_note.getText().toString();
                    String pass2 = set_pass_note2.getText().toString();
                    if(pass.equals("")) {
                        set_pass_note.setError("Vui lòng nhập mật khẩu");
                        set_pass_note.requestFocus();
                    }
                    else if(pass.equals(" ")) {
                        set_pass_note.setError("Mật khẩu không được chứa khoảng trắng");
                        set_pass_note.requestFocus();
                    }
                    else if(!pass2.equals(pass)) {
                        set_pass_note2.setError("Mật khẩu xác nhận không trùng khớp");
                        set_pass_note2.requestFocus();
                    }
                    else {
                        db.noteDAO().updatePassNote(idNote,pass);
                        Notes note = db.noteDAO().getNoteById(idNote);
                        note.setPassword(pass);
                        //noteDbRef.child(user.getUid()).child("password").setValue(pass);
                        noteDbRef.push().setValue(note);
                        Toast.makeText(SetNotePassActivity.this, "Đặt mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else {
                    String curPass = current_pass_note.getText().toString();
                    String newPass = change_pass_note.getText().toString();
                    String newPass2 = change_pass_note2.getText().toString();
                    if(curPass.equals("")) {
                        current_pass_note.setError("Vui lòng nhập mật khẩu hiện tại");
                        current_pass_note.requestFocus();
                    }
                    else if(newPass.equals("")) {
                        change_pass_note.setError("Vui lòng nhập mật khẩu mới");
                        change_pass_note.requestFocus();
                    }
                    else if(newPass2.equals("")) {
                        change_pass_note2.setError("Vui lòng nhập mật khẩu xác nhận");
                        change_pass_note2.requestFocus();
                    }
                    else if(curPass.equals(" ")) {
                        current_pass_note.setError("Mật khẩu không được chứa khoảng trắng");
                        current_pass_note.requestFocus();
                    }
                    else if(newPass.equals(" ")) {
                        change_pass_note.setError("Mật khẩu không được chứa khoảng trắng");
                        change_pass_note.requestFocus();
                    }
                    else if(newPass2.equals(" ")) {
                        change_pass_note2.setError("Mật khẩu không được chứa khoảng trắng");
                        change_pass_note2.requestFocus();
                    }
                    else if(!curPass.equals(db.noteDAO().getNoteById(idNote).getPassword())) {
                        current_pass_note.setError("Mật khẩu hiện tại không đúng");
                        current_pass_note.requestFocus();
                    }
                    else if(!newPass2.equals(newPass)) {
                        change_pass_note2.setError("Mật khẩu xác nhận không trùng khớp");
                        change_pass_note2.requestFocus();
                    }
                    else {
                        db.noteDAO().updatePassNote(idNote,newPass);
                        Notes note = db.noteDAO().getNoteById(idNote);
                        note.setPassword(newPass);
                        //noteDbRef.child(user.getUid()).child("password").setValue(newPass);
                        noteDbRef.push().setValue(note);
                        Toast.makeText(SetNotePassActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });

        cancel_pass_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.noteDAO().updatePassNote(idNote,"");
                Notes note = db.noteDAO().getNoteById(idNote);
                note.setPassword("");
                //noteDbRef.child(user.getUid()).child("password").setValue("");
                noteDbRef.push().setValue(note);
                Toast.makeText(SetNotePassActivity.this, "Xóa mật khẩu thành công", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}