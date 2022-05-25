package com.example.noteapp;

import android.app.Activity;
import android.app.ProgressDialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText edt_currentPassword;
    private EditText edt_newPassword;
    private EditText edt_newPasswordConfirm;
    private Button btn_changePassword;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        initView();
        btn_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickChangePassword();
            }
        });
    }

    private void initView() {
        progressDialog = new ProgressDialog(this);
        edt_currentPassword = findViewById(R.id.edt_currentPassword);
        edt_newPassword = findViewById(R.id.edt_newPassword);
        edt_newPasswordConfirm = findViewById(R.id.edt_newPasswordConfirm);
        btn_changePassword = findViewById(R.id.btn_changePassword);
    }

    private void onClickChangePassword() {
        String strNewPassword = edt_newPassword.getText().toString().trim();
        progressDialog.show();
        String strNewPasswordConfirm = edt_newPasswordConfirm.getText().toString().trim();

        if(strNewPassword.equals(strNewPasswordConfirm)){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            user.updatePassword(strNewPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
