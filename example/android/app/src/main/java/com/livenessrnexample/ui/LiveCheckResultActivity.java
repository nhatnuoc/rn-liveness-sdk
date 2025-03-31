package com.livenessrnexample.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.liveness.sdk.corev4.model.DataModel;
import com.liveness.sdk.corev4.model.LivenessModel;
import com.livenessrnexample.R;
import com.livenessrnexample.util.DataUtil;
import com.livenessrnexample.MainActivity;

public class LiveCheckResultActivity extends AppCompatActivity {
  private static final String MRZ_RESULT = "MRZ_RESULT";
  private static String TAG = LiveCheckResultActivity.class.getSimpleName();
  //    private TextView tvMrzCode;
  private TextView tvMathScore;
  private TextView tvResult;
  private TextView tvLiveScore;
  private ImageView ivCardId;
  private RecyclerView rvPhoto;
  private TextView tvDataResult;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_liveness_check_result);
    // some device still show the action bar, e.g. Fold 2,
    // so we need to manual hide the action bar
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    if (getSupportActionBar() != null) {
      getSupportActionBar().hide();
    }

//        tvMrzCode = findViewById(R.id.tv_mrz_code);
    tvMathScore = findViewById(R.id.result_matching_score);
    tvResult = findViewById(R.id.result_text);
    tvLiveScore = findViewById(R.id.result_live_score);
    ivCardId = findViewById(R.id.ivCardId);
    rvPhoto = findViewById(R.id.rvPhoto);
    tvDataResult = findViewById(R.id.tvDataResult);

    if (DataUtil.result != null) {
      DataModel model = DataUtil.result.getData();
      if (model != null) {
        tvMathScore.setText(String.valueOf(model.getFaceMatchingScore()));
        tvLiveScore.setText(String.valueOf(model.getLivenesScore()));
        boolean result = model.getFaceMatchingResult() != null && model.getFaceMatchingResult() == 1;
        tvResult.setText(String.valueOf(result));
      }

      LivenessModel raw= DataUtil.result;
      raw.setImageResult(null);
      raw.setSignature(null);
      raw.setLivenessImage(null);
      tvDataResult.setText(raw.toString());
    }
    if (DataUtil.CARD_IMAGE != null) {
      ivCardId.setImageBitmap(DataUtil.CARD_IMAGE);
    }
    createPhoto();
    findViewById(R.id.btn_complete).setOnClickListener(view -> {
      Intent returnIntent = new Intent();
      returnIntent.putExtra(MainActivity.DO_NEXT_STEP, true);
      setResult(Activity.RESULT_OK, returnIntent);
      finish();
    });

    findViewById(R.id.btn_close_liveness_check_result).setOnClickListener(view -> finish());
  }

  private void createPhoto(){
    if(DataUtil.IMAGE_RESULT==null) return;
    ResultAdapter adapter= new ResultAdapter(DataUtil.IMAGE_RESULT);
    rvPhoto.setAdapter(adapter);
    adapter.notifyDataSetChanged();
  }

}
