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
//MSSV:21110826 Họ Và Tên: Từ Thanh Hoài
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context; // Context của ứng dụng
    private List<CartItem> cartItemList; // Danh sách các mục trong giỏ hàng
    private OnItemRemoveListener onItemRemoveListener; // Listener để xử lý sự kiện xóa mục khỏi giỏ hàng

    public CartAdapter(Context context, List<CartItem> cartItemList, OnItemRemoveListener onItemRemoveListener) {
        this.context = context; // Khởi tạo context
        this.cartItemList = cartItemList; // Khởi tạo danh sách mục giỏ hàng
        this.onItemRemoveListener = onItemRemoveListener; // Khởi tạo listener để xử lý sự kiện xóa
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho mỗi item trong RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_cart, parent, false);
        return new CartViewHolder(view); // Trả về ViewHolder mới
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        // Lấy mục giỏ hàng từ danh sách tại vị trí position
        CartItem cartItem = cartItemList.get(position);

        if (cartItem.getCourse() != null) {
            // Thiết lập các giá trị cho các view trong ViewHolder
            holder.titleTxt.setText(cartItem.getCourse().getTitle());
            holder.authorTxt.setText(cartItem.getCourse().getInstructorName());
            holder.feeEachItem.setText("$" + cartItem.getCourse().getPrice().toString());

            // Sử dụng Glide để tải hình ảnh của khóa học vào ImageView
            Glide.with(context)
                    .load(cartItem.getCourse().getPicture())
                    .into(holder.pic);
        }

        // Thiết lập sự kiện click cho nút xóa (trash button)
        holder.trashBtn.setOnClickListener(v -> onItemRemoveListener.onRemove(holder.getAdapterPosition(), cartItem));
    }

    @Override
    public int getItemCount() {
        return cartItemList.size(); // Trả về số lượng mục giỏ hàng trong danh sách
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView pic; // ImageView để hiển thị hình ảnh khóa học
        TextView titleTxt, feeEachItem, authorTxt; // TextView để hiển thị tiêu đề, giá và tên người dạy khóa học
        View trashBtn; // View để hiển thị nút xóa (trash button)

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            // Liên kết các view với ID trong layout
            pic = itemView.findViewById(R.id.pic);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            authorTxt = itemView.findViewById(R.id.authorTxt);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            trashBtn = itemView.findViewById(R.id.trashBtn);
        }
    }

    public interface OnItemRemoveListener {
        void onRemove(int position, CartItem cartItem); // Phương thức để xử lý sự kiện xóa mục giỏ hàng
    }
}
