package com.example.projectfinaltth.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.response.cart.CartItem;
import com.example.projectfinaltth.databinding.FragmentCartBinding;
import com.example.projectfinaltth.ui.adapter.CartAdapter;
import com.example.projectfinaltth.ui.checkout.CheckoutActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
//MSSV:21110826 Họ Và Tên: Từ Thanh Hoài
public class CartFragment extends Fragment {

    private RecyclerView cartRecyclerView; // RecyclerView để hiển thị danh sách giỏ hàng
    private CartAdapter cartAdapter; // Adapter cho RecyclerView
    private List<CartItem> cartItemList; // Danh sách các mục trong giỏ hàng
    private CompositeDisposable compositeDisposable = new CompositeDisposable(); // Quản lý các disposable để tránh rò rỉ bộ nhớ
    private TextView totalTxt; // TextView để hiển thị tổng giá

    FragmentCartBinding mFragmentCartBinding; // Binding cho fragment

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e("course", "Cart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("course", "Reload Cart");
        loadCart(); // Tải lại giỏ hàng khi Fragment được resume
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho Fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        mFragmentCartBinding = FragmentCartBinding.inflate(inflater, container, false);

        // Liên kết RecyclerView từ layout
        cartRecyclerView = view.findViewById(R.id.cartView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Đặt layout cho RecyclerView

        cartItemList = new ArrayList<>(); // Khởi tạo danh sách mục giỏ hàng
        cartAdapter = new CartAdapter(getContext(), cartItemList, this::deleteCartItem); // Khởi tạo adapter với danh sách và listener xóa
        cartRecyclerView.setAdapter(cartAdapter); // Thiết lập adapter cho RecyclerView

        Button btnCheckout = view.findViewById(R.id.btn_checkout); // Liên kết nút "Checkout" từ layout
        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CheckoutActivity.class); // Tạo intent để mở CheckoutActivity
            startActivity(intent); // Bắt đầu activity
        });

        // Liên kết TextView để hiển thị tổng giá
        totalTxt = view.findViewById(R.id.totalTxt);

        return view;
    }

    private void loadCart() {
        String token = DataLocalManager.getToken(); // Lấy token từ local storage
        if (token != null) {
            Log.d("CartFragment", "Token: " + token);
            compositeDisposable.add(
                    ApiService.apiService.getCartItem("Bearer " + token)
                            .subscribeOn(Schedulers.io()) // Chạy yêu cầu trên luồng I/O
                            .observeOn(AndroidSchedulers.mainThread()) // Quan sát kết quả trên luồng chính
                            .subscribe(cartItemResponse -> {
                                // Kiểm tra nếu phản hồi từ API không null và có dữ liệu
                                if (cartItemResponse != null && cartItemResponse.getCartItems() != null) {
                                    cartItemList.clear();
                                    cartItemList.addAll(cartItemResponse.getCartItems());
                                    loadCoursesForCartItems(cartItemList); // Tải thông tin chi tiết các khóa học trong giỏ hàng
                                } else {
                                    Log.e("CartFragment", "Cart response is null or has no items");
                                }
                            }, throwable -> {
                                Log.e("CartFragment", "Error loading cart: " + throwable.getMessage());
                            })
            );
        } else {
            Log.e("CartFragment", "Token is null");
        }
    }

    private void loadCoursesForCartItems(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            compositeDisposable.add(
                    ApiService.apiService.getCourseIntroById(cartItem.getCourseId())
                            .subscribeOn(Schedulers.io()) // Chạy yêu cầu trên luồng I/O
                            .observeOn(AndroidSchedulers.mainThread()) // Quan sát kết quả trên luồng chính
                            .subscribe(course -> {
                                cartItem.setCourse(course.getCourse()); // Thiết lập thông tin chi tiết cho mục giỏ hàng
                                cartAdapter.notifyDataSetChanged(); // Cập nhật adapter
                                updateTotalPrice(); // Cập nhật tổng giá
                            }, throwable -> {
                                Log.e("CartFragment", "Error loading course: " + throwable.getMessage());
                            })
            );
        }
    }

    private void updateTotalPrice() {
        double total = 0.0;
        for (CartItem item : cartItemList) {
            if (item.getCourse() != null) {
                total += item.getCourse().getPrice(); // Tính tổng giá
            }
        }
        totalTxt.setText("$" + total); // Hiển thị tổng giá
    }

    private void deleteCartItem(int position, CartItem cartItem) {
        String token = DataLocalManager.getToken(); // Lấy token từ local storage

        // Xóa mục khỏi giao diện ngay lập tức
        cartItemList.remove(position);
        cartAdapter.notifyItemRemoved(position);
        cartAdapter.notifyItemRangeChanged(position, cartItemList.size());
        updateTotalPrice();

        if (token != null) {
            Log.d("CartFragment", "Deleting item with cartId: " + cartItem.getCartId() + " and courseId: " + cartItem.getCourseId());
            compositeDisposable.add(
                    ApiService.apiService.removeFromCart("Bearer " + token, cartItem.getCartId(), cartItem.getCourseId())
                            .subscribeOn(Schedulers.io()) // Chạy yêu cầu trên luồng I/O
                            .observeOn(AndroidSchedulers.mainThread()) // Quan sát kết quả trên luồng chính
                            .subscribe(
                                    () -> {
                                        Snackbar.make(cartRecyclerView, "Delete successful", Snackbar.LENGTH_SHORT).show(); // Hiển thị thông báo thành công
                                    },
                                    throwable -> {
                                        Log.e("CartFragment", "Error removing item from cart: " + throwable.getMessage());
                                        Snackbar.make(cartRecyclerView, "Error removing item: " + throwable.getMessage(), Snackbar.LENGTH_SHORT).show(); // Hiển thị thông báo lỗi
                                    }
                            )
            );
        } else {
            Log.e("CartFragment", "Token is null");
            Snackbar.make(cartRecyclerView, "Token is null", Snackbar.LENGTH_SHORT).show(); // Hiển thị thông báo lỗi
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear(); // Xóa tất cả các disposable khi Fragment bị hủy
    }
}
