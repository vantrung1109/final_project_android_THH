package com.example.projectfinaltth.ui.mlkit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.response.profile.UserResponse;
import com.example.projectfinaltth.ui.main.MainActivity;
import com.example.projectfinaltth.ui.main.MainInstructorActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FaceMlKitCameraActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private Bitmap profileBitmap; // To hold the user's profile image bitmap
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_mlkit_camera);

        // Đảm bảo DataLocalManager được khởi tạo
        DataLocalManager.init(this);

        previewView = findViewById(R.id.previewView);
        Button captureButton = findViewById(R.id.captureButton);
        Button verifyButton = findViewById(R.id.verifyButton);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        captureButton.setOnClickListener(v -> takePhoto());
        verifyButton.setOnClickListener(v -> verifyFace());

        // Lấy và tải ảnh profile
        loadProfileImage();
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                int rotation = previewView.getDisplay().getRotation();

                Preview preview = new Preview.Builder()
                        .setTargetRotation(rotation)
                        .build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setTargetRotation(rotation)
                        .build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

                adjustPreviewOrientation(previewView, rotation);

            } catch (ExecutionException | InterruptedException e) {
                Log.e("FaceMlKitCameraActivity", "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void adjustPreviewOrientation(PreviewView previewView, int rotation) {
        Matrix matrix = new Matrix();
        switch (rotation) {
            case Surface.ROTATION_0:
                matrix.postRotate(180, previewView.getWidth() / 2f, previewView.getHeight() / 2f);
                break;
            case Surface.ROTATION_90:
                matrix.postRotate(270, previewView.getWidth() / 2f, previewView.getHeight() / 2f);
                break;
            case Surface.ROTATION_180:
                matrix.postRotate(0, previewView.getWidth() / 2f, previewView.getHeight() / 2f);
                break;
            case Surface.ROTATION_270:
                matrix.postRotate(90, previewView.getWidth() / 2f, previewView.getHeight() / 2f);
                break;
            default:
                matrix.postRotate(180, previewView.getWidth() / 2f, previewView.getHeight() / 2f);
                break;
        }
        previewView.setScaleType(PreviewView.ScaleType.FILL_CENTER);
        previewView.getOverlay().clear();
        previewView.getOverlay().add(new RotatedOverlay(previewView.getContext(), matrix));
    }

    public static class RotatedOverlay extends View {
        private final Matrix matrix;

        public RotatedOverlay(Context context, Matrix matrix) {
            super(context);
            this.matrix = matrix;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.concat(matrix);
            super.onDraw(canvas);
        }
    }

    private void loadProfileImage() {
        String profileImageUrl = DataLocalManager.getProfilePicture();
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Glide.with(this)
                    .asBitmap()
                    .load(profileImageUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            profileBitmap = resource;
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            Log.e("FaceMlKitCameraActivity", "Failed to load profile image");
                            Toast.makeText(FaceMlKitCameraActivity.this, "Failed to load profile image.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Profile image not loaded.", Toast.LENGTH_SHORT).show();
        }
    }

    private void takePhoto() {
        if (imageCapture != null) {
            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
                    new File(getFilesDir(), "temp.jpg")).build();

            imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    InputImage image;
                    try {
                        image = InputImage.fromFilePath(FaceMlKitCameraActivity.this, Uri.fromFile(new File(getFilesDir(), "temp.jpg")));
                        detectAndCompareFace(image);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(FaceMlKitCameraActivity.this, "Failed to load image.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    Toast.makeText(FaceMlKitCameraActivity.this, "Photo capture failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void verifyFace() {
        if (imageCapture != null) {
            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
                    new File(getFilesDir(), "temp.jpg")).build();

            imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    InputImage image;
                    try {
                        image = InputImage.fromFilePath(FaceMlKitCameraActivity.this, Uri.fromFile(new File(getFilesDir(), "temp.jpg")));
                        detectAndCompareFace(image);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(FaceMlKitCameraActivity.this, "Failed to load image.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    Toast.makeText(FaceMlKitCameraActivity.this, "Photo capture failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void detectAndCompareFace(InputImage image) {
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();

        FaceDetector detector = FaceDetection.getClient(options);

        detector.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                    @Override
                    public void onSuccess(List<Face> faces) {
                        if (!faces.isEmpty()) {
                            Face capturedFace = faces.get(0);
                            if (profileBitmap != null) {
                                InputImage profileImage = InputImage.fromBitmap(profileBitmap, 0);
                                compareFaces(profileImage, capturedFace);
                            } else {
                                Toast.makeText(FaceMlKitCameraActivity.this, "Profile image not loaded.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(FaceMlKitCameraActivity.this, "No face detected.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FaceMlKitCameraActivity.this, "Failed to detect face.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void compareFaces(InputImage profileImage, Face capturedFace) {
        boolean facesMatch = compareFaceBitmaps(profileBitmap, capturedFace);

        if (facesMatch) {
            String token = DataLocalManager.getToken();
            Intent intent;
            if ("INSTRUCTOR".equalsIgnoreCase(DataLocalManager.getRole())) {
                intent = new Intent(FaceMlKitCameraActivity.this, MainInstructorActivity.class);
            } else {
                intent = new Intent(FaceMlKitCameraActivity.this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(FaceMlKitCameraActivity.this, "Face does not match.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean compareFaceBitmaps(Bitmap profileBitmap, Face capturedFace) {
        // Placeholder logic for face comparison
        // Implement the actual comparison algorithm here
        return true; // Return true if faces match, false otherwise
    }
}
