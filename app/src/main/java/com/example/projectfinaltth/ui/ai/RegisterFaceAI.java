package com.example.projectfinaltth.ui.ai;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
public class RegisterFaceAI extends AppCompatActivity {
    private static final String TAG = "RegisterFaceAI";
    private static final int PERMISSION_CODE = 1001;// Mã yêu cầu quyền truy cập camera
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;// Quyền truy cập camera
    private PreviewView previewView;// Xem trước camera
    private CameraSelector cameraSelector;// Lựa chọn camera (trước/sau)
    private ProcessCameraProvider cameraProvider;// Nhà cung cấp camera
    private int lensFacing = CameraSelector.LENS_FACING_BACK;// Mặt camera đang được sử dụng (mặt sau)
    private Preview previewUseCase;// Trường hợp sử dụng xem trước
    private ImageAnalysis analysisUseCase;// Trường hợp sử dụng phân tích hình ảnh
    private GraphicOverlay graphicOverlay;// Lớp phủ đồ họa để vẽ các hình ảnh
    private ImageView previewImg;// Hình ảnh xem trước
    private TextView detectionTextView;// TextView để hiển thị kết quả phát hiện khuôn mặt

    private final HashMap<String, SimilarityClassifier.Recognition> registered = new HashMap<>(); // Lưu các khuôn mặt đã đăng ky
    private Interpreter tfLite;// Trình thông dịch TensorFlow Lite
    private boolean flipX = false;// Lật ảnh theo trục X
    private boolean start = true;// Cờ để bắt đầu nhận diện
    private float[][] embeddings;// Ma trận nhúng của khuôn mặt

    private static final float IMAGE_MEAN = 128.0f;// Giá trị trung bình của ảnh
    private static final float IMAGE_STD = 128.0f;// Độ lệch chuẩn của ảnh
    private static final int INPUT_SIZE = 112;// Kích thước đầu vào của ảnh
    private static final int OUTPUT_SIZE=192;// Kích thước đầu ra của nhúng
    // 21110194 - Đặng Xuân Hùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_face);

        previewView = findViewById(R.id.previewView);
        previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);
        graphicOverlay = findViewById(R.id.graphic_overlay);
        previewImg = findViewById(R.id.preview_img);
        detectionTextView = findViewById(R.id.detection_text);

        ImageButton addBtn = findViewById(R.id.add_btn);
        addBtn.setOnClickListener((v -> addFace()));

        ImageButton switchCamBtn = findViewById(R.id.switch_camera);
        switchCamBtn.setOnClickListener((view -> switchCamera()));

        loadModel(); // Tải mô hình TensorFlow Lite
        loadRegisteredFaces(); // Tải các khuôn mặt đã đăng ký từ SharedPreferences
    }
    // Phương thức tải các khuôn mặt đã đăng ký từ SharedPreferences
    private void loadRegisteredFaces() {
        SharedPreferences sharedPreferences = getSharedPreferences("FaceRecognition", MODE_PRIVATE);
        String json = sharedPreferences.getString("registered_faces", null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, Map<String, Object>>>() {}.getType();
            HashMap<String, Map<String, Object>> tempMap = gson.fromJson(json, type);

            if (tempMap != null) {
                registered.clear();
                for (Map.Entry<String, Map<String, Object>> entry : tempMap.entrySet()) {
                    String name = entry.getKey();
                    Map<String, Object> map = entry.getValue();
                    String id = (String) map.get("id");
                    String title = (String) map.get("title");
                    float distance = ((Number) map.get("distance")).floatValue(); // Chuyển đổi từ Number sang float
                    List<List<Double>> list = (List<List<Double>>) map.get("extra");

                    // Chuyển đổi List<List<Double>> về float[][]
                    float[][] embeddings = new float[list.size()][];
                    for (int i = 0; i < list.size(); i++) {
                        List<Double> innerList = list.get(i);
                        embeddings[i] = new float[innerList.size()];
                        for (int j = 0; j < innerList.size(); j++) {
                            embeddings[i][j] = innerList.get(j).floatValue(); // Chuyển đổi từ Double sang float
                        }
                    }

                    SimilarityClassifier.Recognition recognition = new SimilarityClassifier.Recognition(id, title, distance);
                    recognition.setExtra(embeddings);
                    registered.put(name, recognition);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCamera();// Bắt đầu camera khi resume
    }

    /** Phương thức xử lý quyền truy cập */
    private void getPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA_PERMISSION}, PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (requestCode == PERMISSION_CODE) {
            setupCamera(); // Cài đặt camera nếu quyền được cấp
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /** Phương thức khởi tạo và cài đặt camera */
    private void startCamera() {
        if(ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            setupCamera(); // Cài đặt camera nếu quyền đã được cấp
        } else {
            getPermissions(); // Yêu cầu quyền nếu chưa có
        }
    }
    // Cài đặt camera
    private void setupCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindAllCameraUseCases();// Gắn các trường hợp sử dụng của camera
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "cameraProviderFuture.addListener Error", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }
    // Gắn tất cả các trường hợp sử dụng của camera
    private void bindAllCameraUseCases() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
            bindPreviewUseCase(); // Gắn trường hợp sử dụng xem trước
            bindAnalysisUseCase(); // Gắn trường hợp sử dụng phân tích hình ảnh
        }
    }
    // Gắn trường hợp sử dụng xem trước
    private void bindPreviewUseCase() {
        if (cameraProvider == null) {
            return;
        }

        if (previewUseCase != null) {
            cameraProvider.unbind(previewUseCase);
        }

        Preview.Builder builder = new Preview.Builder();
        builder.setTargetAspectRatio(AspectRatio.RATIO_4_3);
        builder.setTargetRotation(getRotation());

        previewUseCase = builder.build();
        previewUseCase.setSurfaceProvider(previewView.getSurfaceProvider());

        try {
            cameraProvider
                    .bindToLifecycle(this, cameraSelector, previewUseCase);
        } catch (Exception e) {
            Log.e(TAG, "Error when bind preview", e);
        }
    }
    // Gắn trường hợp sử dụng phân tích hình ảnh
    private void bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return;
        }

        if (analysisUseCase != null) {
            cameraProvider.unbind(analysisUseCase);
        }

        Executor cameraExecutor = Executors.newSingleThreadExecutor();

        ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
        builder.setTargetAspectRatio(AspectRatio.RATIO_4_3);
        builder.setTargetRotation(getRotation());

        analysisUseCase = builder.build();
        analysisUseCase.setAnalyzer(cameraExecutor, this::analyze);// Gán bộ phân tích hình ảnh

        try {
            cameraProvider
                    .bindToLifecycle(this, cameraSelector, analysisUseCase);
        } catch (Exception e) {
            Log.e(TAG, "Error when bind analysis", e);
        }
    }
    // Lấy độ xoay của camera
    protected int getRotation() throws NullPointerException {
        return previewView.getDisplay().getRotation();
    }
    // Chuyển đổi giữa camera trước và sau
    private void switchCamera() {
        if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            lensFacing = CameraSelector.LENS_FACING_FRONT;
            flipX = true;
        } else {
            lensFacing = CameraSelector.LENS_FACING_BACK;
            flipX = false;
        }

        if(cameraProvider != null) cameraProvider.unbindAll();
        startCamera();
    }

    /** Bộ xử lý phát hiện khuôn mặt */
    @SuppressLint("UnsafeOptInUsageError")
    private void analyze(@NonNull ImageProxy image) {
        if (image.getImage() == null) return;

        InputImage inputImage = InputImage.fromMediaImage(
                image.getImage(),
                image.getImageInfo().getRotationDegrees()
        );

        FaceDetector faceDetector = FaceDetection.getClient();

        faceDetector.process(inputImage)
                .addOnSuccessListener(faces -> onSuccessListener(faces, inputImage))
                .addOnFailureListener(e -> Log.e(TAG, "Barcode process failure", e))
                .addOnCompleteListener(task -> image.close());
    }
    // Xử lý kết quả phát hiện khuôn mặt
    private void onSuccessListener(List<Face> faces, InputImage inputImage) {
        Rect boundingBox = null;
        String name = null;
        float scaleX = (float) previewView.getWidth() / (float) inputImage.getHeight();
        float scaleY = (float) previewView.getHeight() / (float) inputImage.getWidth();

        if(faces.size() > 0) {
            detectionTextView.setText(R.string.face_detected);
            // Lấy khuôn mặt đầu tiên được phát hiện
            Face face = faces.get(0);

            // Lấy khung bao quanh khuôn mặt
            boundingBox = face.getBoundingBox();

            // Chuyển đổi ảnh thành bitmap và cắt ảnh
            Bitmap bitmap = mediaImgToBmp(
                    inputImage.getMediaImage(),
                    inputImage.getRotationDegrees(),
                    boundingBox);

            if(start) name = recognizeImage(bitmap);
            if(name != null) detectionTextView.setText(name);
        }
        else {
            detectionTextView.setText(R.string.no_face_detected);
        }

        graphicOverlay.draw(boundingBox, scaleX, scaleY, name);
    }

    /** Bộ xử lý nhận diện khuôn mặt */
    private void addFace() {
        start = false;

        // Lấy email và mật khẩu từ local
        String email = DataLocalManager.getEmail();
        String password = DataLocalManager.getPassword();

        // Sử dụng email và mật khẩu lấy được
        String name = email + " " + password;

        // Assuming `embeddings` and `registered` are defined elsewhere in your class
        SimilarityClassifier.Recognition result = new SimilarityClassifier.Recognition("0", "", -1f);
        result.setExtra(embeddings);
        registered.put(name, result);

        saveRegisteredFaces(); // Lưu các khuôn mặt đã đăng ký

        start = true;
    }

    // Lưu các khuôn mặt đã đăng ký vào SharedPreferences
    private void saveRegisteredFaces() {
        SharedPreferences sharedPreferences = getSharedPreferences("FaceRecognition", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        HashMap<String, Map<String, Object>> tempMap = new HashMap<>();

        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : registered.entrySet()) {
            SimilarityClassifier.Recognition recognition = entry.getValue();
            Map<String, Object> map = new HashMap<>();
            map.put("id", recognition.getId());
            map.put("title", recognition.getTitle());
            map.put("distance", recognition.getDistance());
            map.put("extra", Arrays.asList((float[][]) recognition.getExtra()));  // Chuyển đổi float[][] thành List<float[]>
            tempMap.put(entry.getKey(), map);
        }

        String json = gson.toJson(tempMap);
        editor.putString("registered_faces", json);
        editor.apply();
    }
    // Nhận diện khuôn mặt từ bitmap
    public String recognizeImage(final Bitmap bitmap) {
        // Đặt ảnh vào preview
        previewImg.setImageBitmap(bitmap);

        // Tạo ByteBuffer để lưu trữ ảnh đã chuẩn hóa

        ByteBuffer imgData = ByteBuffer.allocateDirect(INPUT_SIZE * INPUT_SIZE * 3 * 4);

        imgData.order(ByteOrder.nativeOrder());

        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];

        // Lấy giá trị pixel từ Bitmap để chuẩn hóa
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        imgData.rewind();

        for (int i = 0; i < INPUT_SIZE; ++i) {
            for (int j = 0; j < INPUT_SIZE; ++j) {
                int pixelValue = intValues[i * INPUT_SIZE + j];
                imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
            }
        }
        // imgData là đầu vào cho mô hình
        Object[] inputArray = {imgData};

        Map<Integer, Object> outputMap = new HashMap<>();


        embeddings = new float[1][OUTPUT_SIZE]; // Đầu ra của mô hình sẽ được lưu trữ trong biến này

        outputMap.put(0, embeddings);

        tfLite.runForMultipleInputsOutputs(inputArray, outputMap); // Chạy mô hình



        float distance;

        // So sánh khuôn mặt mới với các khuôn mặt đã lưu
        if (registered.size() > 0) {

            final Pair<String, Float> nearest = findNearest(embeddings[0]); // Tìm khuôn mặt gần nhất


            if (nearest != null) {

                final String name = nearest.first;
                distance = nearest.second;
                if(distance<1.000f){ //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
                    return name; // Trả về username
                }
                else
                    return "unknown";
            }
        }

        return null;
    }

    //Compare Faces by distance between face embeddings
    private Pair<String, Float> findNearest(float[] emb) {

        Pair<String, Float> ret = null;
        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : registered.entrySet()) {

            final String name = entry.getKey();
            final float[] knownEmb = ((float[][]) entry.getValue().getExtra())[0];

            float distance = 0;
            for (int i = 0; i < emb.length; i++) {
                float diff = emb[i] - knownEmb[i];
                distance += diff*diff;
            }
            distance = (float) Math.sqrt(distance);
            if (ret == null || distance < ret.second) {
                ret = new Pair<>(name, distance);
            }
        }

        return ret;

    }

    /** Bộ chuyển đổi Bitmap */
    private Bitmap mediaImgToBmp(Image image, int rotation, Rect boundingBox) {
        // Chuyển đổi hình ảnh thành Bitmap
        Bitmap frame_bmp = toBitmap(image);

        // Điều chỉnh hướng của khuôn mặt
        Bitmap frame_bmp1 = rotateBitmap(frame_bmp, rotation, flipX);

        // Cắt vùng bao quanh từ toàn bộ Bitmap (ảnh)
        float padding = 0.0f;
        RectF adjustedBoundingBox = new RectF(
                boundingBox.left - padding,
                boundingBox.top - padding,
                boundingBox.right + padding,
                boundingBox.bottom + padding);
        Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, adjustedBoundingBox);

        // Thay đổi kích thước bitmap thành 112x112
        return getResizedBitmap(cropped_face);
    }
    // Thay đổi kích thước bitmap
    private Bitmap getResizedBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) 112) / width;
        float scaleHeight = ((float) 112) / height;
        // TẠO MỘT MA TRẬN ĐỂ MANIPULATE
        Matrix matrix = new Matrix();
        // THAY ĐỔI KÍCH THƯỚC BITMAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "TẠO LẠI" BITMAP MỚI
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
    // Cắt bitmap theo CPU
    private static Bitmap getCropBitmapByCPU(Bitmap source, RectF cropRectF) {
        Bitmap resultBitmap = Bitmap.createBitmap((int) cropRectF.width(),
                (int) cropRectF.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);

        // draw background
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setColor(Color.WHITE);
        canvas.drawRect(//from  w w  w. ja v  a  2s. c  om
                new RectF(0, 0, cropRectF.width(), cropRectF.height()),
                paint);

        Matrix matrix = new Matrix();
        matrix.postTranslate(-cropRectF.left, -cropRectF.top);

        canvas.drawBitmap(source, matrix, paint);

        if (source != null && !source.isRecycled()) {
            source.recycle();
        }

        return resultBitmap;
    }
    // Xoay bitmap theo góc xoay
    private static Bitmap rotateBitmap(
            Bitmap bitmap, int rotationDegrees, boolean flipX) {
        Matrix matrix = new Matrix();

        // Rotate the image back to straight.
        matrix.postRotate(rotationDegrees);

        // Mirror the image along the X or Y axis.
        matrix.postScale(flipX ? -1.0f : 1.0f, 1.0f);
        Bitmap rotatedBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        // Recycle the old bitmap if it has changed.
        if (rotatedBitmap != bitmap) {
            bitmap.recycle();
        }
        return rotatedBitmap;
    }
    // Chuyển đổi YUV_420_888 thành NV21
    private static byte[] YUV_420_888toNV21(Image image) {

        int width = image.getWidth();
        int height = image.getHeight();
        int ySize = width*height;
        int uvSize = width*height/4;

        byte[] nv21 = new byte[ySize + uvSize*2];

        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer(); // Y
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer(); // U
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer(); // V

        int rowStride = image.getPlanes()[0].getRowStride();
        assert(image.getPlanes()[0].getPixelStride() == 1);

        int pos = 0;

        if (rowStride == width) { // likely
            yBuffer.get(nv21, 0, ySize);
            pos += ySize;
        }
        else {
            long yBufferPos = -rowStride; // not an actual position
            for (; pos<ySize; pos+=width) {
                yBufferPos += rowStride;
                yBuffer.position((int) yBufferPos);
                yBuffer.get(nv21, pos, width);
            }
        }

        rowStride = image.getPlanes()[2].getRowStride();
        int pixelStride = image.getPlanes()[2].getPixelStride();

        assert(rowStride == image.getPlanes()[1].getRowStride());
        assert(pixelStride == image.getPlanes()[1].getPixelStride());

        if (pixelStride == 2 && rowStride == width && uBuffer.get(0) == vBuffer.get(1)) {
            // có thể mặt phẳng V và U trùng nhau như NV21, nghĩa là vBuffer[1] là alias của uBuffer[0]
            byte savePixel = vBuffer.get(1);
            try {
                vBuffer.put(1, (byte)~savePixel);
                if (uBuffer.get(0) == (byte)~savePixel) {
                    vBuffer.put(1, savePixel);
                    vBuffer.position(0);
                    uBuffer.position(0);
                    vBuffer.get(nv21, ySize, 1);
                    uBuffer.get(nv21, ySize + 1, uBuffer.remaining());

                    return nv21; // shortcut
                }
            }
            catch (ReadOnlyBufferException ex) {
                // không may, chúng tôi không thể kiểm tra nếu vBuffer và uBuffer trùng nhau
            }

            // không may, kiểm tra thất bại. Chúng ta phải lưu U và V pixel theo pixel
            vBuffer.put(1, savePixel);
        }

        // các tối ưu khác có thể kiểm tra nếu (pixelStride == 1) hoặc (pixelStride == 2),
        // nhưng hiệu suất tăng sẽ ít đáng kể hơn

        for (int row=0; row<height/2; row++) {
            for (int col=0; col<width/2; col++) {
                int vuPos = col*pixelStride + row*rowStride;
                nv21[pos++] = vBuffer.get(vuPos);
                nv21[pos++] = uBuffer.get(vuPos);
            }
        }

        return nv21;
    }
    // Chuyển đổi hình ảnh thành Bitmap

    private Bitmap toBitmap(Image image) {

        byte[] nv21=YUV_420_888toNV21(image);


        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    /** Tải mô hình TensorFlow Lite */
    @SuppressWarnings("deprecation")
    private void loadModel() {
        try {
            //model name
            String modelFile = "mobile_face_net.tflite";
            tfLite = new Interpreter(loadModelFile(RegisterFaceAI.this, modelFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Tải tệp mô hình từ tài nguyên
    private MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}