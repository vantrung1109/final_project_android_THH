package com.example.projectfinaltth;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItemList;

    public CartAdapter(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        holder.pic.setImageResource(cartItem.getImageResource());
        holder.titleTxt.setText(cartItem.getTitle());
        holder.authorTxt.setText(cartItem.getAuthor()); // Set giá trị cho authorTxt
        holder.feeEachItem.setText(cartItem.getFee());
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView titleTxt, feeEachItem, authorTxt;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.pic);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            authorTxt = itemView.findViewById(R.id.authorTxt); // Khởi tạo authorTxt
            feeEachItem = itemView.findViewById(R.id.feeEachItem);

            // Thêm kiểm tra null để tránh lỗi NullPointerException
            if (pic == null) {
                throw new NullPointerException("ImageView 'pic' not found in itemView");
            }
            if (titleTxt == null) {
                throw new NullPointerException("TextView 'titleTxt' not found in itemView");
            }
            if (authorTxt == null) {
                throw new NullPointerException("TextView 'authorTxt' not found in itemView");
            }
            if (feeEachItem == null) {
                throw new NullPointerException("TextView 'feeEachItem' not found in itemView");
            }
        }
    }
}
