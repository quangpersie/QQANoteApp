package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
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

public class CreateNoteActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    TextView day_create, time_display, day_display, remove_img;
    EditText note_title_detail, note_desc_detail;
    Button btn_save, btn_setTime, btn_setDay;
    Button pick_pink, pick_red, pick_yellow, pick_blue, pick_green;
    Notes note;
    ImageView remind_note, label_note, password_note, image_insert, image_note, pick_color_note_bg;
    ImageView bold, italic, underline, align_center, align_justify, color_pick;
    LinearLayout layout_remind, setting_note_layout, image_field, color_bg_field;
    DatabaseReference noteDbRef;
    boolean isCreatedNote = false;
    String selectedImagePath;
    RoomDB db;
    int idToGetImg = -1;
    Button cancel_remind, confirm_remind;
    DatePicker mDatePicker;
    TimePicker mTimePicker;

    @RequiresApi(api = Build.VERSION_CODES.O)
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

        cancel_remind = findViewById(R.id.cancel_remind);
        confirm_remind = findViewById(R.id.confirm_remind);

        cancel_remind.setOnClickListener(this);
        confirm_remind.setOnClickListener(this);

        bold = findViewById(R.id.bold);
        italic = findViewById(R.id.italic);
        underline = findViewById(R.id.underline);
        align_center = findViewById(R.id.align_center);
        align_justify = findViewById(R.id.align_justify);
        color_pick = findViewById(R.id.color_pick);

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
//        Log.e("idNote - CreateActivity",""+idNote);

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
                if(db.noteDAO().getNoteById(idToGetImg).getDate_remind() != null) {
                    time_display.setText(db.noteDAO().getNoteById(idToGetImg).getTime_remind());
                }
                if(db.noteDAO().getNoteById(idToGetImg).getTime_remind() != null) {
                    day_display.setText(db.noteDAO().getNoteById(idToGetImg).getDate_remind());
                }
            }
            catch (Exception e) {
                Toast.makeText(this, "Không tìm thấy file, có thể đường dẫn đã mất hoặc bị thay đổi", Toast.LENGTH_SHORT).show();
            }
        }

        day_create.setText(date);

        note = new Notes();
        try {
            switch (db.defaultDAO().getSettingById(1).getSize_default()){
                case "Nhỏ":
                    note_desc_detail.setTextSize(16);
                    break;
                case "Lớn":
                    note_desc_detail.setTextSize(22);
                    break;
                case "Rất lớn":
                    note_desc_detail.setTextSize(26);
                    break;
                case "Cực đại":
                    note_desc_detail.setTextSize(30);
                    break;
                default:
                    note_desc_detail.setTextSize(18);
            }
            switch (db.defaultDAO().getSettingById(1).getFont_default()){
                case "Mặc định":
                    note_desc_detail.setTypeface(Typeface.DEFAULT);
                    break;
                case "Rokkit":
                    note_desc_detail.setTypeface(getResources().getFont(R.font.rokkit));
                    break;
                case "Librebodoni":
                    note_desc_detail.setTypeface(getResources().getFont(R.font.librebodoni));
                    break;
                case "RobotoSlab":
                    note_desc_detail.setTypeface(getResources().getFont(R.font.robotoslab));
                    break;
                case "Texturina":
                    note_desc_detail.setTypeface(getResources().getFont(R.font.texturina));
                    break;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        try {
            note = (Notes) getIntent().getSerializableExtra("old_note");
            note_title_detail.setText(note.getTitle());
            note_desc_detail.setText(note.getContent());
            switch (note.getFontSize()){
                case "Nhỏ":
                    note_desc_detail.setTextSize(16);
                    break;
                case "Lớn":
                    note_desc_detail.setTextSize(22);
                    break;
                case "Rất lớn":
                    note_desc_detail.setTextSize(26);
                    break;
                case "Cực đại":
                    note_desc_detail.setTextSize(30);
                    break;
                default:
                    note_desc_detail.setTextSize(18);
            }
            switch (note.getFontStyle()){
                case "Rokkit":
                    note_desc_detail.setTypeface(getResources().getFont(R.font.rokkit));
                    break;
                case "Librebodoni":
                    note_desc_detail.setTypeface(getResources().getFont(R.font.librebodoni));
                    break;
                case "RobotoSlab":
                    note_desc_detail.setTypeface(getResources().getFont(R.font.robotoslab));
                    break;
                case "Texturina":
                    note_desc_detail.setTypeface(getResources().getFont(R.font.texturina));
                    break;
                default:
                    note_desc_detail.setTypeface(Typeface.DEFAULT);
            }
            isCreatedNote = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        colorPickOnClick();

        //onClicks
        btn_save.setOnClickListener(view -> {
            String title = note_title_detail.getText().toString();
            String desc = note_desc_detail.getText().toString();
            if(title.isEmpty()) {
                Toast.makeText(CreateNoteActivity.this, "Tiêu đề không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
            Date date1 = new Date();

            if(!isCreatedNote) {
                note = new Notes();
            }

            note.setUser(mAuth.getCurrentUser().getEmail());
            note.setTitle(title);
            note.setContent(desc);
            note.setDate_create(formatter.format(date1));
            note.setFontSize(db.defaultDAO().getSettingById(1).getSize_default());
            note.setFontStyle(db.defaultDAO().getSettingById(1).getFont_default());

            Intent intent = new Intent();
            intent.putExtra("note", note);
            setResult(Activity.RESULT_OK, intent);


            noteDbRef.push().setValue(note);
            if(desc.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(CreateNoteActivity.this);
                alert.setTitle("Nội dung ghi chú rỗng");
                alert.setMessage("Bạn đang tạo ghi chú mà không có nội dung");
                alert.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                });
                alert.setNegativeButton("Thêm nội dung", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alert.show();
            }
            else {
                finish();
                //noteDbRef.push().setValue(note);
            }
        });

        btn_setTime.setOnClickListener(view -> showTimeSelectionDialog());

        btn_setDay.setOnClickListener(view -> showDateSelectionDialog());

        remind_note.setOnClickListener(view -> {
            if (layout_remind.getVisibility() == View.GONE) {
                layout_remind.setVisibility(View.VISIBLE);
            }
            else if (layout_remind.getVisibility() == View.VISIBLE) {
                layout_remind.setVisibility(View.GONE);
            }
        });

        int finalIdNote = idNote;
        label_note.setOnClickListener(view -> {
            Intent intent = new Intent(CreateNoteActivity.this, LabelActivity.class);
            intent.putExtra("id_note", finalIdNote);
            startActivity(intent);
        });

        password_note.setOnClickListener(view -> {
            Intent intent = new Intent(CreateNoteActivity.this, SetNotePassActivity.class);
            intent.putExtra("idNote",finalIdNote);
            startActivity(intent);
        });

        image_insert.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        CreateNoteActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                        , 17
                );
            } else {
                selectImage();
            }
        });

        remove_img.setOnClickListener(view -> {
            db.noteDAO().updateImgPath(idToGetImg, "");
            image_field.setVisibility(View.GONE);
        });

        pick_color_note_bg.setOnClickListener(view -> {
            if(color_bg_field.getVisibility() == View.VISIBLE) {
                color_bg_field.setVisibility(View.GONE);
            }
            else {
                color_bg_field.setVisibility(View.VISIBLE);
            }
        });
        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spannable spannable = new SpannableStringBuilder(note_desc_detail.getText());
                spannable.setSpan(new StyleSpan(Typeface.BOLD),
                        note_desc_detail.getSelectionStart(),
                        note_desc_detail.getSelectionEnd(),
                        0 );
                note_desc_detail.setText(spannable);
                note.setUser(mAuth.getCurrentUser().getEmail());
                note.setContent(note_desc_detail.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("note", note);
                setResult(Activity.RESULT_OK, intent);
            }
        });
        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spannable spannable = new SpannableStringBuilder(note_desc_detail.getText());
                spannable.setSpan(new StyleSpan(Typeface.ITALIC),
                        note_desc_detail.getSelectionStart(),
                        note_desc_detail.getSelectionEnd(),
                        0 );
                note_desc_detail.setText(spannable);
                note.setUser(mAuth.getCurrentUser().getEmail());
                note.setContent(note_desc_detail.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("note", note);
                setResult(Activity.RESULT_OK, intent);
            }
        });
        underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spannable spannable = new SpannableStringBuilder(note_desc_detail.getText());
                spannable.setSpan(new UnderlineSpan(),
                        note_desc_detail.getSelectionStart(),
                        note_desc_detail.getSelectionEnd(),
                        0 );
                note_desc_detail.setText(spannable);
                note.setUser(mAuth.getCurrentUser().getEmail());
                note.setContent(note_desc_detail.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("note", note);
                setResult(Activity.RESULT_OK, intent);
            }
        });
        align_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                note_desc_detail.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                Spannable spannable = new SpannableStringBuilder(note_desc_detail.getText());

                note_desc_detail.setText(spannable);
                note.setUser(mAuth.getCurrentUser().getEmail());
                note.setContent(note_desc_detail.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("note", note);
                setResult(Activity.RESULT_OK, intent);
            }
        });
        align_justify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                note_desc_detail.setTextAlignment(View.TEXT_ALIGNMENT_INHERIT);
                Spannable spannable = new SpannableStringBuilder(note_desc_detail.getText());

                note_desc_detail.setText(spannable);
                note.setUser(mAuth.getCurrentUser().getEmail());
                note.setContent(note_desc_detail.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("note", note);
                setResult(Activity.RESULT_OK, intent);
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
        Calendar c = Calendar.getInstance();
        int mHourOfDay = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                mTimePicker = timePicker;
                String min = String.valueOf(minute);
                if(String.valueOf(minute).length() == 1) {
                    min = "0"+minute;
                }
                time_display.setText(hourOfDay + ":" + min);
                db.noteDAO().updateTimeRemind(idToGetImg,hourOfDay + ":" + min);
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
                mDatePicker = datePicker;
                day_display.setText(dayOfMonth + "/" + (monthOfYear+1) + "/" + year);
                db.noteDAO().updateDateRemind(idToGetImg,dayOfMonth + "/" + (monthOfYear+1) + "/" + year);
            }
        }, startYear, startMonth, startDay);
        datePickerDialog.show();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(CreateNoteActivity.this, AlarmReceiver.class);

        intent.putExtra("name_sound",
                db.defaultDAO().getSettingById(1).getSound_default());

        intent.putExtra("title_note", db.noteDAO().getNoteById(idToGetImg).getTitle());
        intent.putExtra("desc_note", db.noteDAO().getNoteById(idToGetImg).getContent());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(CreateNoteActivity.this,
                db.noteDAO().getNoteById(idToGetImg).getRequest_code(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        switch (view.getId()) {
            case R.id.confirm_remind:
//                Log.e("OK","OK");
                Calendar remindTime = Calendar.getInstance();
                try {
                    remindTime.set(Calendar.HOUR_OF_DAY, mTimePicker.getCurrentHour());
                    remindTime.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());
                    remindTime.set(Calendar.SECOND, 0);
                    remindTime.set(Calendar.YEAR, mDatePicker.getYear());
                    remindTime.set(Calendar.MONTH, mDatePicker.getMonth());
                    remindTime.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
                }
                catch (Exception e) {
//                    Toast.makeText(this, "date or time null", Toast.LENGTH_SHORT).show();
                }
                long alarmStartTime = remindTime.getTimeInMillis();
                Calendar currentTime = Calendar.getInstance();
                if (remindTime.getTimeInMillis() > currentTime.getTimeInMillis()) {
                    alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent);
                    Toast.makeText(CreateNoteActivity.this, "Đặt nhắc nhở thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CreateNoteActivity.this, "Không đặt được thời gian trong quá khứ", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cancel_remind:
                alarm.cancel(alarmIntent);
                Toast.makeText(CreateNoteActivity.this, "Đã hủy nhắc nhở cho ghi chú", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /*private int getBroadcastCode() {
        return (int) new Date().getTime();
    }*/
}