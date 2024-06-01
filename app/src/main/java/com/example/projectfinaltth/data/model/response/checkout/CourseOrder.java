package com.example.projectfinaltth.data.model.response.checkout;

import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.response.courseIntro.Course;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseOrder extends AbstractFlexibleItem<CourseOrder.CourseOrderViewHolder> {
    String _id;
    String userId;
    String cloudinary;
    String title;
    String topic;
    String picture;
    String description;
    Double price;
    Boolean visibility;
    Boolean status;
    String createdAt;
    String updatedAt;
    Integer __v;
    String instructorName;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public CourseOrder(Course course){
        this._id = course.get_id();
        this.userId = course.getUserId();
        this.cloudinary = course.getCloudinary();
        this.title = course.getTitle();
        this.topic = course.getTopic();
        this.picture = course.getPicture();
        this.description = course.getDescription();
        this.price = course.getPrice();
        this.visibility = course.getVisibility();
        this.status = course.getStatus();
        this.createdAt = course.getCreatedAt();
        this.updatedAt = course.getUpdatedAt();
        this.__v = course.get__v();
        this.instructorName = course.getInstructorName();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rcv_cart_item;
    }

    @Override
    public CourseOrderViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new CourseOrderViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, CourseOrderViewHolder holder, int position, List<Object> payloads) {
        holder.tvNameCourse.setText(title);
        holder.tvPriceCourse.setText(price.toString());
        holder.tvInstructorName.setText(instructorName);
        Glide.with(holder.itemView).load(picture).into(holder.imgCourse);

    }

    public class CourseOrderViewHolder extends FlexibleViewHolder {
        ImageView imgCourse;
        TextView tvNameCourse, tvPriceCourse, tvInstructorName;
        ImageView btnRemove;
        public CourseOrderViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            imgCourse = view.findViewById(R.id.img_cart_item);
            tvNameCourse = view.findViewById(R.id.tv_name_course);
            tvPriceCourse = view.findViewById(R.id.tv_price_course);
            tvInstructorName = view.findViewById(R.id.tv_course_author);


        }
    }
}
