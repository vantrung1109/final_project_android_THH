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
import com.example.projectfinaltth.databinding.FragmentAccountBinding;
import com.example.projectfinaltth.ui.ai.RegisterFaceAI;
import com.example.projectfinaltth.ui.profile.ProfileActivity;
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

public class AccountFragment extends Fragment {

    private TextView tvName, tvEmail; // TextView để hiển thị tên và email người dùng
    private ImageView imgProfile; // ImageView để hiển thị ảnh đại diện người dùng
    private RelativeLayout layoutLogout; // Layout cho phần logout
    FragmentAccountBinding mFragmentAccountBinding; // Binding cho fragment
    CompositeDisposable compositeDisposable = new CompositeDisposable(); // Quản lý các disposable để tránh rò rỉ bộ nhớ

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
        fetchUserData(); // Tải lại dữ liệu người dùng khi Fragment được resume
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout cho Fragment và liên kết với binding
        mFragmentAccountBinding = FragmentAccountBinding.inflate(inflater, container, false);

        // Thiết lập sự kiện click cho layoutLogOut để thực hiện logout
        mFragmentAccountBinding.layoutLogOut.setOnClickListener(v -> {
            logout();
        });

        // Thiết lập sự kiện click cho buttonChangeAvatar để chọn và cập nhật ảnh đại diện
        mFragmentAccountBinding.buttonChangeAvatar.setOnClickListener(v -> {
            ImagePicker.Companion.with(AccountFragment.this).crop().compress(512).maxResultSize(200, 200).start();
        });

        // Thiết lập sự kiện click cho layoutChangePassword để mở ChangePasswordActivity
        mFragmentAccountBinding.layoutChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        // Thiết lập sự kiện click cho layoutChangeName để mở EditProfileActivity
        mFragmentAccountBinding.layoutChangeName.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        // Thiết lập sự kiện click cho layoutRegisterFaceAI để mở RegisterFaceAI activity
        mFragmentAccountBinding.layoutRegisterFaceAI.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RegisterFaceAI.class);
            Bundle bundle = new Bundle();
            bundle.putString("email", mFragmentAccountBinding.tvEmail.getText().toString());
            bundle.putString("name", mFragmentAccountBinding.tvName.getText().toString());
            intent.putExtras(bundle);
            startActivity(intent);
        });

        return mFragmentAccountBinding.getRoot(); // Trả về view của Fragment
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
                                    mFragmentAccountBinding.tvName.setText(userResponse.getUser().getName());
                                    mFragmentAccountBinding.tvEmail.setText(userResponse.getUser().getEmail());

                                    // Load ảnh đại diện sử dụng Glide
                                    Glide.with(this.getActivity())
                                            .load(userResponse.getUser().getPicture())
                                            .into(mFragmentAccountBinding.imgProfile);
                                },
                                throwable -> {
                                    // Xử lý lỗi khi tải dữ liệu người dùng
                                    Log.e("AccountFragment", "Error fetching user data", throwable);
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

                        String token = DataLocalManager.getToken(); // Lấy token từ local storage
                        compositeDisposable.add(
                                ApiService.apiService.updateProfilePicture("Bearer " + token, imagePart)
                                        .subscribeOn(Schedulers.io()) // Chạy yêu cầu trên luồng I/O
                                        .observeOn(AndroidSchedulers.mainThread()) // Quan sát kết quả trên luồng chính
                                        .subscribe(
                                                response -> {
                                                    // Cập nhật thành công, hiển thị ảnh đại diện mới
                                                    Glide.with(AccountFragment.this)
                                                            .load(uri)
                                                            .into(mFragmentAccountBinding.imgProfile);
                                                    Log.d("AccountFragment", "Update profile picture success");
                                                    Toast.makeText(AccountFragment.this.getContext(), "Update profile picture success", Toast.LENGTH_SHORT).show();
                                                },
                                                throwable -> {
                                                    // Xử lý lỗi khi cập nhật ảnh đại diện
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
                dialog.show(); // Hiển thị hộp thoại
            }
        }
    }
}
