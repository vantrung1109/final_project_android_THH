package com.example.projectfinaltth.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.model.response.cart.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItemList;
    private OnItemRemoveListener onItemRemoveListener;

    public CartAdapter(Context context, List<CartItem> cartItemList, OnItemRemoveListener onItemRemoveListener) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.onItemRemoveListener = onItemRemoveListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);

        if (cartItem.getCourse() != null) {
            holder.titleTxt.setText(cartItem.getCourse().getTitle());
            holder.authorTxt.setText(cartItem.getCourse().getInstructorName());
            holder.feeEachItem.setText("$" + cartItem.getCourse().getPrice().toString());

            // Sử dụng Glide để tải ảnh từ URL
            Glide.with(context)
                    .load(cartItem.getCourse().getPicture())
                    .into(holder.pic);
        }

        holder.trashBtn.setOnClickListener(v -> onItemRemoveListener.onRemove(holder.getAdapterPosition(), cartItem));
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView titleTxt, feeEachItem, authorTxt;
        View trashBtn;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.pic);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            authorTxt = itemView.findViewById(R.id.authorTxt);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            trashBtn = itemView.findViewById(R.id.trashBtn);
        }
    }

    public interface OnItemRemoveListener {
        void onRemove(int position, CartItem cartItem);
    }
}
