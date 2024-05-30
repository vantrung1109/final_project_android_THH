package com.example.projectfinaltth.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.response.profile.UserResponse;
import com.example.projectfinaltth.ui.changepassword.ChangePasswordActivity;
import com.example.projectfinaltth.ui.sign_in.SignInActivity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AccountFragment extends Fragment {

    private TextView tvName, tvEmail;
    private ImageView imgProfile;
    private RelativeLayout layoutLogout, layoutChangePassword;

    public AccountFragment() {
        // Required empty public constructor
    }

    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        Log.e("course","Account");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.e("course","Reload Account");
        fetchUserData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);
        tvName = view.findViewById(R.id.tv_name);
        tvEmail = view.findViewById(R.id.tv_email);
        imgProfile = view.findViewById(R.id.img_profile);
        layoutLogout = view.findViewById(R.id.layout_log_out);
        layoutChangePassword = view.findViewById(R.id.layout_change_password); // Add this line

        layoutLogout.setOnClickListener(v -> {
            logout();
        });

        layoutChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void fetchUserData() {
        String token = DataLocalManager.getToken(); // Replace with actual token
        ApiService.apiService.getUserDetails("Bearer " + token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userResponse -> {
                            // Update UI with user data
                            tvName.setText(userResponse.getUser().getName());
                            tvEmail.setText(userResponse.getUser().getEmail());
                            // Load profile image using Glide
                            Glide.with(this)
                                    .load(userResponse.getUser().getPicture())
                                    .into(imgProfile);
                        },
                        throwable -> {
                            // Handle error
                            Log.e("AccountFragment", "Error fetching user data", throwable);
                        }
                );
    }

    private void logout() {
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
