package com.example.noteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyActivity extends AppCompatActivity implements View.OnClickListener {

    private Button sendLink, success;
    private TextView resend, skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        sendLink = findViewById(R.id.sendLink);
        skip = findViewById(R.id.skip);
        resend = findViewById(R.id.resend);
        success = findViewById(R.id.success);

        sendLink.setOnClickListener(this);
        resend.setOnClickListener(this);
        success.setOnClickListener(this);
        skip.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        switch (view.getId()) {
            case R.id.sendLink:
                sendLink.setEnabled(false);
                user.sendEmailVerification();
                Toast.makeText(this, "Kiểm tra email để xác thực tài khoản", Toast.LENGTH_SHORT).show();
                break;
            case R.id.skip:
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                break;
            case R.id.resend:
                user.sendEmailVerification();
                Toast.makeText(this, "Hệ thống đã gửi lại link xác thực, vui lòng kiểm tra email để xác thực", Toast.LENGTH_SHORT).show();
                break;
            case R.id.success:
                final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user.isEmailVerified()) {
                            Toast.makeText(getApplicationContext(), "Xác thực thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Tài khoản của bạn chưa được xác thực, vui lòng kiểm tra lại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }
}