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
import com.example.projectfinaltth.data.model.response.courseIntro.Course;

import java.text.DecimalFormat;
import java.util.List;

public class HomeCourseAdapter extends RecyclerView.Adapter<HomeCourseAdapter.ViewHolder> {
    private List<Course> items;
    private DecimalFormat formatter;
    private Context context;

    public HomeCourseAdapter(List<Course> items) {
        this.items = items;
        formatter = new DecimalFormat("###,###,###,###.##");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_course, parent, false);
        context = parent.getContext();
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titleTxt.setText(items.get(position).getTitle());
        holder.ownerTxt.setText(items.get(position).getInstructorName());
        holder.priceTxt.setText("$" + formatter.format(items.get(position).getPrice()));

        Glide.with(holder.itemView.getContext())
                .load(items.get(position).getPicture())
                .into(holder.pic);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, ownerTxt, priceTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            ownerTxt = itemView.findViewById(R.id.ownerTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}
