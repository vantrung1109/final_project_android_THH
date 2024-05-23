package com.example.projectfinaltth;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartRecyclerView = view.findViewById(R.id.cartView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartItemList = new ArrayList<>();
        // Thêm các item vào cartItemList với thuộc tính author
        cartItemList.add(new CartItem(R.drawable.pic1, "Item 1", "Từ Thanh Hoài", "$10"));
        cartItemList.add(new CartItem(R.drawable.pic2, "Item 2", "Từ Thanh Hoài", "$15"));
        cartItemList.add(new CartItem(R.drawable.pic1, "Item 3", "Từ Thanh Hoài", "$20"));
        cartItemList.add(new CartItem(R.drawable.pic1, "Item 1", "Từ Thanh Hoài", "$10"));
        cartItemList.add(new CartItem(R.drawable.pic2, "Item 2", "Từ Thanh Hoài", "$15"));
        cartItemList.add(new CartItem(R.drawable.pic1, "Item 3", "Từ Thanh Hoài", "$20"));
        cartItemList.add(new CartItem(R.drawable.pic1, "Item 1", "Từ Thanh Hoài", "$10"));
        cartItemList.add(new CartItem(R.drawable.pic2, "Item 2", "Từ Thanh Hoài", "$15"));
        cartItemList.add(new CartItem(R.drawable.pic1, "Item 3", "Từ Thanh Hoài", "$20"));
        cartAdapter = new CartAdapter(cartItemList);
        cartRecyclerView.setAdapter(cartAdapter);

        return view;
    }
}
