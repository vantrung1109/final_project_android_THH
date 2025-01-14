package com.example.projectfinaltth.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.AddToCartRequest;
import com.example.projectfinaltth.data.model.request.RequestBody;
import com.example.projectfinaltth.data.model.response.cart.CartItem;
import com.example.projectfinaltth.data.model.response.courseIntro.Course;
import com.example.projectfinaltth.ui.adapter.HomeCourseAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
//MSSV:21110826 Họ Và Tên: Từ Thanh Hoài
public class HomeFragment extends Fragment {

    private RecyclerView popularView; // RecyclerView để hiển thị danh sách khóa học
    private HomeCourseAdapter homeCourseAdapter; // Adapter cho RecyclerView
    private CompositeDisposable compositeDisposable = new CompositeDisposable(); // Quản lý các disposable để tránh rò rỉ bộ nhớ
    private List<String> myCourses = new ArrayList<>(); // Danh sách các courseId trong "My Courses"
    private List<Course> allCourses = new ArrayList<>(); // Danh sách tất cả các khóa học
    private Set<String> topics = new HashSet<>(); // Tập hợp các chủ đề duy nhất
    private List<CartItem> cartItems = new ArrayList<>(); // Danh sách các mục trong giỏ hàng

    private TextView textView71, textView72, textView73, textView7, textView81, textView82, textView83, textView8; // TextView để hiển thị các chủ đề
    private EditText editTextText; // EditText để nhập từ khóa tìm kiếm

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false); // Inflate layout cho Fragment
        popularView = view.findViewById(R.id.popularView); // Gán RecyclerView từ layout

        popularView.setLayoutManager(new GridLayoutManager(getContext(), 1)); // Đặt layout cho RecyclerView

        // Liên kết các TextView và EditText từ layout
        textView71 = view.findViewById(R.id.textView71);
        textView72 = view.findViewById(R.id.textView72);
        textView73 = view.findViewById(R.id.textView73);
        textView7 = view.findViewById(R.id.textView7);
        textView81 = view.findViewById(R.id.textView81);
        textView82 = view.findViewById(R.id.textView82);
        textView83 = view.findViewById(R.id.textView83);
        textView8 = view.findViewById(R.id.textView8);
        editTextText = view.findViewById(R.id.editTextText);
        Button searchButton = view.findViewById(R.id.searchButton);

        // Liên kết các ImageView từ layout
        ImageView imageView61 = view.findViewById(R.id.imageView61);
        ImageView imageView62 = view.findViewById(R.id.imageView62);
        ImageView imageView63 = view.findViewById(R.id.imageView63);
        ImageView imageView6 = view.findViewById(R.id.imageView6);
        ImageView imageView71 = view.findViewById(R.id.imageView71);
        ImageView imageView72 = view.findViewById(R.id.imageView72);
        ImageView imageView73 = view.findViewById(R.id.imageView73);
        ImageView imageView8 = view.findViewById(R.id.imageView8);

        // Thiết lập sự kiện click cho TextView để tìm kiếm khóa học theo chủ đề
        textView71.setOnClickListener(v -> fetchCoursesWithSearch(1, textView71.getText().toString()));
        textView72.setOnClickListener(v -> fetchCoursesWithSearch(1, textView72.getText().toString()));
        textView73.setOnClickListener(v -> fetchCoursesWithSearch(1, textView73.getText().toString()));
        textView7.setOnClickListener(v -> fetchCoursesWithSearch(1, textView7.getText().toString()));
        textView81.setOnClickListener(v -> fetchCoursesWithSearch(1, textView81.getText().toString()));
        textView82.setOnClickListener(v -> fetchCoursesWithSearch(1, textView82.getText().toString()));
        textView83.setOnClickListener(v -> fetchCoursesWithSearch(1, textView83.getText().toString()));

        // Thiết lập sự kiện click cho ImageView để tìm kiếm khóa học theo chủ đề
        imageView8.setOnClickListener(v -> fetchCoursesWithSearch(1, "ALL"));
        imageView61.setOnClickListener(v -> fetchCoursesWithSearch(1, textView71.getText().toString()));
        imageView62.setOnClickListener(v -> fetchCoursesWithSearch(1, textView72.getText().toString()));
        imageView63.setOnClickListener(v -> fetchCoursesWithSearch(1, textView73.getText().toString()));
        imageView6.setOnClickListener(v -> fetchCoursesWithSearch(1, textView7.getText().toString()));
        imageView71.setOnClickListener(v -> fetchCoursesWithSearch(1, textView81.getText().toString()));
        imageView72.setOnClickListener(v -> fetchCoursesWithSearch(1, textView82.getText().toString()));
        imageView73.setOnClickListener(v -> fetchCoursesWithSearch(1, textView83.getText().toString()));

        fetchCoursesWithSearch(1, "ALL"); // Bắt đầu từ trang 1 và tất cả các khóa học
        fetchMyCourses(); // Lấy danh sách khóa học của tôi
        fetchCartItems(); // Lấy danh sách các mục trong giỏ hàng

        // Thêm sự kiện cho EditText để tìm kiếm khi nhấn Enter trên bàn phím
        editTextText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = editTextText.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchCoursesByName(query);
                }
                return true;
            }
            return false;
        });

        // Thêm sự kiện cho searchButton để tìm kiếm khi nhấn nút
        searchButton.setOnClickListener(v -> {
            String query = editTextText.getText().toString().trim();
            if (!query.isEmpty()) {
                searchCoursesByName(query);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchCartItems(); // Lấy danh sách các mục trong giỏ hàng khi Fragment được resume
    }

    private void fetchCoursesWithSearch(int currentPage, String topic) {
        RequestBody requestBody = new RequestBody("", topic, currentPage, "newest"); // Tạo RequestBody để tìm kiếm khóa học

        compositeDisposable.add(
                ApiService.apiService.searchCourses(requestBody)
                        .subscribeOn(Schedulers.io()) // Chạy yêu cầu trên luồng I/O
                        .observeOn(AndroidSchedulers.mainThread()) // Quan sát kết quả trên luồng chính
                        .subscribe(courseResponse -> {
                            if (currentPage == 1) {
                                allCourses.clear(); // Xóa danh sách khóa học nếu là trang đầu tiên
                            }
                            allCourses.addAll(courseResponse.getCourses());
                            for (Course course : courseResponse.getCourses()) {
                                topics.add(course.getTopic()); // Thêm các chủ đề vào tập hợp
                            }

                            if (courseResponse.getCourses().size() > 0) {
                                // Nếu còn dữ liệu, tiếp tục lấy trang tiếp theo
                                fetchCoursesWithSearch(currentPage + 1, topic);
                            } else {
                                // Khi đã lấy hết tất cả các trang, cập nhật adapter
                                homeCourseAdapter = new HomeCourseAdapter(allCourses, HomeFragment.this);
                                popularView.setAdapter(homeCourseAdapter);
                                displayTopics(); // Hiển thị các chủ đề
                            }
                        }, throwable -> {
                            throwable.printStackTrace(); // In lỗi ra console
                        })
        );
    }

    private void fetchMyCourses() {
        String token = DataLocalManager.getToken(); // Lấy token từ local storage
        compositeDisposable.add(
                ApiService.apiService.getMyCourses("Bearer " + token)
                        .subscribeOn(Schedulers.io()) // Chạy yêu cầu trên luồng I/O
                        .observeOn(AndroidSchedulers.mainThread()) // Quan sát kết quả trên luồng chính
                        .subscribe(myCoursesResponse -> {
                            for (Course course : myCoursesResponse.getCourses()) {
                                myCourses.add(course.get_id()); // Thêm courseId vào danh sách myCourses
                            }
                        }, throwable -> {
                            throwable.printStackTrace(); // In lỗi ra console
                        })
        );
    }

    private void fetchCartItems() {
        String token = DataLocalManager.getToken(); // Lấy token từ local storage
        compositeDisposable.add(
                ApiService.apiService.getCartItem("Bearer " + token)
                        .subscribeOn(Schedulers.io()) // Chạy yêu cầu trên luồng I/O
                        .observeOn(AndroidSchedulers.mainThread()) // Quan sát kết quả trên luồng chính
                        .subscribe(cartItemResponse -> {
                            if (cartItemResponse != null && cartItemResponse.getCartItems() != null) {
                                cartItems.clear();
                                cartItems.addAll(cartItemResponse.getCartItems()); // Thêm tất cả các mục giỏ hàng vào danh sách cartItems
                            }
                        }, throwable -> {
                            throwable.printStackTrace(); // In lỗi ra console
                        })
        );
    }

    private void displayTopics() {
        List<String> topicList = new ArrayList<>(topics); // Chuyển đổi Set topics thành List
        // Hiển thị các chủ đề lên các TextView tương ứng
        if (topicList.size() > 0) textView71.setText(topicList.get(0));
        if (topicList.size() > 1) textView72.setText(topicList.get(1));
        if (topicList.size() > 2) textView73.setText(topicList.get(2));
        if (topicList.size() > 3) textView7.setText(topicList.get(3));
        if (topicList.size() > 4) textView81.setText(topicList.get(4));
        if (topicList.size() > 5) textView82.setText(topicList.get(5));
        if (topicList.size() > 6) textView83.setText(topicList.get(6));
        if (topicList.size() > 7) textView8.setText(topicList.get(7));
    }

    private void searchCoursesByName(String query) {
        RequestBody requestBody = new RequestBody(query, "ALL", 1, "newest"); // Tạo RequestBody để tìm kiếm khóa học theo tên

        compositeDisposable.add(
                ApiService.apiService.searchCourses(requestBody)
                        .subscribeOn(Schedulers.io()) // Chạy yêu cầu trên luồng I/O
                        .observeOn(AndroidSchedulers.mainThread()) // Quan sát kết quả trên luồng chính
                        .subscribe(courseResponse -> {
                            Log.d("HomeFragment", "API Response: " + courseResponse); // Ghi nhật ký phản hồi API

                            if (courseResponse != null && courseResponse.getCourses() != null && !courseResponse.getCourses().isEmpty()) {
                                List<Course> filteredCourses = new ArrayList<>();
                                for (Course course : courseResponse.getCourses()) {
                                    if (course.getTitle().toLowerCase().contains(query.toLowerCase())) {
                                        filteredCourses.add(course); // Lọc các khóa học có chứa từ khóa
                                    }
                                }

                                Log.d("HomeFragment", "Filtered Courses: " + filteredCourses); // Ghi nhật ký khóa học đã lọc

                                if (filteredCourses.isEmpty()) {
                                    Toast.makeText(getContext(), "No courses found", Toast.LENGTH_SHORT).show(); // Thông báo khi không tìm thấy khóa học
                                } else {
                                    allCourses.clear();
                                    allCourses.addAll(filteredCourses); // Thêm các khóa học đã lọc vào danh sách allCourses

                                    if (homeCourseAdapter == null) {
                                        homeCourseAdapter = new HomeCourseAdapter(allCourses, HomeFragment.this);
                                        popularView.setAdapter(homeCourseAdapter); // Thiết lập adapter cho RecyclerView
                                    } else {
                                        homeCourseAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu cho adapter
                                    }
                                }
                            } else {
                                Toast.makeText(getContext(), "No courses found", Toast.LENGTH_SHORT).show(); // Thông báo khi không tìm thấy khóa học
                            }
                        }, throwable -> {
                            throwable.printStackTrace(); // In lỗi ra console
                            Toast.makeText(getContext(), "Failed to fetch courses", Toast.LENGTH_SHORT).show(); // Thông báo khi tìm kiếm thất bại
                        })
        );
    }

    public void addToCart(String courseId) {
        // Kiểm tra xem khóa học đã được mua chưa
        if (myCourses.contains(courseId)) {
            Toast.makeText(getContext(), "You purchased this course", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra xem khóa học đã có trong giỏ hàng chưa
        for (CartItem item : cartItems) {
            if (item.getCourseId().equals(courseId)) {
                Toast.makeText(getContext(), "This course is already in your cart.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String token = DataLocalManager.getToken();
        String cartId = DataLocalManager.getCartId();

        // Kiểm tra xem cartId có trống không
        if (cartId == null || cartId.isEmpty()) {
            Toast.makeText(getContext(), "Cart ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        AddToCartRequest request = new AddToCartRequest(courseId, cartId);

        compositeDisposable.add(
                ApiService.apiService.addToCart("Bearer " + token, request)
                        .subscribeOn(Schedulers.io()) // Chạy yêu cầu trên luồng I/O
                        .observeOn(AndroidSchedulers.mainThread()) // Quan sát kết quả trên luồng chính
                        .subscribe(cartItemResponse -> {
                            Toast.makeText(getContext(), "Add to cart", Toast.LENGTH_SHORT).show();
                            fetchCartItems(); // Làm mới danh sách giỏ hàng sau khi thêm một mục mới
                        }, throwable -> {
                            // Kiểm tra thông báo lỗi và hiển thị thông báo thích hợp
                            String errorMessage = throwable.getMessage();
                            if (errorMessage != null && errorMessage.contains("already in your cart")) {
                                Toast.makeText(getContext(), "This course is already in your cart.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to add to cart: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear(); // Xóa tất cả các disposable khi Fragment bị hủy
    }
}
