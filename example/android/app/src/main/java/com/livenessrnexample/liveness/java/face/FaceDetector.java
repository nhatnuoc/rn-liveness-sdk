package com.livenessrnexample.liveness.java.face;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.GuardedBy;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FaceDetector {

    private static final String TAG = "FaceDetector";
    private static final float MIN_FACE_SIZE = 0.15F;

    private final com.google.mlkit.vision.face.FaceDetector mlKitFaceDetector;
    private OnFaceDetectionResultListener onFaceDetectionResultListener;
    private ExecutorService faceDetectionExecutor;
    private final Handler mainExecutor = new Handler(Looper.getMainLooper());
    private final Object lock = new Object();

    @GuardedBy("lock")
    private boolean isProcessing = false;
    private boolean isSmiled = false;
    private FaceBoundsOverlay faceBoundsOverlay;

    public FaceDetector(FaceBoundsOverlay faceBoundsOverlay) {
        this.faceBoundsOverlay = faceBoundsOverlay;
        mlKitFaceDetector = FaceDetection.getClient(
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .setMinFaceSize(MIN_FACE_SIZE)
                        .enableTracking()
                        .build()
        );

        faceBoundsOverlay.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                faceDetectionExecutor = Executors.newSingleThreadExecutor();
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                if (faceDetectionExecutor != null) {
                    faceDetectionExecutor.shutdown();
                }
            }
        });
    }

    public void setOnFaceDetectionResultListener(OnFaceDetectionResultListener listener) {
        this.onFaceDetectionResultListener = listener;
    }

    public void process(Frame frame) {
        synchronized (lock) {
            if (!isProcessing) {
                isProcessing = true;
                if (faceDetectionExecutor == null) {
                    IllegalStateException exception = new IllegalStateException(
                            "Cannot run face detection. Make sure the face " +
                                    "bounds overlay is attached to the current window."
                    );
                    onError(exception);
                } else {
                    faceDetectionExecutor.execute(() -> detectFaces(frame));
                }
            }
        }
    }

    public byte[] toByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public Bitmap toBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    private void detectFaces(Frame frame) {
        byte[] dataImage = frame.getData();
        if (dataImage == null) return;

        InputImage inputImage = InputImage.fromByteArray(dataImage, frame.getSize().getWidth(), frame.getSize().getHeight(), frame.getRotation(), frame.getFormat());
        mlKitFaceDetector.process(inputImage)
                .addOnSuccessListener(faces -> {
                    synchronized (lock) {
                        isProcessing = false;
                    }
                    if (faces.size() > 0) {
                        for (Face face : faces) {
//                            if (face.getSmilingProbability() != null) {
//                                float smile = face.getSmilingProbability() != null ? face.getSmilingProbability() : 0.0f;
//                                if (smile > 0.95) {
//                                    if (onFaceDetectionResultListener != null) {
//                                        onFaceDetectionResultListener.onSuccess(face, faces.size());
//                                    }
//                                    isProcessing = true;
//                                } else {
////                                    List<FaceBounds> faceBounds = faces.stream().map(f -> toFaceBounds(f, frame)).collect(Collectors.toList());
//                                    // mainExecutor.execute(() -> faceBoundsOverlay.updateFaces(faceBounds));
//                                }
//                            }
                            if (isSmiled) {
                                if (face.getLeftEyeOpenProbability() != null && face.getRightEyeOpenProbability() != null) {
                                    if (checkEyeBlink(face)) {
                                        onFaceDetectionResultListener.onSuccess(face, faces.size());
                                        isSmiled = false;
                                        onFaceDetectionResultListener.onProcessing(false);
                                        isProcessing = true;
                                    }
                                }
                            } else {
                                if (face.getSmilingProbability() != null) { //&& checkFaceFrame(face)
                                    float smile = face.getSmilingProbability() != null ? face.getSmilingProbability() : 0.0f;
                                    if (smile > 0.95) {
                                        isSmiled = true;
                                        onFaceDetectionResultListener.onProcessing(true);
                                    }
                                }
                            }
                        }
                    } else {
                        // List<FaceBounds> faceBounds = faces.stream().map(f -> toFaceBounds(f, frame)).toList();
                        // mainExecutor.execute(() -> faceBoundsOverlay.updateFaces(faceBounds));
                    }
                })
                .addOnFailureListener(exception -> {
                    synchronized (lock) {
                        isProcessing = false;
                    }
                    onError(exception);
                });
    }

    private FaceBounds toFaceBounds(Face face, Frame frame) {
        boolean reverseDimens = frame.getRotation() == 90 || frame.getRotation() == 270;
        int width = reverseDimens ? frame.getSize().getHeight() : frame.getSize().getWidth();
        int height = reverseDimens ? frame.getSize().getWidth() : frame.getSize().getHeight();
        float scaleX = (float) faceBoundsOverlay.getWidth() / width;
        float scaleY = (float) faceBoundsOverlay.getHeight() / height;

        boolean isFrontLens = frame.getLensFacing() == LensFacing.FRONT;
        float flippedLeft = isFrontLens ? width - face.getBoundingBox().right : face.getBoundingBox().left;
        float flippedRight = isFrontLens ? width - face.getBoundingBox().left : face.getBoundingBox().right;

        float scaledLeft = scaleX * flippedLeft;
        float scaledTop = scaleY * face.getBoundingBox().top;
        float scaledRight = scaleX * flippedRight;
        float scaledBottom = scaleY * face.getBoundingBox().bottom;
        RectF scaledBoundingBox = new RectF(scaledLeft, scaledTop, scaledRight, scaledBottom);

        return new FaceBounds(face.getTrackingId(), scaledBoundingBox);
    }

    private void onError(Exception exception) {
        if (onFaceDetectionResultListener != null) {
            onFaceDetectionResultListener.onFailure(exception);
        }
        Log.e(TAG, "An error occurred while running a face detection", exception);
    }

    public interface OnFaceDetectionResultListener {
        void onSuccess(Face faceBounds, int faceSize);
        void onProcessing(Boolean isSmile);

        void onFailure(Exception exception);
    }
    private boolean checkEyeBlink(Face face) {
        float leftEyeOpenProbability = face.getLeftEyeOpenProbability() != null ? face.getLeftEyeOpenProbability() : 0f;
        float rightEyeOpenProbability = face.getRightEyeOpenProbability() != null ? face.getRightEyeOpenProbability() : 0f;
        Log.d("Thuytv", "-----left: " + leftEyeOpenProbability + " ---right: " + rightEyeOpenProbability);
        return leftEyeOpenProbability < 0.2f || rightEyeOpenProbability < 0.2f;
    }
}
