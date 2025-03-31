//package com.livenessrnexample.liveness.java;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//import android.util.Size;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.core.content.res.ResourcesCompat;
//import androidx.fragment.app.Fragment;
//
//import com.google.mlkit.vision.face.Face;
//import com.liveness.sdk.core.LiveNessSDK;
//import com.liveness.sdk.core.model.LivenessRequest;
//import com.liveness.sdk.core.utils.CallbackAPIListener;
//import com.otaliastudios.cameraview.CameraListener;
//import com.otaliastudios.cameraview.CameraOptions;
//import com.otaliastudios.cameraview.CameraView;
//import com.otaliastudios.cameraview.PictureResult;
//import com.otaliastudios.cameraview.VideoResult;
//import com.otaliastudios.cameraview.controls.Facing;
//import com.otaliastudios.cameraview.controls.Mode;
//import com.otaliastudios.cameraview.frame.Frame;
//import com.otaliastudios.cameraview.frame.FrameProcessor;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Random;
//
//import com.livenessrnexample.R;
//import com.livenessrnexample.liveness.java.face.FaceDetector;
//import com.livenessrnexample.liveness.java.face.LensFacing;
//import com.livenessrnexample.ui.MainActivity;
//
///**
// * Created by Thuytv on 24/05/2024.
// */
//public class FaceVideoJavaFragment extends Fragment {
//    private final int REQUEST_PERMISSION_CODE = 1231;
//
//    private String pathVideo = "";
//    private int bgColor = 0;
//    private boolean isCapture = false;
//    private final ArrayList<Integer> lstBgDefault = new ArrayList<>(Arrays.asList(R.drawable.img_0, R.drawable.img_1, R.drawable.img_2, R.drawable.img_3));
//
//    private final boolean isFirstVideo = true;
//    //    private String typeScreen = "";
//    private CameraView cameraViewVideo;
//    private Button btnCapture;
//    private ProgressBar prbLoading;
//    private ImageView bgFullScreenDefault;
//    private RelativeLayout llVideo;
//    private TextView vlStatusDetect;
//    private FrameLayout frameViewCustom;
//    private String mImgLiveNess = "";
//    private String mFaceImage = "";
//    private String mTransactionIdReadCard = "";
//
//    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.ui_main_face_video_java, container, false);
//        cameraViewVideo = view.findViewById(R.id.camera_view_video);
//        btnCapture = view.findViewById(R.id.btn_capture);
//        prbLoading = view.findViewById(R.id.prb_loading);
//        bgFullScreenDefault = view.findViewById(R.id.bg_full_screen_default);
//        llVideo = view.findViewById(R.id.ll_video);
//        frameViewCustom = view.findViewById(R.id.frame_view_custom);
//        vlStatusDetect = view.findViewById(R.id.vl_status_detect);
//
////        typeScreen = getArguments().getString("KEY_BUNDLE_SCREEN");
//        if (getArguments() != null) {
//            mTransactionIdReadCard = getArguments().getString(MainActivity.TRANSACTION_ID_READ_CARD);
//        }
//
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
//            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
//                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        }
//        // Set config for SDK
//        LiveNessSDK.Companion.setConfigSDK(requireContext(), getLivenessRequest());
//        initCamera(view);
//        if (checkPermissions()) {
//            cameraViewVideo.open();
//        } else {
//            requestPermissions();
//        }
////        if (typeScreen == "TYPE_SCREEN_REGISTER_FACE") {
////            btnCapture.setVisibility(View.VISIBLE);
////            btnCapture.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////                    cameraViewVideo.takePictureSnapshot();
////                }
////            });
////        } else {
//        btnCapture.setVisibility(View.GONE);
////        }
//        return view;
//    }
//
//    private boolean checkPermissions() {
//        int resultCamera = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA);
//        int resultRecord = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO);
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
//            int resultRead = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
//            int resultWrite = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            return (resultCamera == PackageManager.PERMISSION_GRANTED
//                    && resultRecord == PackageManager.PERMISSION_GRANTED
//                    && resultRead == PackageManager.PERMISSION_GRANTED
//                    && resultWrite == PackageManager.PERMISSION_GRANTED);
//
//        } else {
//            return resultCamera == PackageManager.PERMISSION_GRANTED && resultRecord == PackageManager.PERMISSION_GRANTED;
//        }
//    }
//
//    private void requestPermissions() {
//        ActivityCompat.requestPermissions(requireActivity(), permissions, REQUEST_PERMISSION_CODE);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_PERMISSION_CODE) {
//            if (checkPermissions()) {
//                cameraViewVideo.open();
//            } else {
//                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    private void initCamera(View view) {
//        File fileCache = new File(requireContext().getCacheDir(), "VideoLiveNess" + System.currentTimeMillis() + ".mp4");
//        pathVideo = fileCache.getAbsolutePath();
//        Facing lensFacing = Facing.FRONT;
//        setupCamera(lensFacing, view);
//    }
//
//    private void setupCamera(Facing lensFacing, View view) {
//        FaceDetector faceDetector = new FaceDetector(view.findViewById(R.id.faceBoundsOverlay));
//        faceDetector.setOnFaceDetectionResultListener(new FaceDetector.OnFaceDetectionResultListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//            }
//
//            @Override
//            public void onSuccess(@NonNull Face faceBounds, int faceSize) {
//                if (!isCapture) {
//                    isCapture = true;
////                    cameraViewVideo.stopVideo()
//                    bgFullScreenDefault.setVisibility(View.VISIBLE);
//                    llVideo.setVisibility(View.GONE);
//                    bgColor = new Random().nextInt(3);
//                    bgFullScreenDefault.setBackground(ResourcesCompat.getDrawable(getResources(), lstBgDefault.get(bgColor), requireContext().getTheme()));
//                    new Handler(Looper.myLooper()).postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            cameraViewVideo.takePictureSnapshot();
//                        }
//                    }, 100);
//                }
//            }
//
//            @Override
//            public void onProcessing(Boolean isSmile) {
//                if (isSmile) {
//                    vlStatusDetect.setText("Winking...");
//                } else {
//                    vlStatusDetect.setText("Smiling...");
//                }
//            }
//        });
//        cameraViewVideo.setFacing(lensFacing);
//        cameraViewVideo.setMode(Mode.VIDEO);
//        cameraViewVideo.addCameraListener(new CameraListener() {
//            @Override
//            public void onCameraOpened(@NonNull CameraOptions options) {
//                super.onCameraOpened(options);
////                if (!typeScreen.equals("TYPE_SCREEN_REGISTER_FACE")) {
//                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        cameraViewVideo.takeVideoSnapshot(new File(pathVideo));
//                    }
//                }, 500);
////                }
//            }
//
//            @Override
//            public void onPictureTaken(@NonNull PictureResult result) {
//                super.onPictureTaken(result);
////                if (!typeScreen.equals("TYPE_SCREEN_REGISTER_FACE")) {
//                result.getData();
//                mImgLiveNess = android.util.Base64.encodeToString(result.getData(), android.util.Base64.NO_PADDING);
//                callAPIGEtTOTP(mImgLiveNess, bgColor);
//                bgFullScreenDefault.setVisibility(View.GONE);
//                llVideo.setVisibility(View.VISIBLE);
//                isCapture = false;
//                cameraViewVideo.stopVideo();
////                } else {
////                    mFaceImage = android.util.Base64.encodeToString(result.getData(), android.util.Base64.NO_PADDING);
////                    registerDevice(mFaceImage);
////                }
//            }
//
//            @Override
//            public void onVideoTaken(@NonNull VideoResult result) {
//                super.onVideoTaken(result);
//            }
//        });
////        if (!typeScreen.equals("TYPE_SCREEN_REGISTER_FACE")) {
//        cameraViewVideo.addFrameProcessor(new FrameProcessor() {
//            @Override
//            public void process(@NonNull Frame frame) {
//                faceDetector.process(
//                        new com.livenessrnexample.liveness.java.face.Frame(
//                                frame.getData(),
//                                frame.getRotationToUser(),
//                                new Size(frame.getSize().getWidth(), frame.getSize().getHeight()),
//                                frame.getFormat(),
//                                LensFacing.FRONT
//                        )
//                );
//            }
//        });
////        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        cameraViewVideo.open();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        cameraViewVideo.close();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        cameraViewVideo.destroy();
//    }
//
//    private void initTransaction(String imgLiveNess, int bgColor) {
//        LiveNessSDK.Companion.initTransaction(requireContext(), null, new CallbackAPIListener() {
//            @Override
//            public void onCallbackResponse(@Nullable String data) {
//                JSONObject result = null;
//                try {
//                    if (data != null) {
//                        result = new JSONObject(data);
//                        int status = -1;
//                        if (result.has("status")) {
//                            status = result.getInt("status");
//                        }
//                        if (status == 200) {
//                            String transactionId = result.getString("data");
//                            checkLiveNessFlash(transactionId, imgLiveNess, bgColor);
//                        } else {
////                        showLoading(false)
//                            showToast("init Transaction Fail");
//                            onBackFragment();
//                        }
//                    } else {
//                        showToast("init Transaction Fail");
//                        onBackFragment();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//    }
//
//    private void checkLiveNessFlash(String transactionID, String imgLiveNess, Integer bgColor) {
//        HashMap<String, String> optionRequest = new HashMap();
//        optionRequest.put("clientTransactionId", mTransactionIdReadCard);
//        LivenessRequest request = new LivenessRequest();
//        request.setOptionRequest(optionRequest);
//        LiveNessSDK.Companion.checkLiveNessFlash(requireContext(), transactionID, imgLiveNess, bgColor, request,
//                new CallbackAPIListener() {
//
//                    @Override
//                    public void onCallbackResponse(@Nullable String data) {
//                        Log.d("Thuytv", "data:" + data);
//                        showToast(data);
//                        showLoading(false);
//                        onBackFragment();
//                    }
//                });
//    }
//
//    private void onBackFragment() {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (getActivity() != null) {
//                    getActivity().finish();
//                }
//            }
//        }, 1500);
//
//    }
//
//    private void callAPIGEtTOTP(String imgLiveNess, int bgColor) {
//        showLoading(true);
//        initTransaction(imgLiveNess, bgColor);
//    }
//
//    private void showToast(String strToast) {
//        if (getActivity() != null) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(requireContext(), strToast, Toast.LENGTH_SHORT).show();
//                    showLoading(false);
//                }
//            });
//        }
//    }
//
//    private void showLoading(boolean isShow) {
//        if (getActivity() != null) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (isShow) {
//                        prbLoading.setVisibility(View.VISIBLE);
//                    } else {
//                        prbLoading.setVisibility(View.GONE);
//                    }
//                }
//            });
//        }
//
//    }
//
//    private void registerDevice(String faceImage) {
//        showLoading(true);
//        LiveNessSDK.Companion.registerDevice(requireContext(), null, new CallbackAPIListener() {
//            @Override
//            public void onCallbackResponse(@Nullable String data) {
//                JSONObject result = null;
//                try {
//                    if (data != null) {
//                        result = new JSONObject(data);
//                        int status = -1;
//                        if (result.has("status")) {
//                            status = result.getInt("status");
//                        }
//                        if (status == 200) {
//                            registerFace(faceImage);
//                        } else {
//                            showToast("Register Device Fail");
//                            onBackFragment();
//                        }
//                    } else {
//                        showToast("Register Device Fail");
//                        onBackFragment();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    private void registerFace(String faceImage) {
////        showLoading(true)
//        LiveNessSDK.Companion.registerFace(requireContext(), faceImage, null, new CallbackAPIListener() {
//            @Override
//            public void onCallbackResponse(@Nullable String data) {
//                showLoading(false);
//                JSONObject result = null;
//                try {
//                    if (data != null) {
//                        result = new JSONObject(data);
//                        int status = -1;
//                        if (result.has("status")) {
//                            status = result.getInt("status");
//                        }
//                        if (status == 200) {
//                            showToast("Register Face Success");
//                            onBackFragment();
//                        } else {
//                            showToast("Register Face Fail");
//                            onBackFragment();
//                        }
//                    } else {
//                        showToast("Register Face Fail");
//                        onBackFragment();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//    }
//
//    private LivenessRequest getLivenessRequest() {
//        String privateKey = "-----BEGIN PRIVATE KEY-----\n" +
//                "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCiOMdedNfAhAdI\n" +
//                "M1YmUd2hheu2vDMmFHjCfWHon8wv0doubYPY6/uhMcUERpPiFddWqe+Dfr/XwCsa\n" +
//                "EaPOa27ghyUQ8HjdzAxcZ1yTWrgWttGruHlrHoXDPaov3QqvJTUrBclsH8p3ufPp\n" +
//                "gmBC0HrFD0Pl4+vEpki4VvCDJFEGuBaSAqFe7JqUuaOVRG9mBBZWslkNi8XNkAQT\n" +
//                "/Es+zReMf4EXIO2+wMo3aPIhe+sSZ3e3VqFL/10EJqNhurOT5ijUwReMlNb9wcxu\n" +
//                "drfSKjLOgW1n+ZLjo16GdS2ye68B7ZaA0J3DPuDdRXJ5YuoW4UQd8o6CyezIHWpP\n" +
//                "vH1tWFABAgMBAAECggEAB485yy8Kts/wPu8Vfqel+lbxSwyuHYIqtnV9UIfRzhCr\n" +
//                "aCp2UG9+xF47Xh2j2o9F/6XfoXMQoY808vwLdB0Rh6kEkyuBlmRh1xSB/ePmXDic\n" +
//                "wLHSBqnfdd+zxJM6YjsLpTuZzU4V80pZEXKf5b0tW22Arn/Whs1w6hYzEwloNTXf\n" +
//                "4K974i+st1E5/0JjufTBTOTlBtwbphwN9ia/Xs2EY3D6kuJhYZ5lCWDocD21xYWd\n" +
//                "NPM2CWqVXjJYEaqDTIWGwNGb2hkwNG5t/9MnN2On6BR7kgOWU4XxXHoLD3XoErwB\n" +
//                "M3J8QAXGZwb+wRtkzRCVgojA6AQXfu9/QyPjyHW4oQKBgQDYMEC+LuNtjrNju8yF\n" +
//                "LHMFbYbSfBQITE+kJn7iemezkwJw25NuKWl0pcxPe+NtpaHNFDmHnTVrlICTh90c\n" +
//                "qrtge1vsqtgEoaZfdYqkUVvl1jJWBJ+VqQNO2Nxos/6fM0ARDC/9YXHoDWKC4WeS\n" +
//                "PvYJ55MkMHseddpKIUGrZ1xO5QKBgQDAGGFxC9xWhG/CEm/JAFul+uyp9ncG6ro/\n" +
//                "47Tw75M5+2K9wsP2R2c0uoXZtQHFvvi9CADaQkSYrzY3wCqgjDhsR+3psN1R+Pkw\n" +
//                "bgMf3Rt6bMrYemPaGOe9qZ+Dpw/2GnLZfmCcJfKoRfY73YsxlL4/0Zf1va/qZnbp\n" +
//                "pGh4IlvO7QKBgD87teQq0Mi9wYi9aG/XdXkz9Qhh1HYs4+qOe/SAew6SRFeAUhoZ\n" +
//                "sMe2qxDgmr/6f139uWoKOJLT59u/FJSK962bx2JtAiwwn/ox5jBzv551TVnNlmPv\n" +
//                "AJGyap2RcDtegTG7T9ocA3YtXBAOH/4tvkddXbNrHsflDsk5+vxIij5lAoGAFli/\n" +
//                "vS7sCwSNG76ZUoDAKKbwMTWC00MrN5N90SmNrwkXi4vE0DmuP+wS9iigdCirNxJf\n" +
//                "RwS+hiSb4hBw5Qxq4+3aN31jwc18761cn7BRKgTN9DEIvK55Bw9chyxAJxkck0Co\n" +
//                "bIHdoMXCx2QWdUYge7weOXA/rr0MyFFf9dnJZGECgYEAuhJrRoxLdyouTd6X9+R1\n" +
//                "8FWY0XGfsBp+PkN/nnPuK6IJR/IeI+cdiorfm45l4ByF0XEBCDz2xXQ6MVBNz3zF\n" +
//                "MjEQ61dTFRfiTW2ZDqhMTtZH4R4T5NLWf+3ItjkAkOdStszplhHy0bUQIYgptYXd\n" +
//                "5Sw/UvMv83CmlztVC5tGG9o=\n" +
//                "-----END PRIVATE KEY-----";
//        String public_key = "-----BEGIN CERTIFICATE-----\n" +
//                "MIIE8jCCA9qgAwIBAgIQVAESDxKv/JtHV15tvtt1UjANBgkqhkiG9w0BAQsFADAr\n" +
//                "MQ0wCwYDVQQDDARJLUNBMQ0wCwYDVQQKDARJLUNBMQswCQYDVQQGEwJWTjAeFw0y\n" +
//                "MzA2MDcwNjU1MDNaFw0yNjA2MDkwNjU1MDNaMIHlMQswCQYDVQQGEwJWTjESMBAG\n" +
//                "A1UECAwJSMOgIE7hu5lpMRowGAYDVQQHDBFRdeG6rW4gSG/DoG5nIE1haTFCMEAG\n" +
//                "A1UECgw5Q8OUTkcgVFkgQ1AgROG7ikNIIFbhu6QgVsOAIEPDlE5HIE5HSOG7hiBT\n" +
//                "4buQIFFVQU5HIFRSVU5HMUIwQAYDVQQDDDlDw5RORyBUWSBDUCBE4buKQ0ggVuG7\n" +
//                "pCBWw4AgQ8OUTkcgTkdI4buGIFPhu5AgUVVBTkcgVFJVTkcxHjAcBgoJkiaJk/Is\n" +
//                "ZAEBDA5NU1Q6MDExMDE4ODA2NTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoC\n" +
//                "ggEBAJO6JDU+kNEUIiO6m75LOfgHkwGExYFv0tILHInS9CkK2k0FjmvU8VYJ0cQA\n" +
//                "sGGabpHIwfh07llLfK3TUZlhnlFZYRrYvuexlLWQydjHYPqT+1c3iYaiXXcOqEjm\n" +
//                "OupCj71m93ThFrYzzI2Zx07jccRptAAZrWMjI+30vJN7SDxhYsD1uQxYhUkx7psq\n" +
//                "MqD4/nOyaWzZHLU94kTAw5lhAlVOMu3/6pXhIltX/097Wji1eyYqHFu8w7q3B5yW\n" +
//                "gJYugEZfplaeLLtcTxok4VbQCb3cXTOSFiQYJ3nShlBd89AHxaVE+eqJaMuGj9z9\n" +
//                "rdIoGr9LHU/P6KF+/SLwxpsYgnkCAwEAAaOCAVUwggFRMAwGA1UdEwEB/wQCMAAw\n" +
//                "HwYDVR0jBBgwFoAUyCcJbMLE30fqGfJ3KXtnXEOxKSswgZUGCCsGAQUFBwEBBIGI\n" +
//                "MIGFMDIGCCsGAQUFBzAChiZodHRwczovL3Jvb3RjYS5nb3Yudm4vY3J0L3ZucmNh\n" +
//                "MjU2LnA3YjAuBggrBgEFBQcwAoYiaHR0cHM6Ly9yb290Y2EuZ292LnZuL2NydC9J\n" +
//                "LUNBLnA3YjAfBggrBgEFBQcwAYYTaHR0cDovL29jc3AuaS1jYS52bjA0BgNVHSUE\n" +
//                "LTArBggrBgEFBQcDAgYIKwYBBQUHAwQGCisGAQQBgjcKAwwGCSqGSIb3LwEBBTAj\n" +
//                "BgNVHR8EHDAaMBigFqAUhhJodHRwOi8vY3JsLmktY2Eudm4wHQYDVR0OBBYEFE6G\n" +
//                "FFM4HXne9mnFBZInWzSBkYNLMA4GA1UdDwEB/wQEAwIE8DANBgkqhkiG9w0BAQsF\n" +
//                "AAOCAQEAH5ifoJzc8eZegzMPlXswoECq6PF3kLp70E7SlxaO6RJSP5Y324ftXnSW\n" +
//                "0RlfeSr/A20Y79WDbA7Y3AslehM4kbMr77wd3zIij5VQ1sdCbOvcZXyeO0TJsqmQ\n" +
//                "b46tVnayvpJYW1wbui6smCrTlNZu+c1lLQnVsSrAER76krZXaOZhiHD45csmN4dk\n" +
//                "Y0T848QTx6QN0rubEW36Mk6/npaGU6qw6yF7UMvQO7mPeqdufVX9duUJav+WBJ/I\n" +
//                "Y/EdqKp20cAT9vgNap7Bfgv5XN9PrE+Yt0C1BkxXnfJHA7L9hcoYrknsae/Fa2IP\n" +
//                "99RyIXaHLJyzSTKLRUhEVqrycM0UXg==\n" +
//                "-----END CERTIFICATE-----";
//        String appId = "com.pvcb";
////        if (deviceId.isNullOrEmpty()) {
////            deviceId = UUID.randomUUID().toString()
////        }
////        deviceId = "f8552f6d-35da-45f0-9761-f38fe1ea33d1"
//
//        //ABCDEFGHIJKLMNOP
//        LivenessRequest livenessRequest = new LivenessRequest();
//        livenessRequest.setDuration(30);
//        livenessRequest.setPrivateKey(privateKey);
//        livenessRequest.setPublicKey(public_key);
//        livenessRequest.setAppId(appId);
//        livenessRequest.setClientTransactionId(mTransactionIdReadCard);//transactionId from readcard sdk
//        livenessRequest.setBaseURL("https://ekyc-sandbox.eidas.vn/face-matching");
//
//        return livenessRequest;
//
//    }
//
//    private String getImage() {
//        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.img_0);
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
//        byte[] image = stream.toByteArray();
//        return android.util.Base64.encodeToString(image, android.util.Base64.NO_PADDING);
//    }
//}
