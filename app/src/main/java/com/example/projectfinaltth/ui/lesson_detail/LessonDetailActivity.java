package com.example.projectfinaltth.ui.lesson_detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.model.request.comment.CommentRequest;
import com.example.projectfinaltth.data.model.request.document.DocumentRequest;
import com.example.projectfinaltth.data.model.response.lesson.Lesson;
import com.example.projectfinaltth.databinding.ActivityCourseDetailBinding;
import com.example.projectfinaltth.databinding.ActivityLessonDetailBinding;

import com.example.projectfinaltth.ui.comment.CommentActivity;
import com.example.projectfinaltth.utils.DateConvertUtils;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

// MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
public class LessonDetailActivity extends AppCompatActivity  implements Player.Listener{
    ActivityLessonDetailBinding mActivityLessonDetailBinding;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    FlexibleAdapter mFlexibleAdapterDocuments, mFlexibleAdapterComments;

    Lesson lesson = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityLessonDetailBinding = ActivityLessonDetailBinding.inflate(getLayoutInflater());
//        mActivityTestVideoBinding = ActivityTestVideoBinding.inflate(getLayoutInflater());
        setContentView(mActivityLessonDetailBinding.getRoot());

        Bundle bundle = getIntent().getExtras();

        if (bundle != null)
            lesson = (Lesson) bundle.getSerializable("lesson");
        else
            return;

        // Set dữ liệu vào view của rcv Comments
        compositeDisposable.add(
                ApiService.apiService.getLessonComments(new CommentRequest(lesson.get_id()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                                    Log.e("LessonDetailActivity", "onCreate: " + response.getComments());
                                    mFlexibleAdapterComments = new FlexibleAdapter(response.getComments(), this);

                                    mActivityLessonDetailBinding.rcvComments.setAdapter(mFlexibleAdapterComments);
                                    mActivityLessonDetailBinding.rcvComments.setLayoutManager(new LinearLayoutManager(this));
                                }, throwable -> {
                                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                                }
                        ));

        // Set dữ liệu vào view của rcv Documents
        compositeDisposable.add(
                ApiService.apiService.getLessonDocuments(new DocumentRequest(lesson.get_id()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {

                            mFlexibleAdapterDocuments = new FlexibleAdapter(response.getDocuments(), this);
                            mActivityLessonDetailBinding.rcvDocuments.setAdapter(mFlexibleAdapterDocuments);
                            mActivityLessonDetailBinding.rcvDocuments.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                        }, throwable -> {
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                        }
        ));
        // Set dữ liệu vào view
        mActivityLessonDetailBinding.buttonBack.setOnClickListener(v -> {
            this.finish();
        });

        mActivityLessonDetailBinding.btnLeaveComment.setOnClickListener(v -> {
            // MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
            // Xử lý sự kiện khi người dùng click vào nút gửi bình luận
            Intent intent = new Intent(this, CommentActivity.class);
            Bundle bundle1 = new Bundle();
            bundle1.putSerializable("lesson", lesson);
            intent.putExtras(bundle1);
            startActivityForResult(intent, 1);

        });


    }

    // MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
    // Update lại dữ liệu sau khi người dùng thêm bình luận
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                compositeDisposable.add(
                        ApiService.apiService.getLessonComments(new CommentRequest(lesson.get_id()))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(response -> {
                                    // Set dữ liệu lại lên view
                                            mFlexibleAdapterComments.updateDataSet(response.getComments());
                                        }, throwable -> {
                                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                ));
            }
        }
    }
}
