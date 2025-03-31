package com.livenessrnexample.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.livenessrnexample.R;
import com.livenessrnexample.mlkit.DocType;
import com.livenessrnexample.mlkit.camera.CameraSource;
import com.livenessrnexample.mlkit.camera.CameraSourcePreview;
import com.livenessrnexample.mlkit.text.TextRecognitionProcessor;
import com.livenessrnexample.mlkit.camera.GraphicOverlay;
import com.livenessrnexample.util.DataUtil;

import org.jmrtd.lds.icao.MRZInfo;
import java.io.IOException;

public class CaptureActivity extends AppCompatActivity implements TextRecognitionProcessor.ResultListener {
    public static final String MRZ_RESULT = "MRZ_RESULT";
    public static final String DOC_TYPE = "DOC_TYPE";
    private static String TAG = CaptureActivity.class.getSimpleName();
    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private DocType docType = DocType.OTHER;
    private Boolean showedSuccessToast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (getIntent().hasExtra(DOC_TYPE)) {
            docType = (DocType) getIntent().getSerializableExtra(DOC_TYPE);
            if (docType == DocType.PASSPORT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        findViewById(R.id.btn_close_scan_mrz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        preview = findViewById(R.id.camera_source_preview);
        if (preview == null) {
            Log.d(TAG, "Preview is null");
        }
        graphicOverlay = findViewById(R.id.graphics_overlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }

        createCameraSource();
        startCameraSource();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
            cameraSource = null;
        }
    }

    private void createCameraSource() {

        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
            cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
        }

        cameraSource.setMachineLearningFrameProcessor(new TextRecognitionProcessor(docType, this));
    }

    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    public void onSuccess(MRZInfo mrzInfo, String b64Data) {
        // This method might be called multiple at once, IDK, so I will use a flag for not showing too much toast to user
        if (!showedSuccessToast) {
            showedSuccessToast = true;
            Toast.makeText(this,  R.string.camera_text_scan_successful, Toast.LENGTH_SHORT).show();
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra(MRZ_RESULT, mrzInfo);
        DataUtil.backCardImage = b64Data;
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onError(Exception exp) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
