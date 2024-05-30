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
import com.example.projectfinaltth.data.model.response.courseIntro.Course;
import com.example.projectfinaltth.ui.adapter.HomeCourseAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    private RecyclerView popularView;
    private HomeCourseAdapter homeCourseAdapter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private List<String> myCourses = new ArrayList<>(); // Danh sách các courseId trong "My Courses"
    private List<Course> allCourses = new ArrayList<>(); // Danh sách tất cả các khóa học
    private Set<String> topics = new HashSet<>(); // Tập hợp các chủ đề duy nhất

    private TextView textView71, textView72, textView73, textView7, textView81, textView82, textView83, textView8;
    private EditText editTextText;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        popularView = view.findViewById(R.id.popularView);

        popularView.setLayoutManager(new GridLayoutManager(getContext(), 1));

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

        ImageView imageView61 = view.findViewById(R.id.imageView61);
        ImageView imageView62 = view.findViewById(R.id.imageView62);
        ImageView imageView63 = view.findViewById(R.id.imageView63);
        ImageView imageView6 = view.findViewById(R.id.imageView6);
        ImageView imageView71 = view.findViewById(R.id.imageView71);
        ImageView imageView72 = view.findViewById(R.id.imageView72);
        ImageView imageView73 = view.findViewById(R.id.imageView73);
        ImageView imageView8 = view.findViewById(R.id.imageView8);

        textView71.setOnClickListener(v -> fetchCoursesWithSearch(1, textView71.getText().toString()));
        textView72.setOnClickListener(v -> fetchCoursesWithSearch(1, textView72.getText().toString()));
        textView73.setOnClickListener(v -> fetchCoursesWithSearch(1, textView73.getText().toString()));
        textView7.setOnClickListener(v -> fetchCoursesWithSearch(1, textView7.getText().toString()));
        textView81.setOnClickListener(v -> fetchCoursesWithSearch(1, textView81.getText().toString()));
        textView82.setOnClickListener(v -> fetchCoursesWithSearch(1, textView82.getText().toString()));
        textView83.setOnClickListener(v -> fetchCoursesWithSearch(1, textView83.getText().toString()));

        imageView8.setOnClickListener(v -> fetchCoursesWithSearch(1, "ALL"));
//        textView8.setOnClickListener(v -> fetchCoursesWithSearch(1, "ALL"));


        imageView61.setOnClickListener(v -> fetchCoursesWithSearch(1, textView71.getText().toString()));
        imageView62.setOnClickListener(v -> fetchCoursesWithSearch(1, textView72.getText().toString()));
        imageView63.setOnClickListener(v -> fetchCoursesWithSearch(1, textView73.getText().toString()));
        imageView6.setOnClickListener(v -> fetchCoursesWithSearch(1, textView7.getText().toString()));
        imageView71.setOnClickListener(v -> fetchCoursesWithSearch(1, textView81.getText().toString()));
        imageView72.setOnClickListener(v -> fetchCoursesWithSearch(1, textView82.getText().toString()));
        imageView73.setOnClickListener(v -> fetchCoursesWithSearch(1, textView83.getText().toString()));


        fetchCoursesWithSearch(1, "ALL"); 
        fetchMyCourses();

        // Thêm sự kiện cho EditText để tìm kiếm
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

        // Thêm sự kiện cho searchButton để tìm kiếm
        searchButton.setOnClickListener(v -> {
            String query = editTextText.getText().toString().trim();
            if (!query.isEmpty()) {
                searchCoursesByName(query);
            }
        });

        return view;
    }


    private void fetchCoursesWithSearch(int currentPage, String topic) {
        RequestBody requestBody = new RequestBody("", topic, currentPage, "newest");

        compositeDisposable.add(
                ApiService.apiService.searchCourses(requestBody)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(courseResponse -> {
                            if (currentPage == 1) {
                                allCourses.clear();
                            }
                            allCourses.addAll(courseResponse.getCourses());
                            for (Course course : courseResponse.getCourses()) {
                                topics.add(course.getTopic());
                            }

                            if (courseResponse.getCourses().size() > 0) {
                                fetchCoursesWithSearch(currentPage + 1, topic);
                            } else {
                                // Khi đã lấy hết tất cả các trang, cập nhật adapter
                                homeCourseAdapter = new HomeCourseAdapter(allCourses, HomeFragment.this);
                                popularView.setAdapter(homeCourseAdapter);
                                displayTopics();
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

    private void displayTopics() {
        List<String> topicList = new ArrayList<>(topics);
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
        RequestBody requestBody = new RequestBody(query, "ALL", 1, "newest");

        compositeDisposable.add(
                ApiService.apiService.searchCourses(requestBody)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(courseResponse -> {
                            Log.d("HomeFragment", "API Response: " + courseResponse);

                            if (courseResponse != null && courseResponse.getCourses() != null && !courseResponse.getCourses().isEmpty()) {
                                List<Course> filteredCourses = new ArrayList<>();
                                for (Course course : courseResponse.getCourses()) {
                                    if (course.getTitle().toLowerCase().contains(query.toLowerCase())) {
                                        filteredCourses.add(course);
                                    }
                                }

                                Log.d("HomeFragment", "Filtered Courses: " + filteredCourses);

                                if (filteredCourses.isEmpty()) {
                                    Toast.makeText(getContext(), "No courses found", Toast.LENGTH_SHORT).show();
                                } else {
                                    allCourses.clear();
                                    allCourses.addAll(filteredCourses);

                                    if (homeCourseAdapter == null) {
                                        homeCourseAdapter = new HomeCourseAdapter(allCourses, HomeFragment.this);
                                        popularView.setAdapter(homeCourseAdapter);
                                    } else {
                                        homeCourseAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                Toast.makeText(getContext(), "No courses found", Toast.LENGTH_SHORT).show();
                            }
                        }, throwable -> {
                            throwable.printStackTrace();
                            Toast.makeText(getContext(), "Failed to fetch courses", Toast.LENGTH_SHORT).show();
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
                            // Kiểm tra lỗi và hiển thị thông báo phù hợp
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
        compositeDisposable.clear();
    }
}
