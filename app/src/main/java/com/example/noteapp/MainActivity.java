package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.SearchView;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener,
        NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private Notes selectedNote;
    private FloatingActionButton add_note, remove_all, hide_show;
    private TextView empty_notify;
    private SearchView search_bar;
    private ImageView list_display, grid_display;
    private List<Notes> notes = new ArrayList<>();
    private RoomDB database;
    private boolean flag_display;
    private Spinner mSpinner;
    private List<String> lShowByLabel  = new ArrayList<>();
//    private List<Notes> noteCopy = new ArrayList<>();
    DatabaseReference noteDbRef, labelDbRef, settingDbref;
    NavigationView navigationView;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userMail = user.getEmail();
    private DrawerLayout drawerLayout;
    int idDF = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Log.e("TAG",""+userMail);
        Log.e("TAG",""+user.isEmailVerified());*/

        recyclerView = findViewById(R.id.recyclerview);
        add_note = findViewById(R.id.add_note);
        empty_notify = findViewById(R.id.empty_notify);
        search_bar = findViewById(R.id.search_bar);
        list_display = findViewById(R.id.list_display);
        grid_display = findViewById(R.id.grid_display);
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        mSpinner = findViewById(R.id.mSpinner);
        noteDbRef = FirebaseDatabase.getInstance().getReference().child("Notes");
        settingDbref = FirebaseDatabase.getInstance().getReference().child("Settings");
        remove_all = findViewById(R.id.remove_all);
        hide_show = findViewById(R.id.hide_show);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        database = RoomDB.getInstance(this);

        try {
            if(database.defaultDAO().getCount() == 0) {
                database.defaultDAO().insert(new DefaultSetting());
                List<DefaultSetting> lDF = database.defaultDAO().getAllDF();
                idDF = lDF.get(0).getId(); //=1
            }
        }
        catch (Exception e) {

        }

        DividerItemDecoration divider = new DividerItemDecoration(MainActivity.this,
                LinearLayoutManager.VERTICAL);

        ListLayout(notes);
        if(recyclerView.getItemDecorationCount() == 0) {
            recyclerView.addItemDecoration(divider);
        }

        hide_show.setOnClickListener(view -> {
            if(add_note.getVisibility() == View.VISIBLE && remove_all.getVisibility() == View.VISIBLE) {
                add_note.setVisibility(View.GONE);
                remove_all.setVisibility(View.GONE);
            }
            else {
                add_note.setVisibility(View.VISIBLE);
                remove_all.setVisibility(View.VISIBLE);
            }
        });

        add_note.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
            intent.putExtra("hide_label",707);
            startActivityForResult(intent, 10);
        });

        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterSearch(s);
                if(s.equals("")) {
                    empty_notify.setText("Chưa có ghi chú nào\n Bấm vào dấu cộng để thêm ghi chú mới");
                    empty_notify.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                return false;
            }
        });

        list_display.setOnClickListener(view -> {
            flag_display = false;
            grid_display.setBackgroundColor(MainActivity.this.getResources().getColor(R.color.white));
            list_display.setBackgroundColor(MainActivity.this.getResources().getColor(R.color.select_layout_color));
            MainActivity.this.ListLayout(notes);
            if (recyclerView.getItemDecorationCount() == 0) {
                recyclerView.addItemDecoration(divider);
            }
        });

        grid_display.setOnClickListener(view -> {
            flag_display = true;
            grid_display.setBackgroundColor(getResources().getColor(R.color.select_layout_color));
            list_display.setBackgroundColor(getResources().getColor(R.color.white));
            GridLayout(notes);
            recyclerView.removeItemDecoration(divider);
        });

        remove_all.setOnClickListener(view -> {
            if(notes.size() > 0) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Xóa tất cả vào thùng rác");
                alert.setMessage("Bạn vẫn muốn tiếp tục thực hiện thao tác này?");
                alert.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
                    Date date = new Date();
                    public void onClick(DialogInterface dialog, int whichButton) {
//                        database.noteDAO().deletedAllNoteToTrash();
                        for(Notes n:database.noteDAO().getAllUserNoteInUse(userMail)) {
                            database.noteDAO().deletedNote(n.getId());
                            database.noteDAO().updateNoteOrderDel(n.getId(),
                                    database.noteDAO().getMaxOrderDel(userMail) + 1);
                            database.noteDAO().updateDateDel(n.getId(),formatter.format(date));

                            Intent intent = new Intent(MainActivity.this, AlarmReceiverDel.class);
                            intent.putExtra("idNoteDelAuto",n.getId());
                            PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this,
                                    n.getRequest_code(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

                            Calendar startTime = Calendar.getInstance();
                            switch (database.defaultDAO().getSettingById(1).getDelete_default()) {
                                case "1 ngày":
                                    startTime.set(Calendar.MINUTE, startTime.getTime().getDay() + 1);
                                    break;
                                case "7 ngày":
                                    startTime.set(Calendar.MINUTE, startTime.getTime().getDay() + 7);
                                    break;
                                default:
                                    startTime.set(Calendar.MINUTE, startTime.getTime().getMinutes() + 1);
                            }
                            long alarmStartTime = startTime.getTimeInMillis();
                            alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent);
                        }
                        notes.clear();
                        noteAdapter.notifyDataSetChanged();
                        updateNotify();
                    }
                });
                alert.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alert.show();
            }
            else {
                Toast.makeText(this, "Không có ghi chú để xóa", Toast.LENGTH_SHORT).show();
            }
        });

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        addDataDefaultLabels();

        mSpinner = findViewById(R.id.mSpinner);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, lShowByLabel);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);
        mSpinner.setOnItemSelectedListener(MainActivity.this);
        updateNotify();

        try {
            TextView tvDisplay = (TextView) navigationView.getHeaderView(0).findViewById(R.id.display_user_name);
            tvDisplay.setText(userMail);
            TextView veDisplay = (TextView) navigationView.getHeaderView(0).findViewById(R.id.verify_notify);
            if(user.isEmailVerified()) {
                veDisplay.setText("Đã xác thực");
            }
            else {
                veDisplay.setText("Chưa xác thực");
            }
        }
        catch (Exception e) {

        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("flag",flag_display);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        flag_display = savedInstanceState.getBoolean("flag");
    }

    @Override
    protected void onResume() {
        super.onResume();
        notes.clear();
        if(hasPinNote()) {
            notes.addAll(database.noteDAO().getNoteHasPin(true,userMail));
            notes.addAll(database.noteDAO().getNoteNoPin(false,userMail));
        }
        else {
            notes = database.noteDAO().getAllUserNote(userMail);
        }

        if (grid_display.getBackground().equals(getResources().getColor(R.color.select_layout_color))) {
            flag_display = true;
        }
        DividerItemDecoration divider = new DividerItemDecoration(MainActivity.this,
                LinearLayoutManager.VERTICAL);

        if (flag_display == true) {
            grid_display.setBackgroundColor(getResources().getColor(R.color.select_layout_color));
            list_display.setBackgroundColor(getResources().getColor(R.color.white));
            GridLayout(notes);
            if(recyclerView.getItemDecorationCount() != 0) {
                recyclerView.removeItemDecoration(divider);
            }
        }
        else {
            ListLayout(notes);
            if(recyclerView.getItemDecorationCount() == 0) {
                recyclerView.addItemDecoration(divider);
            }
        }
        noteAdapter.notifyDataSetChanged();
        addDataDefaultLabels();
        mSpinner.setSelection(0);
        updateNotify();
    }

    private void addDataDefaultLabels() {
        List<Label> labels = database.labelDAO().getAllLabel();
        if(labels.size() == 0) {
            lShowByLabel.clear();
            lShowByLabel.add("Tất cả ghi chú");
            lShowByLabel.add("Work");
            lShowByLabel.add("Personal");
            lShowByLabel.add("Family");
        }
        else {
            lShowByLabel.clear();
            lShowByLabel.add("Tất cả ghi chú");
            for(Label label:labels) {
                lShowByLabel.add(label.getName());
            }
        }
    }


    private void filterSearch(String s) {
        List<Notes> containList = new ArrayList<>();
        for(Notes note:notes) {
            if(note.getTitle().toLowerCase(Locale.ROOT).contains(s.toLowerCase())
                    || note.getContent().toLowerCase(Locale.ROOT).contains(s.toLowerCase())) {
                containList.add(note);
            }
        }
        noteAdapter.filterList(containList);
        if(containList.size() == 0) {
            empty_notify.setVisibility(View.VISIBLE);
            empty_notify.setText("Không tìm thấy");
        }
        else {
            empty_notify.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 10) {
            if(resultCode == Activity.RESULT_OK) {
                Notes new_note = (Notes) data.getSerializableExtra("note");
                if(user.isEmailVerified() == true) {
                    new_note.setRequest_code(database.noteDAO().getMaxRequestCode()+1);
                    DefaultSetting df = database.defaultDAO().getSettingById(1);
                    settingDbref.push().setValue(df);
                    database.noteDAO().insert(new_note);
                    noteDbRef.push().setValue(new_note);
                }
                else if((user.isEmailVerified() == false
                        && database.noteDAO().getAllUserNoteAndRBin(userMail).size() < 5)) {
                    new_note.setRequest_code(database.noteDAO().getMaxRequestCode()+1);
                    database.noteDAO().insert(new_note);
                    noteDbRef.push().setValue(new_note);
                    DefaultSetting df = database.defaultDAO().getSettingById(1);
                    settingDbref.push().setValue(df);
                }
                else {
                    Toast toast = Toast.makeText(this, "Tài khoản chưa xác thực, ghi chú được tạo tối đa là 5", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 40);
                    toast.show();
                }
                notes.clear();
                notes.addAll(database.noteDAO().getAllUserNote(userMail));
                noteAdapter.notifyDataSetChanged();
//                updateNotify();
            }
            if(empty_notify.getVisibility() == View.VISIBLE) {
                empty_notify.setVisibility(View.GONE);
            }
        }
        else if(requestCode == 11) {
            if(resultCode == Activity.RESULT_OK) {
                Notes new_note = (Notes) data.getSerializableExtra("note");
                database.noteDAO().update(new_note.getId(), new_note.getTitle(), new_note.getContent());
                notes.clear();
                notes.addAll(database.noteDAO().getAllUserNote(userMail));
                noteAdapter.notifyDataSetChanged();
//                updateNotify();
            }
        }
    }

    private void ListLayout(List<Notes> notes) {
        noteAdapter = new NoteAdapter(MainActivity.this, notes, noteClickListener);

        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this,
                LinearLayoutManager.VERTICAL,false);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(noteAdapter);
    }

    private void GridLayout(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        noteAdapter = new NoteAdapter(MainActivity.this, notes, noteClickListener);
        recyclerView.setAdapter(noteAdapter);
    }

    private final NoteClickListener noteClickListener = new NoteClickListener() {
        @Override
        public void onClick(Notes notes) {
//            Log.e("PAS",""+notes.getPassword());
            if(!notes.getPassword().equals("")) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                final EditText edt = new EditText(MainActivity.this);
                edt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                edt.setTransformationMethod(new PasswordTransformationMethod());
                edt.setPadding(32,32,32,32);
                alert.setTitle("Ghi chú này yêu cầu mật khẩu");
                alert.setMessage("Nhập mật khẩu để xem nội dung");
                alert.setView(edt);

                alert.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String input_pass = edt.getText().toString();
                        if(input_pass.equals(notes.getPassword())) {
                            Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                            intent.putExtra("old_note", notes);
                            intent.putExtra("date_create", notes.getDate_create());
                            intent.putExtra("id_note_click", notes.getId());
                            startActivityForResult(intent, 11);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alert.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alert.show();
            }
            else {
                Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                intent.putExtra("old_note", notes);
                intent.putExtra("date_create", notes.getDate_create());
                intent.putExtra("id_note_click", notes.getId());
                startActivityForResult(intent, 11);
            }
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
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    boolean hasPinNote() {
        if(database.noteDAO().getCountNoteUser(userMail) != 0) {
            for(Notes note:database.noteDAO().getAllUserNote(userMail)) {
                if(note.isPinned()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        Intent intent = new Intent(MainActivity.this, AlarmReceiverDel.class);
        intent.putExtra("idNoteDelAuto",selectedNote.getId());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this,
                selectedNote.getRequest_code(), intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

        switch (item.getItemId()) {
            case R.id.pin_note:
                if(selectedNote.isPinned()) {
                    database.noteDAO().pin(selectedNote.getId(), false);
                    Toast.makeText(this, "Đã bỏ ghim", Toast.LENGTH_SHORT).show();
                    database.noteDAO().unPin(selectedNote.getId());
                    selectedNote.setPinned(false);
                    noteDbRef.push().setValue(selectedNote);
                    notes.clear();
                    if(hasPinNote()) {
                        notes.addAll(database.noteDAO().getNoteHasPin(true,userMail));
                        notes.addAll(database.noteDAO().getNoteNoPin(false,userMail));
                    }
                    else {
                        notes.addAll(database.noteDAO().getAllUserNote(userMail));
                    }
//                    noteAdapter.notifyDataSetChanged();
                }
                else {
                    database.noteDAO().pin(selectedNote.getId(), true);
//                    Toast.makeText(this, "Đã ghim", Toast.LENGTH_SHORT).show();

                    database.noteDAO().updateOrder(selectedNote.getId(),database.noteDAO().maxOrderForPin(userMail));
                    selectedNote.setPinned(true);
                    noteDbRef.push().setValue(selectedNote);
                    notes.clear();
                    notes.addAll(database.noteDAO().getNoteHasPin(true,userMail));
                    notes.addAll(database.noteDAO().getNoteNoPin(false,userMail));
                }
                noteAdapter.notifyDataSetChanged();
                return true;
            case R.id.delete_note:
                if(!selectedNote.getTime_remind().equals("")
                        && !selectedNote.getDate_remind().equals("")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setTitle("Ghi chú trong thời gian nhắc nhở");
                    alert.setMessage("Để xóa, hãy hủy nhắc nhở của ghi chú này trong màn hình sửa đổi ghi chú");
                    alert.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    alert.show();
                }
                else {
                    Calendar startTime = Calendar.getInstance();
                    switch (database.defaultDAO().getSettingById(1).getDelete_default()) {
                        case "1 ngày":
                            startTime.set(Calendar.MINUTE, startTime.getTime().getDay() + 1);
                            break;
                        case "7 ngày":
                            startTime.set(Calendar.MINUTE, startTime.getTime().getDay() + 7);
                            break;
                        default:
                            startTime.set(Calendar.MINUTE, startTime.getTime().getMinutes() + 1);
                    }
                    long alarmStartTime = startTime.getTimeInMillis();
                    alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent);

                    SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
                    database.noteDAO().updateDateDel(selectedNote.getId(),formatter.format(new Date()));
                    database.noteDAO().updateNoteOrderDel(selectedNote.getId(),
                            database.noteDAO().getMaxOrderDel(userMail)+1);

                    database.noteDAO().deletedNote(selectedNote.getId());
                    selectedNote.setDelete(true);
                    noteDbRef.push().setValue(selectedNote);
                    notes.remove(selectedNote);
                    noteAdapter.notifyDataSetChanged();
                    updateNotify();
                    Toast.makeText(this, "Đã xóa ghi chú", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.font_change){
             startActivity(new Intent(MainActivity.this, FontSettingActivity.class));
        }else if(id == R.id.sound_change) {
            startActivity(new Intent(MainActivity.this,PickSoundNotifyDefault.class));
        }else if(id == R.id.trash_can) {
            startActivity(new Intent(MainActivity.this, RecycleBinActivity.class));
        }else if(id == R.id.auth_nav){
            if(user.isEmailVerified()) {
                Toast.makeText(this, "Tài khoản đã được xác thực", Toast.LENGTH_SHORT).show();
            }
            else {
                startActivity(new Intent(MainActivity.this, VerifyActivity.class));
            }
        }else if(id == R.id.change_password){
            startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
        }else if(id == R.id.log_out){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    private void updateNotify() {
        if(database.noteDAO().getAllUserNote(userMail).size() != 0 &&
                notes.size() == 0 && mSpinner.getSelectedItemPosition() != 0) {
            empty_notify.setVisibility(View.VISIBLE);
            empty_notify.setText("Nhãn đang chọn không bao gồm ghi chú nào");
            empty_notify.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            return;
        }
        else if(mSpinner.getSelectedItemPosition() != 0 && notes.size() == 0) {
            empty_notify.setVisibility(View.VISIBLE);
            empty_notify.setText("Nhãn đang chọn không bao gồm ghi chú nào");
            empty_notify.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            return;
        }
        else if(database.noteDAO().getAllUserNote(userMail).size() == 0){
            empty_notify.setVisibility(View.VISIBLE);
            empty_notify.setText("Chưa có ghi chú nào\n Bấm vào dấu cộng để thêm ghi chú mới");
            empty_notify.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            return;
        }
        else {
            empty_notify.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        if(pos == 0) {
            notes.clear();
            if(hasPinNote()) {
                notes.addAll(database.noteDAO().getNoteHasPin(true,userMail));
                notes.addAll(database.noteDAO().getNoteNoPin(false,userMail));
            }
            else {
                notes = database.noteDAO().getAllUserNote(userMail);
            }
        }
        else {
            String labelName = adapterView.getItemAtPosition(pos).toString().toLowerCase();
            notes.clear();
            for(Notes note:database.noteDAO().getAllUserNote(userMail)) {
                if(note.getLabel().contains(labelName)) {
                    notes.add(note);
                }
            }
        }
//        noteCopy = notes;
        noteAdapter = new NoteAdapter(this, notes, noteClickListener);
        recyclerView.setAdapter(noteAdapter);
        noteAdapter.notifyDataSetChanged();
        updateNotify();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}