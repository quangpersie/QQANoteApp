package com.example.noteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateNoteActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextView day_create, time_display, day_display;
    EditText note_title_detail, note_desc_detail;
    Button btn_save, btn_setTime, btn_setDay;
    Notes note;
    ImageView remind_note, bold, italic, underline, align_center, align_justify, color_pick;
    LinearLayout layout_remind;
    DatabaseReference noteDbRef = FirebaseDatabase.getInstance().getReference().child("Notes");;
    boolean isOldNote = false;

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
        bold = findViewById(R.id.bold);
        italic = findViewById(R.id.italic);
        underline = findViewById(R.id.underline);
        align_center = findViewById(R.id.align_center);
        align_justify = findViewById(R.id.align_justify);
        color_pick = findViewById(R.id.color_pick);

        String date = (String) getIntent().getSerializableExtra("date_create");
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