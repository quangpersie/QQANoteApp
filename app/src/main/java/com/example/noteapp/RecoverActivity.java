package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecoverActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email_recover;
    private Button pass_recover, back_toLogin;
    private ProgressBar pBar_recover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover);

        mAuth = FirebaseAuth.getInstance();
        InitialVar();

        pass_recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recoverPW();
            }
        });

        back_toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void recoverPW() {
        String email = email_recover.getText().toString().trim();
        if(email.equals("")) {
            email_recover.setError("Vui lòng nhập email của bạn");
            email_recover.requestFocus();
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_recover.setError("Vui lòng cung cấp email hợp lệ");
            email_recover.requestFocus();
            return;
        }
        else {
            pBar_recover.setVisibility(View.VISIBLE);
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        pBar_recover.setVisibility(View.INVISIBLE);
                        Toast.makeText(RecoverActivity.this, "Kiểm tra email của bạn để đặt lại mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(RecoverActivity.this, "Email không đúng, hãy kiểm tra lại", Toast.LENGTH_SHORT).show();
                        pBar_recover.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    private void InitialVar() {
        email_recover = findViewById(R.id.email_recover);
        pass_recover = findViewById(R.id.pass_recover);
        pBar_recover = findViewById(R.id.pBar_recover);
        back_toLogin = findViewById(R.id.back_toLogin);
    }
}