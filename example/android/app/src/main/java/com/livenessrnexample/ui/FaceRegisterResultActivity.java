package com.livenessrnexample.ui;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.liveness.sdk.corev4.model.LivenessModel;
import com.livenessrnexample.R;
import com.livenessrnexample.util.DataUtil;
import com.livenessrnexample.util.ImageUtil;

public class FaceRegisterResultActivity extends AppCompatActivity {
  private TextView tvResult;
  private TextView tvMessage;
  private ImageView ivRegisterFace;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register_face_result);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    if (getSupportActionBar() != null) {
      getSupportActionBar().hide();
    }

    tvResult = findViewById(R.id.result_text);
    tvMessage = findViewById(R.id.tvMessage);

    ivRegisterFace = findViewById(R.id.ivRegisterFace);
    if (DataUtil.result != null) {
      LivenessModel model = DataUtil.result;
      if (model.getMessage() != null) {
        tvMessage.setText(model.getMessage());
      }
      boolean result = model.getStatus() != null && model.getStatus() == 200;
      tvResult.setText(result ? "Success" : "Fail");
    }
    if (DataUtil.FACE_IMAGE != null) {
      Bitmap bm = ImageUtil.base64ToBitmap(DataUtil.FACE_IMAGE);
      if (bm != null) {
        ivRegisterFace.setImageBitmap(bm);
      }
    }


    findViewById(R.id.btn_done).setOnClickListener(view -> {
      finish();
    });

    findViewById(R.id.btn_close_register_face_result).setOnClickListener(view -> finish());
  }

}
