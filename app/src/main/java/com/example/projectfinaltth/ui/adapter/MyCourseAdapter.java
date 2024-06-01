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
//MSSV:21110826 Họ Và Tên: Từ Thanh Hoài
public class MyCourseAdapter extends RecyclerView.Adapter<MyCourseAdapter.CourseViewHolder> {

    private Context context; // Context của ứng dụng
    private List<Course> courseList; // Danh sách các khóa học
    private CompositeDisposable compositeDisposable = new CompositeDisposable(); // Quản lý các disposable để tránh rò rỉ bộ nhớ

    public MyCourseAdapter(Context context, List<Course> courseList) {
        this.context = context; // Khởi tạo context
        this.courseList = courseList; // Khởi tạo danh sách khóa học
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho mỗi item trong RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.my_course, parent, false);
        return new CourseViewHolder(view); // Trả về ViewHolder mới
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        // Lấy khóa học từ danh sách tại vị trí position
        Course course = courseList.get(position);
        // Thiết lập các giá trị cho các view trong ViewHolder
        holder.titleTxt.setText(course.getTitle());
        holder.authorTxt.setText(course.getInstructorName());

        // Sử dụng Glide để tải hình ảnh của khóa học vào ImageView
        Glide.with(context)
                .load(course.getPicture())
                .into(holder.pic);

        // Thêm sự kiện click cho nút "View Detail"
        holder.viewDetailBtn.setOnClickListener(v -> {
            compositeDisposable.add(
                    ApiService.apiService.getCourseIntroById(course.get_id()) // Gọi API để lấy thông tin giới thiệu khóa học
                            .subscribeOn(Schedulers.io()) // Chạy yêu cầu trên luồng I/O
                            .observeOn(AndroidSchedulers.mainThread()) // Quan sát kết quả trên luồng chính
                            .subscribe(response -> {
                                Intent intent = new Intent(context, CourseDetailActivity.class); // Tạo intent để mở CourseDetailActivity
                                intent.putExtra("courseIntro", response); // Truyền đối tượng CourseIntroResponse vào intent
                                context.startActivity(intent); // Bắt đầu activity
                            }, throwable -> {
                                // Xử lý lỗi nếu cần
                            }));
        });

        // Thêm sự kiện click cho nút "Review"
        holder.buttonReview.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReviewActivity.class); // Tạo intent để mở ReviewActivity
            intent.putExtra("courseId", course.get_id()); // Truyền courseId vào intent
            context.startActivity(intent); // Bắt đầu activity
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size(); // Trả về số lượng khóa học trong danh sách
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView pic; // ImageView để hiển thị hình ảnh khóa học
        TextView titleTxt, authorTxt; // TextView để hiển thị tiêu đề và tên người dạy khóa học
        Button viewDetailBtn; // Button để xem chi tiết khóa học
        Button buttonReview; // Button để viết đánh giá khóa học

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            // Liên kết các view với ID trong layout
            pic = itemView.findViewById(R.id.pic);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            authorTxt = itemView.findViewById(R.id.authorTxt);
            viewDetailBtn = itemView.findViewById(R.id.viewDetailBtn);
            buttonReview = itemView.findViewById(R.id.btn_review);
        }
    }
}
