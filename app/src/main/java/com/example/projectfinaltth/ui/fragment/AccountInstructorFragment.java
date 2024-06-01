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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.databinding.FragmentAccountInstructorBinding;
import com.example.projectfinaltth.ui.ai.RegisterFaceAI;
import com.example.projectfinaltth.ui.changepassword.ChangePasswordActivity;
import com.example.projectfinaltth.ui.profile.EditProfileActivity;
import com.example.projectfinaltth.ui.sign_in.SignInActivity;
import com.example.projectfinaltth.utils.RealPathUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AccountInstructorFragment extends Fragment {

    private FragmentAccountInstructorBinding binding; // Binding cho fragment
    private CompositeDisposable compositeDisposable = new CompositeDisposable(); // Quản lý các disposable để tránh rò rỉ bộ nhớ

    public AccountInstructorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e("course", "AccountInstructor");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("course", "Reload AccountInstructor");
        fetchUserData(); // Tải lại dữ liệu người dùng khi Fragment được resume
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho Fragment sử dụng View Binding
        binding = FragmentAccountInstructorBinding.inflate(inflater, container, false);

        // Thiết lập sự kiện click cho layoutLogOut để thực hiện logout
        binding.layoutLogOut.setOnClickListener(v -> {
            logout();
        });

        // Thiết lập sự kiện click cho buttonChangeAvatar để chọn và cập nhật ảnh đại diện
        binding.buttonChangeAvatar.setOnClickListener(v -> {
            ImagePicker.Companion.with(AccountInstructorFragment.this)
                    .crop()
                    .compress(512)
                    .maxResultSize(200, 200)
                    .start();
        });

        // Thiết lập sự kiện click cho layoutChangePassword để mở ChangePasswordActivity
        binding.layoutChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        // Thiết lập sự kiện click cho layoutChangeName để mở EditProfileActivity
        binding.layoutChangeName.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        // Thiết lập sự kiện click cho layoutRegisterFaceAI để mở RegisterFaceAI activity
        binding.layoutRegisterFaceAI.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RegisterFaceAI.class);
            Bundle bundle = new Bundle();
            bundle.putString("email", binding.tvEmail.getText().toString());
            bundle.putString("name", binding.tvName.getText().toString());
            intent.putExtras(bundle);
            startActivity(intent);
        });

        return binding.getRoot(); // Trả về view của Fragment
    }
    //MSSV:21110826 Họ Và Tên: Từ Thanh Hoài
    private void fetchUserData() {
        String token = DataLocalManager.getToken(); // Lấy token từ local storage
        compositeDisposable.add(
                ApiService.apiService.getUserDetails("Bearer " + token)
                        .subscribeOn(Schedulers.io()) // Chạy yêu cầu trên luồng I/O
                        .observeOn(AndroidSchedulers.mainThread()) // Quan sát kết quả trên luồng chính
                        .subscribe(
                                userResponse -> {
                                    // Thiết lập dữ liệu người dùng vào các view
                                    binding.tvName.setText(userResponse.getUser().getName());
                                    binding.tvEmail.setText(userResponse.getUser().getEmail());

                                    // Load ảnh đại diện sử dụng Glide
                                    Glide.with(this.getActivity())
                                            .load(userResponse.getUser().getPicture())
                                            .into(binding.imgProfile);
                                },
                                throwable -> {
                                    // Xử lý lỗi khi tải dữ liệu người dùng
                                    Log.e("AccountInstructorFragment", "Error fetching user data", throwable);
                                }
                        ));
    }


    //MSSV:21110826 Họ Và Tên: Từ Thanh Hoài
    private void logout() {
        // Thực hiện logout và mở SignInActivity
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData(); // Lấy URI của ảnh đã chọn
            if (uri != null) {
                // Hiển thị hộp thoại xác nhận cập nhật ảnh đại diện
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountInstructorFragment.this.getActivity());
                builder.setTitle("Update Profile Picture");
                builder.setMessage("Confirm update?");
                builder.setPositiveButton("YES", (dialog, which) -> {

                    String realPath = RealPathUtil.getRealPath(AccountInstructorFragment.this.getContext(), uri);
                    File file = new File(realPath);

                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    MultipartBody.Part imagePart = MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

                    String token = DataLocalManager.getToken(); // Lấy token từ local storage
                    compositeDisposable.add(
                            ApiService.apiService.updateProfilePicture("Bearer " + token, imagePart)
                                    .subscribeOn(Schedulers.io()) // Chạy yêu cầu trên luồng I/O
                                    .observeOn(AndroidSchedulers.mainThread()) // Quan sát kết quả trên luồng chính
                                    .subscribe(
                                            response -> {
                                                // Cập nhật thành công, hiển thị ảnh đại diện mới
                                                Glide.with(AccountInstructorFragment.this)
                                                        .load(uri)
                                                        .into(binding.imgProfile);
                                                Log.d("AccountInstructorFragment", "Update profile picture success");
                                                Toast.makeText(AccountInstructorFragment.this.getContext(), "Update profile picture success", Toast.LENGTH_SHORT).show();
                                            },
                                            throwable -> {
                                                // Xử lý lỗi khi cập nhật ảnh đại diện
                                                Log.e("AccountInstructorFragment", "Error updating profile picture", throwable);
                                                Toast.makeText(AccountInstructorFragment.this.getContext(), "Error updating profile picture", Toast.LENGTH_SHORT).show();
                                            }
                                    )
                    );
                });
                builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());

                AlertDialog dialog = builder.create();
                dialog.show(); // Hiển thị hộp thoại
            }
        }
    }
}
