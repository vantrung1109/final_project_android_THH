package com.example.projectfinaltth.ui.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectfinaltth.ui.model_temp.CourseDomain;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.ui.adapter.HomeCourseAdapter;

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

        itemsArrayList.add(new CourseDomain("Quick Learn c# language Quick Learn c# language Quick Learn c# language Quick Learn c# language", "Tu Thanh Hoai", 130, 4.8, "https://th.bing.com/th/id/R.0a61c37308bd4a412127451bcfe03791?rik=I0ANrsshoR8FGQ&riu=http%3a%2f%2fgiffiles.alphacoders.com%2f251%2f2512.gif&ehk=KVmLvRNTZV9bCgB8k%2fbh1X4PyTp12GRxypR3FR9ZouQ%3d&risl=&pid=ImgRaw&r=0"));
        itemsArrayList.add(new CourseDomain("Full Course android kotlin", "Alex Alba", 450, 4.6, "https://th.bing.com/th/id/R.24d1f95cee70adbacb9f7c686d4f641b?rik=367XfQV5yovNAA&pid=ImgRaw&r=0"));
        itemsArrayList.add(new CourseDomain("Quick Learn Java Language", "Sara Anderson", 700, 4.3, "https://th.bing.com/th/id/R.7428d2f2d94a2e9ff971466fd448c83b?rik=A92F0l7%2fN7JekQ&pid=ImgRaw&r=0"));
        itemsArrayList.add(new CourseDomain("Quick Learn Java Language", "Sara Anderson", 700, 4.3, "https://media1.giphy.com/media/3oKIPaqnUbU4nmLE5y/giphy.gif"));
        itemsArrayList.add(new CourseDomain("Full Course android kotlin", "Alex Alba", 450, 4.6, "https://th.bing.com/th/id/R.aeb4ee17fec09faec0cd25b3e9565f05?rik=rhVsrxZOIIGiZw&pid=ImgRaw&r=0"));
        itemsArrayList.add(new CourseDomain("Full Course android kotlin", "Alex Alba", 450, 4.6, "https://th.bing.com/th/id/R.c6a29414967d94161bf8864f8199dc78?rik=OTPLg09V6raxyQ&pid=ImgRaw&r=0"));
        itemsArrayList.add(new CourseDomain("Quick Learn Java Language", "Sara Anderson", 700, 4.3, "https://media.tenor.com/lWFGk4q4CcAAAAAd/kaneki.gif"));
        itemsArrayList.add(new CourseDomain("Full Course android kotlin", "Alex Alba", 450, 4.6, "https://i.makeagif.com/media/7-20-2022/1kk3-f.gif"));

        // Sử dụng GridLayoutManager với số cột là 1 để các mục hiển thị theo chiều ngang
        popularView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        popularView.setAdapter(new HomeCourseAdapter(itemsArrayList));
    }
}
