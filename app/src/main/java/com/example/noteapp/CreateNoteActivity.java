package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class CreateNoteActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextView day_create, time_display, day_display, remove_img;
    EditText note_title_detail, note_desc_detail;
    Button btn_save, btn_setTime, btn_setDay;
    Button pick_pink, pick_red, pick_yellow, pick_blue, pick_green;
    Notes note;
    ImageView remind_note, label_note, password_note, image_insert, image_note, pick_color_note_bg;
    LinearLayout layout_remind, setting_note_layout, image_field, color_bg_field;
    DatabaseReference noteDbRef;
    boolean isOldNote = false;
    String selectedImagePath;
    RoomDB db;
    int idToGetImg = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        mAuth = FirebaseAuth.getInstance();

        day_create = findViewById(R.id.day_create);
        note_title_detail = findViewById(R.id.note_title_detail);
        note_desc_detail = findViewById(R.id.note_desc_detail);
        btn_save = findViewById(R.id.btn_save);
        btn_setTime = findViewById(R.id.btn_setTime);
        btn_setDay = findViewById(R.id.btn_setDay);
        time_display = findViewById(R.id.time_display);
        day_display = findViewById(R.id.day_display);
        remind_note = findViewById(R.id.remind_note);
        layout_remind = findViewById(R.id.layout_remind);
        setting_note_layout = findViewById(R.id.setting_note_layout);
        label_note = findViewById(R.id.label_note);
        password_note = findViewById(R.id.password_note);
        image_insert = findViewById(R.id.image_insert);
        image_note = findViewById(R.id.image_note);
        remove_img = findViewById(R.id.remove_img);
        image_field = findViewById(R.id.image_field);
        pick_color_note_bg = findViewById(R.id.pick_color_note_bg);
        color_bg_field = findViewById(R.id.color_bg_field);

        pick_pink = findViewById(R.id.pick_pink);
        pick_red = findViewById(R.id.pick_red);
        pick_yellow = findViewById(R.id.pick_yellow);
        pick_blue = findViewById(R.id.pick_blue);
        pick_green = findViewById(R.id.pick_green);
        noteDbRef = FirebaseDatabase.getInstance().getReference().child("Notes");
        db = RoomDB.getInstance(this);

        selectedImagePath = "";
        String date = (String) getIntent().getSerializableExtra("date_create");
        int idNote = -17;
        if (getIntent().getSerializableExtra("id_note_click") != null) {
            idNote = (int) getIntent().getSerializableExtra("id_note_click");
            idToGetImg = idNote;
        }
        Log.e("idNote - CreateActivity",""+idNote);

        boolean hideLabel = false;
        if (getIntent().getSerializableExtra("hide_label") != null) {
            hideLabel = true;
        }
        if(hideLabel == true) {
            setting_note_layout.setVisibility(View.GONE);
            color_bg_field.setVisibility(View.GONE);
            day_create.setText("Nhập thông tin để tạo ghi chú mới");
            day_create.setVisibility(View.VISIBLE);
        }
        else {
            try {
                String pathImg = db.noteDAO().getNoteById(idNote).getImg_path();
                if(!pathImg.equals("")) {
                    image_note.setImageBitmap(BitmapFactory.decodeFile(pathImg));
                    image_field.setVisibility(View.VISIBLE);
                }
                else {
                    image_field.setVisibility(View.GONE);
                }
            }
            catch (Exception e) {
                Toast.makeText(this, "Không tìm thấy file, có thể đường dẫn đã mất hoặc bị thay đổi", Toast.LENGTH_SHORT).show();
            }
        }
        day_create.setText(date);

        note = new Notes();
        try {
            note = (Notes) getIntent().getSerializableExtra("old_note");
            note_title_detail.setText(note.getTitle());
            note_desc_detail.setText(note.getContent());
            isOldNote = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        colorPickOnClick();

        //onClicks
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = note_title_detail.getText().toString();
                String desc = note_desc_detail.getText().toString();
                if(desc.isEmpty()) {
                    Toast.makeText(CreateNoteActivity.this, "Vui long nhap noi dung", Toast.LENGTH_SHORT).show();
                    return;
                }
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
                Date date = new Date();

                if(!isOldNote) {
                    note = new Notes();
                }

                note.setUser(mAuth.getCurrentUser().getEmail());
                note.setTitle(title);
                note.setContent(desc);
                note.setDate_create(formatter.format(date));
                Intent intent = new Intent();
                intent.putExtra("note", note);
                setResult(Activity.RESULT_OK, intent);

                noteDbRef.push().setValue(note);
                finish();
            }
        });

        btn_setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeSelectionDialog();
            }
        });

        btn_setDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateSelectionDialog();
            }
        });

        remind_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layout_remind.getVisibility() == View.GONE) {
                    layout_remind.setVisibility(View.VISIBLE);
                }
                else if (layout_remind.getVisibility() == View.VISIBLE) {
                    layout_remind.setVisibility(View.GONE);
                }
            }
        });

        int finalIdNote = idNote;
        label_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateNoteActivity.this, LabelActivity.class);
                intent.putExtra("id_note", finalIdNote);
                startActivity(intent);
            }
        });

        password_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateNoteActivity.this, SetNotePassActivity.class);
                intent.putExtra("idNote",finalIdNote);
                startActivity(intent);
            }
        });

        image_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            CreateNoteActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                            , 17
                    );
                } else {
                    selectImage();
                }
            }
        });

        remove_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.noteDAO().updateImgPath(idToGetImg, "");
                image_field.setVisibility(View.GONE);
            }
        });

        pick_color_note_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(color_bg_field.getVisibility() == View.VISIBLE) {
                    color_bg_field.setVisibility(View.GONE);
                }
                else {
                    color_bg_field.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void colorPickOnClick() {
        pick_pink.setOnClickListener(view -> {
            db.noteDAO().updateColorNote(idToGetImg, "pink");
            Toast.makeText(this, "Hồng", Toast.LENGTH_SHORT).show();
        });
        pick_red.setOnClickListener(view -> {
            db.noteDAO().updateColorNote(idToGetImg, "red");
            Toast.makeText(this, "Đỏ", Toast.LENGTH_SHORT).show();
        });
        pick_yellow.setOnClickListener(view -> {
            db.noteDAO().updateColorNote(idToGetImg, "yellow");
            Toast.makeText(this, "Vàng", Toast.LENGTH_SHORT).show();
        });
        pick_blue.setOnClickListener(view -> {
            db.noteDAO().updateColorNote(idToGetImg, "blue");
            Toast.makeText(this, "Lam", Toast.LENGTH_SHORT).show();
        });
        pick_green.setOnClickListener(view -> {
            db.noteDAO().updateColorNote(idToGetImg, "green");
            Toast.makeText(this, "Lục", Toast.LENGTH_SHORT).show();
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 19);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 19) {
            if(resultCode == RESULT_OK) {
                if(data != null) {
                    Uri selectedImageUri = data.getData();
                    if(selectedImageUri != null) {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                            Bitmap bitMap = BitmapFactory.decodeStream(inputStream);
                            image_note.setImageBitmap(bitMap);
                            image_field.setVisibility(View.VISIBLE);

                            selectedImagePath = getPathFromUri(selectedImageUri);
                            db.noteDAO().updateImgPath(idToGetImg, selectedImagePath);
                        }
                        catch (Exception e) {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 17 && grantResults.length > 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            }
            else {
                Toast.makeText(this,"Quyền truy cập bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getPathFromUri(Uri contentUri) {
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri, null, null, null, null);
        if(cursor == null) {
            filePath = contentUri.getPath();
        }
        else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

    private void showTimeSelectionDialog() {
        int mHourOfDay = 0;
        int mMinute = 0;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                String min = String.valueOf(minute);
                if(String.valueOf(minute).length() == 1) {
                    min = "0"+minute;
                }
                time_display.setText(hourOfDay + ":" + min);
            }
        }, mHourOfDay, mMinute, true);
        timePickerDialog.show();
    }

    private void showDateSelectionDialog() {
        Calendar c = Calendar.getInstance();
        int startYear = c.get(Calendar.YEAR);
        int startMonth = c.get(Calendar.MONTH);
        int startDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                day_display.setText(dayOfMonth + "/" + (monthOfYear+1) + "/" + year);
            }
        }, startYear, startMonth, startDay);
        datePickerDialog.show();
    }
}