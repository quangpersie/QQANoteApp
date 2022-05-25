package com.example.noteapp;

import android.app.ProgressDialog;

import android.content.Intent;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
        String currentPassword = edt_currentPassword.getText().toString();
        String strNewPassword = edt_newPassword.getText().toString();
        String strNewPasswordConfirm = edt_newPasswordConfirm.getText().toString();

        if(currentPassword.equals("")) {
            edt_currentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
            edt_currentPassword.requestFocus();
        }
        else if(strNewPassword.equals("")) {
            edt_newPassword.setError("Vui lòng nhập mật khẩu mới");
            edt_newPassword.requestFocus();
        }
        else if(currentPassword.contains(" ")) {
            edt_currentPassword.setError("Mật khẩu không được chứa khoảng trắng");
            edt_currentPassword.requestFocus();
        }
        else if(strNewPassword.contains(" ")) {
            edt_newPassword.setError("Mật khẩu không được chứa khoảng trắng");
            edt_newPassword.requestFocus();
        }
        else if(currentPassword.length() < 8) {
            edt_currentPassword.setError("Mật khẩu phải có ít nhất 8 kí tự");
            edt_currentPassword.requestFocus();
        }
        else if(strNewPassword.length() < 8) {
            edt_newPassword.setError("Mật khẩu phải có ít nhất 8 kí tự");
            edt_newPassword.requestFocus();

        }
        else if(strNewPasswordConfirm.equals("")) {
            edt_newPasswordConfirm.setError("Vui lòng nhập mật khẩu xác nhận");
            edt_newPasswordConfirm.requestFocus();
        }
        else if(!strNewPassword.equals(strNewPasswordConfirm)) {
            edt_newPasswordConfirm.setError("Mật khẩu xác nhận không trùng khớp");
            edt_newPasswordConfirm.requestFocus();
        }
        if(strNewPassword.equals(strNewPasswordConfirm)){
            progressDialog.show();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null && user.getEmail() != null) {
                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), currentPassword);
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            user.updatePassword(strNewPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                            progressDialog.dismiss();
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(ChangePasswordActivity.this, SignInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu không thành công, kiểm tra lại mật khẩu hiện tại", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            }


        }
    }
}
