package com.example.projectfinaltth.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.response.profile.UserResponse;

import com.example.projectfinaltth.databinding.FragmentAccountBinding;
import com.example.projectfinaltth.ui.ai.RegisterFaceAI;
import com.example.projectfinaltth.ui.profile.ProfileActivity;
import com.example.projectfinaltth.ui.changepassword.ChangePasswordActivity;
import com.example.projectfinaltth.ui.profile.EditProfileActivity;

import com.example.projectfinaltth.ui.sign_in.SignInActivity;
import com.example.projectfinaltth.utils.RealPathUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {

    private TextView tvName, tvEmail;
    private ImageView imgProfile;

    private RelativeLayout layoutLogout;
    FragmentAccountBinding mFragmentAccountBinding;
    CompositeDisposable compositeDisposable = new CompositeDisposable();


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

        mFragmentAccountBinding = FragmentAccountBinding.inflate(inflater, container, false);

        mFragmentAccountBinding.layoutLogOut.setOnClickListener(v -> {
            logout();
        });


        mFragmentAccountBinding.buttonChangeAvatar.setOnClickListener(v -> {
            ImagePicker.Companion.with(AccountFragment.this).crop().compress(512).maxResultSize(200, 200).start();
        });


        mFragmentAccountBinding.layoutChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        mFragmentAccountBinding.layoutChangeName.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });
        mFragmentAccountBinding.register.setOnClickListener(v ->
        {
            Intent intent = new Intent(getActivity(), RegisterFaceAI.class);
            startActivity(intent);
        });
        return mFragmentAccountBinding.getRoot();

    }

    private void fetchUserData() {
        String token = DataLocalManager.getToken(); // Replace with actual token
        compositeDisposable.add(
        ApiService.apiService.getUserDetails("Bearer " + token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userResponse -> {
                            mFragmentAccountBinding.tvName.setText(userResponse.getUser().getName());
                            mFragmentAccountBinding.tvEmail.setText(userResponse.getUser().getEmail());

                            // Load profile image using Glide
                            Glide.with(this.getActivity())
                                    .load(userResponse.getUser().getPicture())
                                    .into(mFragmentAccountBinding.imgProfile);
                        },
                        throwable -> {
                            Log.e("AccountFragment", "Error fetching user data", throwable);
                        }
                ));
    }

    private void logout() {
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountFragment.this.getActivity());
                builder.setTitle("Update Profile Picture");
                builder.setMessage("Confirm update?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String realPath = RealPathUtil.getRealPath(AccountFragment.this.getContext(), uri);
                        File file = new File(realPath);

                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

                        String token = DataLocalManager.getToken(); // Replace with actual token
                        compositeDisposable.add(
                                ApiService.apiService.updateProfilePicture("Bearer " + token, imagePart)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                response -> {
                                                    // Handle success
                                                    Glide.with(AccountFragment.this)
                                                            .load(uri)
                                                            .into(mFragmentAccountBinding.imgProfile);
                                                    Log.d("AccountFragment", "Update profile picture success");
                                                    Toast.makeText(AccountFragment.this.getContext(), "Update profile picture success", Toast.LENGTH_SHORT).show();
                                                },
                                                throwable -> {
                                                    // Handle error
                                                    Log.e("AccountFragment", "Error updating profile picture", throwable);
                                                    Toast.makeText(AccountFragment.this.getContext(), "Error updating profile picture", Toast.LENGTH_SHORT).show();
                                                }
                                        )
                        );
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }
}
