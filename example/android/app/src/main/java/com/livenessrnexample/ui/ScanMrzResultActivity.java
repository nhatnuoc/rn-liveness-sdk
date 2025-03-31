package com.livenessrnexample.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.livenessrnexample.MainActivity;
import com.livenessrnexample.R;
import com.livenessrnexample.util.DateTimeUtil;

import org.jmrtd.lds.icao.MRZInfo;

public class ScanMrzResultActivity extends AppCompatActivity {
    private static final String MRZ_RESULT = "MRZ_RESULT";
    private static String TAG = ScanMrzResultActivity.class.getSimpleName();
//    private TextView tvMrzCode;
    private TextView tvLast9Digit;
    private TextView tvDob;
    private TextView tvDoe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_mrz_result);
        // some device still show the action bar, e.g. Fold 2,
        // so we need to manual hide the action bar
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

//        tvMrzCode = findViewById(R.id.tv_mrz_code);
        tvLast9Digit = findViewById(R.id.tv_last_9_digit);
        tvDob = findViewById(R.id.tv_dob);
        tvDoe = findViewById(R.id.tv_doe);
        MRZInfo mrzInfo = (MRZInfo) getIntent().getSerializableExtra(MRZ_RESULT);
        if (mrzInfo != null) {
//            tvMrzCode.setText(mrzInfo.getDocumentCode());
            tvLast9Digit.setText(mrzInfo.getDocumentNumber());
            tvDob.setText(DateTimeUtil.formatDate(mrzInfo.getDateOfBirth(), "yyMMdd", "dd/MM/yyyy"));
            tvDoe.setText(
                    mrzInfo.getDateOfExpiry().equals("991231")
                            ? "Không thời hạn"
                            : DateTimeUtil.formatDate(mrzInfo.getDateOfExpiry(), "yyMMdd", "dd/MM/yyyy")
            );
        } else {
            Log.d(TAG, "mrzInfo is null");
        }

        findViewById(R.id.btn_next_step).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(MainActivity.DO_NEXT_STEP, true);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        findViewById(R.id.btn_close_result_mrz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
