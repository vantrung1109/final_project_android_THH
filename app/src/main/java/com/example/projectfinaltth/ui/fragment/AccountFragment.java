package com.example.projectfinaltth.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projectfinaltth.R;

public class AccountFragment extends Fragment {

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
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_fragment_account, container, false);
    }
}