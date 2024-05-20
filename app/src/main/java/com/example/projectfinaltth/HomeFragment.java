package com.example.projectfinaltth;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView popularView;
    private ArrayList<CourseDomain> itemsArrayList;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initRecyclerView(view);
        return view;
    }

    private void initRecyclerView(View view) {
        popularView = view.findViewById(R.id.popularView);
        itemsArrayList = new ArrayList<>();

        itemsArrayList.add(new CourseDomain("Quick Learn c# language", "Jammie young", 130, 4.8, "pic1"));
        itemsArrayList.add(new CourseDomain("Full Course android kotlin", "Alex Alba", 450, 4.6, "pic2"));
        itemsArrayList.add(new CourseDomain("Quick Learn Java Language", "Sara Anderson", 700, 4.3, "pic1"));
        itemsArrayList.add(new CourseDomain("Quick Learn Java Language", "Sara Anderson", 700, 4.3, "pic1"));
        itemsArrayList.add(new CourseDomain("Full Course android kotlin", "Alex Alba", 450, 4.6, "pic2"));
        itemsArrayList.add(new CourseDomain("Full Course android kotlin", "Alex Alba", 450, 4.6, "pic2"));
        itemsArrayList.add(new CourseDomain("Quick Learn Java Language", "Sara Anderson", 700, 4.3, "pic1"));
        itemsArrayList.add(new CourseDomain("Full Course android kotlin", "Alex Alba", 450, 4.6, "pic2"));
        popularView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularView.setAdapter(new HomeCourseAdapter(itemsArrayList));
    }
}
