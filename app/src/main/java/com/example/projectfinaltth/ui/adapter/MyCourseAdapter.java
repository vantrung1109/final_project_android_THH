package com.example.projectfinaltth.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.model.response.courseIntro.Course;
import com.example.projectfinaltth.data.model.response.courseIntro.CourseIntroResponse;
import com.example.projectfinaltth.ui.courseDetail.CourseDetailActivity;
import com.example.projectfinaltth.ui.review.ReviewActivity;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyCourseAdapter extends RecyclerView.Adapter<MyCourseAdapter.CourseViewHolder> {
    private Context context;
    private List<Course> courseList;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MyCourseAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.titleTxt.setText(course.getTitle());
        holder.authorTxt.setText(course.getInstructorName());

        // Sử dụng Glide để tải ảnh từ URL
        Glide.with(context)
                .load(course.getPicture())
                .into(holder.pic);

        // Thêm sự kiện click cho nút "View Detail"
        holder.viewDetailBtn.setOnClickListener(v -> {
            ApiService.apiService.getCourseIntroById(course.get_id())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        Intent intent = new Intent(context, CourseDetailActivity.class);
                        intent.putExtra("courseIntro", response);
                        context.startActivity(intent);
                    }, throwable -> {
                        // Xử lý lỗi nếu cần
                    });
        });

        holder.btnReview.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReviewActivity.class);
            intent.putExtra("course_id", course.get_id());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView titleTxt, authorTxt;
        Button viewDetailBtn;

        Button btnReview;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.pic);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            authorTxt = itemView.findViewById(R.id.authorTxt);
            viewDetailBtn = itemView.findViewById(R.id.viewDetailBtn);
            btnReview  = itemView.findViewById(R.id.btn_review);
        }
    }
}
