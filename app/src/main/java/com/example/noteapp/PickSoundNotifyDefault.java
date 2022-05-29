package com.example.noteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class PickSoundNotifyDefault extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinner_sound;
    Button save_sound, back_main;
    List<String> listSound = new ArrayList<>();
    RoomDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_sound_notify_default);

        spinner_sound = findViewById(R.id.spinner_sound);
        save_sound = findViewById(R.id.save_sound);
        back_main = findViewById(R.id.back_main);
        db = RoomDB.getInstance(this);

        if(listSound.size() == 0) {
            listSound.add("Default");
            listSound.add("MTP");
            listSound.add("MCK");
            listSound.add("TNS");
        }

        spinner_sound = findViewById(R.id.spinner_sound);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(PickSoundNotifyDefault.this,
                android.R.layout.simple_list_item_1, listSound);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_sound.setAdapter(mAdapter);
        spinner_sound.setOnItemSelectedListener(PickSoundNotifyDefault.this);

        try {
            switch (db.defaultDAO().getSettingById(1).getSound_default()) {
                case "MTP":
                    spinner_sound.setSelection(1);
                    break;
                case "MCK":
                    spinner_sound.setSelection(2);
                    break;
                case "TNS":
                    spinner_sound.setSelection(3);
                    break;
                default:
                    spinner_sound.setSelection(0);
            }
        }
        catch (Exception e) {

        }

        save_sound.setOnClickListener(view -> {
            db.defaultDAO().updateDefaultSound(spinner_sound.getSelectedItem().toString());
            Toast.makeText(this, "Cài âm thanh mặc định thành công", Toast.LENGTH_SHORT).show();
        });
        back_main.setOnClickListener(view -> finish());
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}