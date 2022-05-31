package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.PasswordTransformationMethod;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CreateNoteActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    TextView day_create, time_display, day_display, remove_img;
    EditText note_title_detail, note_desc_detail;
    Button btn_save, btn_setTime, btn_setDay, btn_back;
    Button pick_pink, pick_red, pick_yellow, pick_blue, pick_green;
    Notes note;
    ImageView remind_note, label_note, password_note, image_insert, image_note, pick_color_note_bg;
    ImageView align_left, align_right, align_center, align_justify, color_pick;
    ImageView red_text, blue_text, green_text, black_text, purple_text, share_note;
    LinearLayout layout_remind, setting_note_layout, image_field, color_bg_field, rich_texts, field_pick_color;
    DatabaseReference noteDbRef;
    boolean isCreatedNote = false;
    String selectedImagePath;
    RoomDB db;
    int idToGetImg = -1;
    Button cancel_remind, confirm_remind;
    DatePicker mDatePicker;
    TimePicker mTimePicker;
    Bitmap bitmap;
    @SuppressLint("ResourceType")
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
        share_note = findViewById(R.id.share_note);

        cancel_remind = findViewById(R.id.cancel_remind);
        confirm_remind = findViewById(R.id.confirm_remind);

        cancel_remind.setOnClickListener(this);
        confirm_remind.setOnClickListener(this);

        align_left = findViewById(R.id.align_left);
        align_right = findViewById(R.id.align_right);
        align_center = findViewById(R.id.align_center);
        align_justify = findViewById(R.id.align_justify);
        color_pick = findViewById(R.id.color_pick);

        pick_pink = findViewById(R.id.pick_pink);
        pick_red = findViewById(R.id.pick_red);
        pick_yellow = findViewById(R.id.pick_yellow);
        pick_blue = findViewById(R.id.pick_blue);
        pick_green = findViewById(R.id.pick_green);
        btn_back = findViewById(R.id.btn_back);
        rich_texts = findViewById(R.id.rich_texts);
        field_pick_color = findViewById(R.id.field_pick_color);

        red_text = findViewById(R.id.red_text);
        blue_text = findViewById(R.id.blue_text);
        green_text = findViewById(R.id.green_text);
        black_text = findViewById(R.id.black_text);
        purple_text = findViewById(R.id.purple_text);

        noteDbRef = FirebaseDatabase.getInstance().getReference().child("Notes");
        db = RoomDB.getInstance(this);

        selectedImagePath = "";
        int idNote = -17;
        if (getIntent().getSerializableExtra("id_note_click") != null) {
            idNote = (int) getIntent().getSerializableExtra("id_note_click");
            idToGetImg = idNote;
        }

        boolean hideLabel = false;
        if (getIntent().getSerializableExtra("hide_label") != null) {
            hideLabel = true;
        }
//        Log.e("hide",""+hideLabel);
        if(hideLabel == true) {
            setting_note_layout.setVisibility(View.GONE);
            color_bg_field.setVisibility(View.GONE);
            rich_texts.setVisibility(View.GONE);
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
            catch (Exception e) {}
            try {
                if(db.noteDAO().getNoteById(idToGetImg).getDate_remind() != null) {
                    time_display.setText(db.noteDAO().getNoteById(idToGetImg).getTime_remind());
                }
                if(db.noteDAO().getNoteById(idToGetImg).getTime_remind() != null) {
                    day_display.setText(db.noteDAO().getNoteById(idToGetImg).getDate_remind());
                }
            }
            catch (Exception e) {}
        }

        try {
            String date = (String) getIntent().getSerializableExtra("date_create");
            if(date.equals("")) {
                day_create.setText("Nhập tiêu đề, nội dung để tạo ghi chú mới");
            }
            else {
                day_create.setText(date);
            }
        } catch (Exception e) {}

        note = new Notes();

        try {
            switch (db.defaultDAO().getSettingById(1).getSize_default()){
                case "Nhỏ":
//                    note_title_detail.setTextSize(15);
                    note_desc_detail.setTextSize(14);
                    break;
                case "Lớn":
//                    note_title_detail.setTextSize(20);
                    note_desc_detail.setTextSize(22);
                    break;
                case "Rất lớn":
//                    note_title_detail.setTextSize(24);
                    note_desc_detail.setTextSize(26);
                    break;
                case "Cực đại":
//                    note_title_detail.setTextSize(30);
                    note_desc_detail.setTextSize(30);
                    break;
                default:
//                    note_title_detail.setTextSize(17);
                    note_desc_detail.setTextSize(18);
            }
            switch (db.defaultDAO().getSettingById(1).getFont_default()){
                case "Rokkit":
//                    note_title_detail.setTypeface(getResources().getFont(R.font.rokkit));
                    note_desc_detail.setTypeface(getResources().getFont(R.font.rokkit));
                    break;
                case "Librebodoni":
//                    note_title_detail.setTypeface(getResources().getFont(R.font.librebodoni));
                    note_desc_detail.setTypeface(getResources().getFont(R.font.librebodoni));
                    break;
                case "RobotoSlab":
//                    note_title_detail.setTypeface(getResources().getFont(R.font.robotoslab));
                    note_desc_detail.setTypeface(getResources().getFont(R.font.robotoslab));
                    break;
                case "Texturina":
//                    note_title_detail.setTypeface(getResources().getFont(R.font.texturina));
                    note_desc_detail.setTypeface(getResources().getFont(R.font.texturina));
                    break;
                default:
//                    note_title_detail.setTypeface(Typeface.DEFAULT);
                    note_desc_detail.setTypeface(Typeface.DEFAULT);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            note = (Notes) getIntent().getSerializableExtra("old_note");
            note_title_detail.setText(note.getTitle());
            note_desc_detail.setText(note.getContent());
            isCreatedNote = true;

            switch (note.getFontSize()){
                case "Nhỏ":
                    note_desc_detail.setTextSize(14);
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
            switch (note.getAlign()){
                case "right":
                    note_desc_detail.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                    break;
                case "center":
                    note_desc_detail.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    break;
                case "justify":
                    note_desc_detail.setTextAlignment(View.TEXT_ALIGNMENT_INHERIT);
                    break;
                default:
                    note_desc_detail.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            }
            switch (note.getAlign()){
                case "right":
                    note_desc_detail.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                    break;
                case "center":
                    note_desc_detail.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    break;
                case "justify":
                    note_desc_detail.setTextAlignment(View.TEXT_ALIGNMENT_INHERIT);
                    break;
                default:
                    note_desc_detail.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            }
            switch (note.getColor_text()){
                case "red":
                    note_desc_detail.setTextColor(getResources().getColor(R.color.red_text));
                    break;
                case "blue":
                    note_desc_detail.setTextColor(getResources().getColor(R.color.blue_text));
                    break;
                case "green":
                    note_desc_detail.setTextColor(getResources().getColor(R.color.green_text));
                    break;
                case "purple":
                    note_desc_detail.setTextColor(getResources().getColor(R.color.purple_text));
                    break;
                default:
                    note_desc_detail.setTextColor(getResources().getColor(R.color.black));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        colorPickOnClick();

        //From Alarm
        try {
            int id_from_alarm = (int) getIntent().getSerializableExtra("idNoteFromAlarm");
            Notes note_from_alarm = db.noteDAO().getNoteById(id_from_alarm);
            note_title_detail.setText(note_from_alarm.getTitle());
            note_desc_detail.setText(note_from_alarm.getContent());
            day_create.setText(note_from_alarm.getDate_create());

            rich_texts.setVisibility(View.GONE);
            setting_note_layout.setVisibility(View.GONE);
            btn_save.setEnabled(false);
            btn_save.setBackgroundColor(getResources().getColor(R.color.grey));
        }
        catch (Exception e) {}

        //onClicks
        color_pick.setOnClickListener(view -> {
            if(field_pick_color.getVisibility() == View.GONE) {
                field_pick_color.setVisibility(View.VISIBLE);
            }
            else {
                field_pick_color.setVisibility(View.GONE);
            }
        });

        btn_back.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        btn_save.setOnClickListener(view -> {
            String title = note_title_detail.getText().toString();
            String desc = note_desc_detail.getText().toString();
            if(title.isEmpty()) {
                Toast.makeText(CreateNoteActivity.this, "Tiêu đề không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");

            if(!isCreatedNote) {
                note = new Notes();
            }
            note.setUser(mAuth.getCurrentUser().getEmail());
            note.setTitle(title);
            note.setContent(desc);
            note.setDate_create(formatter.format(new Date()));
            note.setFontSize(db.defaultDAO().getSettingById(1).getSize_default());
            note.setFontStyle(db.defaultDAO().getSettingById(1).getFont_default());


            Intent intent = new Intent();
            intent.putExtra("note", note);
            setResult(Activity.RESULT_OK, intent);

            //noteDbRef.push().setValue(note);

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
            if(color_bg_field.getVisibility() == View.VISIBLE) {
                color_bg_field.setVisibility(View.GONE);
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
            if(layout_remind.getVisibility() == View.VISIBLE) {
                layout_remind.setVisibility(View.GONE);
            }
        });

        align_left.setOnClickListener(view -> {
            note_desc_detail.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            db.noteDAO().updateAlign(finalIdNote,"left");

            Spannable spannable = new SpannableStringBuilder(note_desc_detail.getText());
            note_desc_detail.setText(spannable);
            note.setContent(note_desc_detail.getText().toString());
        });

        align_right.setOnClickListener(view -> {
            db.noteDAO().updateAlign(finalIdNote,"right");
            note_desc_detail.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

            Spannable spannable = new SpannableStringBuilder(note_desc_detail.getText());
            note_desc_detail.setText(spannable);
            note.setContent(note_desc_detail.getText().toString());
        });
        align_center.setOnClickListener(view -> {
            db.noteDAO().updateAlign(finalIdNote,"center");
            note_desc_detail.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            Spannable spannable = new SpannableStringBuilder(note_desc_detail.getText());
            note_desc_detail.setText(spannable);
            note.setContent(note_desc_detail.getText().toString());
        });
        align_justify.setOnClickListener(view -> {
            db.noteDAO().updateAlign(finalIdNote,"justify");
            note_desc_detail.setTextAlignment(View.TEXT_ALIGNMENT_INHERIT);

            Spannable spannable = new SpannableStringBuilder(note_desc_detail.getText());
            note_desc_detail.setText(spannable);
            note.setContent(note_desc_detail.getText().toString());
        });

        red_text.setOnClickListener(view -> {
            db.noteDAO().updateColorText(finalIdNote, "red");
            note_desc_detail.setTextColor(getResources().getColor(R.color.red_text));

            Spannable spannable = new SpannableStringBuilder(note_desc_detail.getText());
            note_desc_detail.setText(spannable);
            note.setContent(note_desc_detail.getText().toString());
        });
        blue_text.setOnClickListener(view -> {
            db.noteDAO().updateColorText(finalIdNote, "blue");
            note_desc_detail.setTextColor(getResources().getColor(R.color.blue_text));
        });
        green_text.setOnClickListener(view -> {
            db.noteDAO().updateColorText(finalIdNote, "green");
            note_desc_detail.setTextColor(getResources().getColor(R.color.green_text));
        });
        purple_text.setOnClickListener(view -> {
            db.noteDAO().updateColorText(finalIdNote, "red");
            note_desc_detail.setTextColor(getResources().getColor(R.color.red_text));
        });
        black_text.setOnClickListener(view -> {
            db.noteDAO().updateColorText(finalIdNote, "black");
            note_desc_detail.setTextColor(getResources().getColor(R.color.black));
        });
        purple_text.setOnClickListener(view -> {
            db.noteDAO().updateColorText(finalIdNote, "purple");
            note_desc_detail.setTextColor(getResources().getColor(R.color.purple_text));

            Spannable spannable = new SpannableStringBuilder(note_desc_detail.getText());
            note_desc_detail.setText(spannable);
            note.setContent(note_desc_detail.getText().toString());
        });
        share_note.setOnClickListener(view -> {
            if(!note.getImg_path().equals("")){
                BitmapDrawable bitmapDrawable = (BitmapDrawable)image_note.getDrawable();
                bitmap = bitmapDrawable.getBitmap();
                shareNoteWithImage();
            }else{
                shareNoteWithoutImage();
            }
        });
    }
    private void shareNoteWithoutImage() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, note.getContent());
        share.putExtra(Intent.EXTRA_TITLE, note.getTitle());
        share.setType("text/plain");
        startActivity(Intent.createChooser(share, null));
    }


    private void shareNoteWithImage() {
        Intent share = new Intent(Intent.ACTION_SEND);
        Uri imageUri;
        imageUri = saveImage(bitmap, getApplicationContext());
        share.putExtra(Intent.EXTRA_TEXT, note.getContent());
        share.putExtra(Intent.EXTRA_TITLE, note.getTitle());
        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        share.setType(Intent.normalizeMimeType("*/*"));
        Intent.createChooser(share, "Share File");
        @SuppressLint("QueryPermissionsNeeded") List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(share, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            this.grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        startActivity(share);
    }

    private Uri saveImage(Bitmap bitmap, Context context) {
        File imagesFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try{
            imagesFolder.mkdir();
            File file = new File(imagesFolder, "shared_images.jpg");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(Objects.requireNonNull(context.getApplicationContext()),
                    "com.example.noteapp" + ".provider", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uri;
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
                            Notes note = db.noteDAO().getNoteById(idToGetImg);
                            noteDbRef.push().setValue(note);
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
        intent.putExtra("idNote",idToGetImg);
        intent.putExtra("title_note", db.noteDAO().getNoteById(idToGetImg).getTitle());
        intent.putExtra("desc_note", db.noteDAO().getNoteById(idToGetImg).getContent());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(CreateNoteActivity.this,
                db.noteDAO().getNoteById(idToGetImg).getRequest_code(), intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        switch (view.getId()) {
            case R.id.confirm_remind:
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

                }
                long alarmStartTime = remindTime.getTimeInMillis();
                Calendar currentTime = Calendar.getInstance();
                if(time_display.getText().toString().equals("")) {
                    Toast.makeText(this, "Vui lòng chọn giờ nhắc nhở", Toast.LENGTH_SHORT).show();
                }
                else if(day_display.getText().toString().equals("")) {
                    Toast.makeText(this, "Vui lòng chọn ngày nhắc nhở", Toast.LENGTH_SHORT).show();
                }
                else if (remindTime.getTimeInMillis() > currentTime.getTimeInMillis()) {
                    alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent);
                    Toast.makeText(CreateNoteActivity.this, "Đặt nhắc nhở thành công", Toast.LENGTH_SHORT).show();
                    Notes note = db.noteDAO().getNoteById(idToGetImg);
                    noteDbRef.push().setValue(note);
                } else {
                    db.noteDAO().updateDateRemind(idToGetImg,"");
                    day_display.setText("");
                    db.noteDAO().updateTimeRemind(idToGetImg,"");
                    time_display.setText("");
                    Toast.makeText(CreateNoteActivity.this, "Không đặt được thời gian trong quá khứ", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cancel_remind:
                alarm.cancel(alarmIntent);
                db.noteDAO().updateDateRemind(idToGetImg, "");
                db.noteDAO().updateTimeRemind(idToGetImg, "");
                Toast.makeText(CreateNoteActivity.this, "Đã hủy nhắc nhở cho ghi chú", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}