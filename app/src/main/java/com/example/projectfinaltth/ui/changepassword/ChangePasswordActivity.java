package com.example.projectfinaltth.ui.changepassword;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.password.ChangePasswordRequest;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText eTextCurrentPassword, eTextNewPassword, eTextReNewPassword;
    private Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        eTextCurrentPassword = findViewById(R.id.eTextCurrentPassword);
        eTextNewPassword = findViewById(R.id.eTextNewPassword);
        eTextReNewPassword = findViewById(R.id.eTextReNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(v -> handleChangePassword());
    }

    private void handleChangePassword() {
        String currentPassword = eTextCurrentPassword.getText().toString().trim();
        String newPassword = eTextNewPassword.getText().toString().trim();
        String reNewPassword = eTextReNewPassword.getText().toString().trim();

        if (newPassword.equals(reNewPassword)) {
            String token = DataLocalManager.getToken();
            ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword, reNewPassword);

            ApiService.apiService.changePassword("Bearer " + token, request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            // Handle onSubscribe if needed
                        }

                        @Override
                        public void onComplete() {
                            Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(ChangePasswordActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }
    }
}
