package com.example.projectfinaltth.ui.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.AddToCartRequest;
import com.example.projectfinaltth.data.model.response.courseIntro.Course;
import com.example.projectfinaltth.ui.adapter.HomeCourseAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    private RecyclerView popularView;
    private HomeCourseAdapter homeCourseAdapter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private List<String> myCourses = new ArrayList<>(); // Danh sách các courseId trong "My Courses"
    private List<Course> allCourses = new ArrayList<>(); // Danh sách tất cả các khóa học

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        popularView = view.findViewById(R.id.popularView);

        popularView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        fetchCoursesWithSearch(1); // Bắt đầu từ trang 1
        fetchMyCourses();

        return view;
    }

    private void fetchCoursesWithSearch(int currentPage) {
        com.example.projectfinaltth.data.model.request.RequestBody requestBody =
                new com.example.projectfinaltth.data.model.request.RequestBody("", "ALL", currentPage, "newest");

        compositeDisposable.add(
                ApiService.apiService.searchCourses(requestBody)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(courseResponse -> {
                            allCourses.addAll(courseResponse.getCourses());
                            if (courseResponse.getCourses().size() > 0) {
                                // Nếu còn dữ liệu, tiếp tục lấy trang tiếp theo
                                fetchCoursesWithSearch(currentPage + 1);
                            } else {
                                // Khi đã lấy hết tất cả các trang, cập nhật adapter
                                homeCourseAdapter = new HomeCourseAdapter(allCourses, HomeFragment.this);
                                popularView.setAdapter(homeCourseAdapter);
                            }
                        }, throwable -> {
                            throwable.printStackTrace();
                        })
        );
    }

    private void fetchMyCourses() {
        String token = DataLocalManager.getToken();
        compositeDisposable.add(
                ApiService.apiService.getMyCourses("Bearer " + token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(myCoursesResponse -> {
                            for (Course course : myCoursesResponse.getCourses()) {
                                myCourses.add(course.get_id());
                            }
                        }, throwable -> {
                            throwable.printStackTrace();
                        })
        );
    }

    public void addToCart(String courseId) {
        if (myCourses.contains(courseId)) {
            Toast.makeText(getContext(), "You purchased this course", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = DataLocalManager.getToken();
        String cartId = DataLocalManager.getCartId();

        // Kiểm tra nếu cartId rỗng, thông báo lỗi
        if (cartId == null || cartId.isEmpty()) {
            Toast.makeText(getContext(), "Cart ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        AddToCartRequest request = new AddToCartRequest(courseId, cartId);

        compositeDisposable.add(
                ApiService.apiService.addToCart("Bearer " + token, request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(cartItemResponse -> {
                            Toast.makeText(getContext(), "Added to cart", Toast.LENGTH_SHORT).show();
                        }, throwable -> {
                            Toast.makeText(getContext(), "Failed to add to cart: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
