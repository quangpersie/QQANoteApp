package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private TextView title_register;
    private EditText email_register;
    private EditText pass_register;
    private EditText pass2_register;
    private Button register_button;
    private TextView notify_pass;
    private ProgressBar progressBar_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        InitialVar();
        title_register.setOnClickListener(this);
        register_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_register:
                finish();
                break;
            case R.id.register_button:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = email_register.getText().toString().trim();
        String pass = pass_register.getText().toString().trim();
        String pass2 = pass2_register.getText().toString().trim();

        if(email.equals("")) {
            email_register.setError("Vui lòng nhập email");
            email_register.requestFocus();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_register.setError("Vui lòng cung cấp email hợp lệ");
            email_register.requestFocus();
        }
        else if(pass.equals("")) {
            pass_register.setError("Vui lòng nhập mật khẩu");
            pass_register.requestFocus();
        }
        else if(pass.length() < 8) {
            pass_register.setError("Mật khẩu phải có ít nhất 8 kí tự");
            pass_register.requestFocus();
        }
        else if(pass2.equals(pass) == false) {
            notify_pass.setText("Mật khẩu xác nhận không trùng khớp");
        }
        else {
            notify_pass.setText("");
            progressBar_register.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Tài khoản đã được đăng ký thành công", Toast.LENGTH_SHORT).show();
                        progressBar_register.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(SignUpActivity.this,VerifyActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(SignUpActivity.this, "Đăng ký tài khoản thất bại", Toast.LENGTH_SHORT).show();
                        progressBar_register.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    private void InitialVar() {
        title_register = findViewById(R.id.title_register);
        email_register = findViewById(R.id.email_register);
        pass_register = findViewById(R.id.pass_register);
        pass2_register = findViewById(R.id.pass2_register);
        register_button = findViewById(R.id.register_button);
        notify_pass = findViewById(R.id.notify_pass);
        progressBar_register = findViewById(R.id.progressBar_register);
    }
}