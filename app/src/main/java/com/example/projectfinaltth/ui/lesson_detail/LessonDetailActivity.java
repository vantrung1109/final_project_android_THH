package com.example.projectfinaltth.ui.lesson_detail;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.model.request.document.DocumentRequest;
import com.example.projectfinaltth.data.model.response.lesson.Lesson;
import com.example.projectfinaltth.databinding.ActivityCourseDetailBinding;
import com.example.projectfinaltth.databinding.ActivityLessonDetailBinding;
import com.example.projectfinaltth.databinding.ActivityTestVideoBinding;
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


    ActivityTestVideoBinding mActivityTestVideoBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityLessonDetailBinding = ActivityLessonDetailBinding.inflate(getLayoutInflater());
//        mActivityTestVideoBinding = ActivityTestVideoBinding.inflate(getLayoutInflater());
        setContentView(mActivityLessonDetailBinding.getRoot());

        Bundle bundle = getIntent().getExtras();
        Lesson lesson = null;
        if (bundle != null)
            lesson = (Lesson) bundle.getSerializable("lesson");
        else
            return;

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



    }


}
