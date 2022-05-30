package com.example.noteapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Type;

public class FontSettingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Spinner autoCompleteTextViewSize, autoCompleteTextViewStyle;
    TextView editedTest;
    Button btnConfirmChange;
    String[] fontSizeAdapter = {"Nhỏ", "Bình thường", "Lớn", "Rất lớn", "Cực đại"};
    String[] fontStyleAdapter = {"Mặc định", "Rokkit", "Librebodoni", "RobotoSlab", "Texturina"};
    RoomDB db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.font_setting_layout);
        super.onCreate(savedInstanceState);

        initView();
        db = RoomDB.getInstance(this);
        autoCompleteTextViewSize = findViewById(R.id.auto_complete_fontSize);
        autoCompleteTextViewStyle = findViewById(R.id.auto_complete_fontStyle);

        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<String>(FontSettingActivity.this,
                android.R.layout.simple_list_item_1, fontSizeAdapter);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteTextViewSize.setAdapter(sizeAdapter);

        switch (db.defaultDAO().getSettingById(1).getSize_default()) {
            case "Nhỏ":
                autoCompleteTextViewSize.setSelection(0);
                break;
            case "Lớn":
                autoCompleteTextViewSize.setSelection(2);
                break;
            case "Rất lớn":
                autoCompleteTextViewSize.setSelection(3);
                break;
            case "Cực đại":
                autoCompleteTextViewSize.setSelection(4);
                break;
            default:
                autoCompleteTextViewSize.setSelection(1);
        }

        ArrayAdapter<String> styleAdapter = new ArrayAdapter<String>(FontSettingActivity.this,
                android.R.layout.simple_list_item_1, fontStyleAdapter);
        styleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteTextViewStyle.setAdapter(styleAdapter);

        switch (db.defaultDAO().getSettingById(1).getFont_default()){
            case "Rokkit":
                autoCompleteTextViewStyle.setSelection(1);
                break;
            case "Librebodoni":
                autoCompleteTextViewStyle.setSelection(2);
                break;
            case "RobotoSlab":
                autoCompleteTextViewStyle.setSelection(3);
                break;
            case "Texturina":
                autoCompleteTextViewStyle.setSelection(4);
                break;
            default:
                autoCompleteTextViewStyle.setSelection(0);
        }

        autoCompleteTextViewSize.setOnItemSelectedListener(FontSettingActivity.this);
        autoCompleteTextViewStyle.setOnItemSelectedListener(FontSettingActivity.this);
        btnConfirmChange.setOnClickListener(view -> {
            finish();
        });
    }

    private void initView() {
        autoCompleteTextViewSize = findViewById(R.id.auto_complete_fontSize);
        autoCompleteTextViewStyle = findViewById(R.id.auto_complete_fontStyle);
        editedTest = findViewById(R.id.editedTest);
        btnConfirmChange = findViewById(R.id.btn_confirm_change);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        Toast.makeText(this, ""+view.getId(), Toast.LENGTH_SHORT).show();
        switch (adapterView.getId()) {
            case R.id.auto_complete_fontSize:
                switch (adapterView.getItemAtPosition(i).toString()) {
                    case "Nhỏ":
                        Toast.makeText(this, "Nhỏ", Toast.LENGTH_SHORT).show();
                        editedTest.setTextSize(16);
                        db.defaultDAO().updateDefaultSize("Nhỏ");
                        break;
                    case "Lớn":
                        editedTest.setTextSize(22);
                        db.defaultDAO().updateDefaultSize("Lớn");
                        break;
                    case "Rất lớn":
                        editedTest.setTextSize(26);
                        db.defaultDAO().updateDefaultSize("Rất lớn");
                        break;
                    case "Cực đại":
                        editedTest.setTextSize(30);
                        db.defaultDAO().updateDefaultSize("Cực đại");
                        break;
                    default:
                        editedTest.setTextSize(18);
                        db.defaultDAO().updateDefaultSize("Bình thường");
                }
                break;
            case R.id.auto_complete_fontStyle:
                switch (adapterView.getItemAtPosition(i).toString()){
                    case "Rokkit":
                        editedTest.setTypeface(getResources().getFont(R.font.rokkit));
                        db.defaultDAO().updateDefaultFont("Rokkit");
                        break;
                    case "Librebodoni":
                        editedTest.setTypeface(getResources().getFont(R.font.librebodoni));
                        db.defaultDAO().updateDefaultFont("Librebodoni");
                        break;
                    case "RobotoSlab":
                        editedTest.setTypeface(getResources().getFont(R.font.robotoslab));
                        db.defaultDAO().updateDefaultFont("RobotoSlab");
                        break;
                    case "Texturina":
                        editedTest.setTypeface(getResources().getFont(R.font.texturina));
                        db.defaultDAO().updateDefaultFont("Texturina");
                        break;
                    default:
                        editedTest.setTypeface(Typeface.DEFAULT);
                        db.defaultDAO().updateDefaultFont("Mặc định");
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
