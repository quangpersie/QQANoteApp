package com.example.noteapp;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Type;

public class FontSettingActivity extends AppCompatActivity {

    AutoCompleteTextView autoCompleteTextViewSize, autoCompleteTextViewStyle;
    ArrayAdapter<String> adapterSize;
    ArrayAdapter<String> adapterStyle;
    TextView editedTest;
    String[] fontSize = {"Nhỏ", "Bình thường", "Lớn", "Cực đại"};
    String[] fontStyle = {"Mặc định", "Rokkit", "Librebodoni", "RobotoSlab", "Texturina"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.font_setting_layout);
        super.onCreate(savedInstanceState);
        initView();

        adapterSize = new ArrayAdapter<String>(this, R.layout.list_dropdown_menu_item, fontSize);
        adapterStyle = new ArrayAdapter<String>(this, R.layout.list_dropdown_menu_item, fontStyle);
        autoCompleteTextViewSize.setAdapter(adapterSize);
        autoCompleteTextViewStyle.setAdapter(adapterStyle);


        autoCompleteTextViewSize.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String fontSize = adapterView.getItemAtPosition(i).toString();
                switch (fontSize){
                    case "Nhỏ":
                        editedTest.setTextSize(15);
                        break;
                    case "Bình thường":
                        editedTest.setTextSize(20);
                        break;
                    case "Lớn":
                        editedTest.setTextSize(25);
                        break;
                    case "Cực đại":
                        editedTest.setTextSize(30);
                        break;
                }
            }
        });

        autoCompleteTextViewStyle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String fontStyle = adapterView.getItemAtPosition(i).toString();
                switch (fontStyle){
                    case "Mặc định":
                        Typeface typeface = Typeface.DEFAULT;
                        editedTest.setTypeface(typeface);
                        break;
                    case "Rokkit":
                        Typeface typeface1 = getResources().getFont(R.font.rokkit);
                        editedTest.setTypeface(typeface1);
                        break;
                    case "Librebodoni":
                        Typeface typeface2 = getResources().getFont(R.font.librebodoni);
                        editedTest.setTypeface(typeface2);
                        break;
                    case "RobotoSlab":
                        Typeface typeface3 = getResources().getFont(R.font.robotoslab);
                        editedTest.setTypeface(typeface3);
                        break;
                    case "Texturina":
                        Typeface typeface4 = getResources().getFont(R.font.texturina);
                        editedTest.setTypeface(typeface4);
                        break;
                }

            }
        });
    }

    private void initView() {
        autoCompleteTextViewSize = findViewById(R.id.auto_complete_fontSize);
        autoCompleteTextViewStyle = findViewById(R.id.auto_complete_fontStyle);
        editedTest = findViewById(R.id.editedTest);
        autoCompleteTextViewSize.setInputType(InputType.TYPE_NULL);
        autoCompleteTextViewStyle.setInputType(InputType.TYPE_NULL);
    }
}
