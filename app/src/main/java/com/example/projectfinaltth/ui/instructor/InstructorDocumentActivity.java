package com.example.projectfinaltth.ui.instructor;

import android.os.Bundle;
import android.util.Log;

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

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_document);

        documentsRecyclerView = findViewById(R.id.rcv_documents);
        commentsRecyclerView = findViewById(R.id.rcv_comments);

        documentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        documentList = new ArrayList<>();
        commentList = new ArrayList<>();

        documentAdapter = new InstructorDocumentAdapter(this, documentList, new InstructorDocumentAdapter.OnItemInteractionListener() {
            @Override
            public void onDeleteLesson(int position, Document document) {
                deleteDocumentItem(position, document);
            }
        });

        commentAdapter = new CommentDocumentAdapter(this, commentList);

        documentsRecyclerView.setAdapter(documentAdapter);
        commentsRecyclerView.setAdapter(commentAdapter);

        // Get lessonId from Intent or other sources
        String lessonId = getIntent().getStringExtra("lessonId");
        Log.e("", lessonId);
        if (lessonId != null && !lessonId.isEmpty()) {
            loadLessonDocuments(lessonId);
            loadLessonComments(lessonId);
        } else {
            Log.e("InstructorDocument", "Lesson ID is null or empty");
        }
    }

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

    private void deleteDocumentItem(int position, Document document) {
        String token = DataLocalManager.getToken(); // Get token from local storage

        // Remove item from UI immediately
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
