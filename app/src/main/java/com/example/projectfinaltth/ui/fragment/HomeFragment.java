package com.example.projectfinaltth.ui.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.model.response.courseIntro.CoursehomeResponse;
import com.example.projectfinaltth.ui.adapter.HomeCourseAdapter;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    private RecyclerView popularView;
    private HomeCourseAdapter homeCourseAdapter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        popularView = view.findViewById(R.id.popularView);


        popularView.setLayoutManager(new GridLayoutManager(getContext(), 1));


        fetchCourses();

        return view;
    }

    private void fetchCourses() {
        compositeDisposable.add(
                ApiService.apiService.getAllCourses()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(coursehomeResponse -> {
                            homeCourseAdapter = new HomeCourseAdapter(new ArrayList<>(coursehomeResponse.getCourses()));
                            popularView.setAdapter(homeCourseAdapter);
                        }, throwable -> {
                            throwable.printStackTrace();
                        })
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
