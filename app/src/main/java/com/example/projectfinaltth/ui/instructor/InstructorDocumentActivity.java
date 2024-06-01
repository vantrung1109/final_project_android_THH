package com.example.projectfinaltth.ui.instructor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.comment.CommentRequest;
import com.example.projectfinaltth.data.model.request.document.DocumentRequest;
import com.example.projectfinaltth.data.model.response.comment.Comment;
import com.example.projectfinaltth.data.model.response.document.Document;
import com.example.projectfinaltth.ui.adapter.CommentDocumentAdapter;
import com.example.projectfinaltth.ui.adapter.InstructorDocumentAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class InstructorDocumentActivity extends AppCompatActivity {

    private RecyclerView documentsRecyclerView;
    private RecyclerView commentsRecyclerView;
    private InstructorDocumentAdapter documentAdapter;
    private CommentDocumentAdapter commentAdapter;
    private List<Document> documentList;
    private List<Comment> commentList;
    ImageView btnBack;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    String lessonId;
    // 21110194 - Đặng Xuân Hùng

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_document);
        // Khởi tạo RecyclerView và LayoutManager
        documentsRecyclerView = findViewById(R.id.rcv_documents);
        commentsRecyclerView = findViewById(R.id.rcv_comments);

        documentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Khởi tạo danh sách và adapter cho tài liệu và bình luận
        documentList = new ArrayList<>();
        commentList = new ArrayList<>();

        documentAdapter = new InstructorDocumentAdapter(this, documentList, new InstructorDocumentAdapter.OnItemInteractionListener() {
            @Override
            public void onDeleteLesson(int position, Document document) {
                // Xử lý sự kiện xóa tài liệu
                deleteDocumentItem(position, document);
            }
        });
        // Thiết lập adapter cho RecyclerView
        commentAdapter = new CommentDocumentAdapter(this, commentList);

        documentsRecyclerView.setAdapter(documentAdapter);
        commentsRecyclerView.setAdapter(commentAdapter);

        // Lấy lessonId từ Intent hoặc các nguồn khác
        lessonId = getIntent().getStringExtra("lessonId");
        Log.e("", lessonId);
        if (lessonId != null && !lessonId.isEmpty()) {
            // Tải tài liệu và bình luận của bài học
            loadLessonDocuments(lessonId);
            loadLessonComments(lessonId);
        } else {
            Log.e("InstructorDocument", "Lesson ID is null or empty");
        }
        // Thiết lập sự kiện click cho nút quay lại
        btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(v -> {
            finish();
        });
        // Thiết lập sự kiện click cho nút thêm tài liệu mới
        TextView btnAddDocument = findViewById(R.id.btn_create_document);

        btnAddDocument.setOnClickListener(v -> {
            // Gửi dữ liệu cần thiết sang activity mới
             Intent intent = new Intent(this, CreateDocumentActivity.class);
             intent.putExtra("lessonId", lessonId); // Chuyển ID của bài học
             startActivityForResult(intent, 1);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Tải lại danh sách tài liệu sau khi thêm mới
            loadLessonDocuments(lessonId);
        }
    }


    // Phương thức để tải các tài liệu của bài học
    private void loadLessonDocuments(String lessonId) {
        compositeDisposable.add(
                ApiService.apiService.getDocumentByLesson(new DocumentRequest(lessonId))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(documentResponse -> {
                            Log.e("", documentResponse.getDocuments().toString());
                            if (documentResponse != null && documentResponse.getDocuments() != null) {
                                documentList.clear();
                                documentList.addAll(documentResponse.getDocuments());
                                documentAdapter.notifyDataSetChanged();
                            } else {
                                Log.e("InstructorDocument", "Document response is null or has no items");
                            }
                        }, throwable -> {
                            Log.e("InstructorDocument", "Error loading documents: " + throwable.getMessage());
                            Snackbar.make(documentsRecyclerView, "Error loading documents: " + throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
                        })
        );
    }
    // Phương thức để tải các bình luận của bài học
    private void loadLessonComments(String lessonId) {
        compositeDisposable.add(
                ApiService.apiService.getLessonComments(new CommentRequest(lessonId))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(commentResponse -> {
                            Log.e("Comments", commentResponse.getComments().toString());
                            if (commentResponse != null && commentResponse.getComments() != null) {
                                commentList.clear();
                                commentList.addAll(commentResponse.getComments());
                                commentAdapter.notifyDataSetChanged();
                            } else {
                                Log.e("InstructorDocument", "Comment response is null or has no items");
                            }
                        }, throwable -> {
                            Log.e("InstructorDocument", "Error loading comments: " + throwable.getMessage());
                            Snackbar.make(commentsRecyclerView, "Error loading comments: " + throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
                        })
        );
    }
    // Phương thức để xóa một tài liệu
    private void deleteDocumentItem(int position, Document document) {
        String token = DataLocalManager.getToken(); // Lấy token từ local storage

        // Xóa mục khỏi giao diện ngay lập tức
        documentList.remove(position);
        documentAdapter.notifyItemRemoved(position);
        documentAdapter.notifyItemRangeChanged(position, documentList.size());

        if (token != null) {
            compositeDisposable.add(
                    ApiService.apiService.deleteDocument("Bearer " + token, document.get_id())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> Snackbar.make(documentsRecyclerView, "Delete successful", Snackbar.LENGTH_SHORT).show(),
                                    throwable -> {
                                        Log.e("InstructorDocument", "Error deleting document: " + throwable.getMessage());
                                        Snackbar.make(documentsRecyclerView, "Error deleting document: " + throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                            )
            );
        } else {
            Log.e("InstructorDocument", "Token is null");
            Snackbar.make(documentsRecyclerView, "Token is null", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
