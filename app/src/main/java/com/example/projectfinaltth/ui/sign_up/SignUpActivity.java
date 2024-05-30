package com.example.projectfinaltth.ui.sign_up;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.model.request.signup.SignUpRequest;
import com.example.projectfinaltth.databinding.SignupBinding;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SignUpActivity extends AppCompatActivity {
    SignupBinding mActivitySignUpBinding;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivitySignUpBinding = SignupBinding.inflate(getLayoutInflater());
        setContentView(mActivitySignUpBinding.getRoot());

        mActivitySignUpBinding.btnSignup.setOnClickListener(v -> {
            compositeDisposable.add(
                    ApiService.apiService.signUp(
                                    new SignUpRequest(
                                            mActivitySignUpBinding.editEmail.getText().toString(),
                                            mActivitySignUpBinding.editName.getText().toString(),
                                            mActivitySignUpBinding.editPassword.getText().toString(),
                                            mActivitySignUpBinding.editConfirmPassword.getText().toString()
                                    )
                            ).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                if (response.getSuccess() != null){
                                    Toast.makeText(this, response.getSuccess(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignUpActivity.this, OtpActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("email", mActivitySignUpBinding.editEmail.getText().toString());
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(this, response.getError(), Toast.LENGTH_SHORT).show();
                                }

                            }, throwable -> {
                                Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            })
            );
        });




    }
}
