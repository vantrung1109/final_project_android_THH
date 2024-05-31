package com.example.projectfinaltth.ui.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.model.response.courseIntro.Course;
import com.example.projectfinaltth.ui.courseIntro.CourseIntroActivity;
import com.example.projectfinaltth.ui.fragment.HomeFragment;

import java.util.List;

public class HomeCourseAdapter extends RecyclerView.Adapter<HomeCourseAdapter.ViewHolder> {

    private List<Course> items;
    private HomeFragment homeFragment;

    public HomeCourseAdapter(List<Course> items, HomeFragment homeFragment) {

        this.items = items;
        this.homeFragment = homeFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_course, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = items.get(position);
        holder.titleTxt.setText(course.getTitle());
        holder.ownerTxt.setText(course.getInstructorName());
        holder.priceTxt.setText("$" + course.getPrice());

        Glide.with(holder.itemView.getContext())
                .load(course.getPicture())
                .into(holder.pic);

        holder.addToCartButton.setOnClickListener(v -> {
            homeFragment.addToCart(course.get_id());
        });

        holder.viewIntroButton.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), CourseIntroActivity.class);
            intent.putExtra("course_id", course.get_id());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, ownerTxt, priceTxt;
        ImageView pic;
        ImageButton addToCartButton;
        Button viewIntroButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            ownerTxt = itemView.findViewById(R.id.ownerTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            pic = itemView.findViewById(R.id.pic);
            addToCartButton = itemView.findViewById(R.id.buttonAddToCart);
            viewIntroButton = itemView.findViewById(R.id.buttonViewIntro);
        }
    }
}
