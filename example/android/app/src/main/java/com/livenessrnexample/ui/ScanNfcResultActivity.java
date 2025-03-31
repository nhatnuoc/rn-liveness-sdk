package com.livenessrnexample.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.livenessrnexample.MainActivity;
import com.livenessrnexample.R;

public class ScanNfcResultActivity extends AppCompatActivity {
    public static final String KEY_CARD_ID = "cardId";
    public static final String KEY_CARD_OLD = "cardOld";
    public static final String KEY_FULLNAME = "fullname";
    public static final String KEY_BIRTH = "birthday";
    public static final String KEY_EXPIRE = "expire";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_STATE = "state";
    public static final String KEY_NATIONALITY = "nationality";
    public static final String KEY_ORIGIN = "origin";
    public static final String KEY_RELIGION = "religion";
    public static final String KEY_IDENTIFY_CHARACTER = "identifyCharacteristics";
    public static final String KEY_ETHNIC = "ethnic";
    public static final String KEY_REGISTER_PLACE_ADDRESS = "registerPlaceAddress";

    public static final String KEY_PHOTO = "photo";
    public static final String KEY_PHOTO_BASE64 = "photoBase64";
    public static final String KEY_PASSIVE_AUTH = "passiveAuth";
    public static final String KEY_CHIP_AUTH = "chipAuth";

    public static final String KEY_FATHER = "fatherName";
    public static final String KEY_MOTHER = "motherName";
    public static final String KEY_PARTNER = "husbandName";
    public static final String KEY_TRANSACTION_ID = "transactionId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_nfc_result);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ((TextView) findViewById(R.id.output_identify)).setText(getIntent().getStringExtra(KEY_CARD_ID));
        ((TextView) findViewById(R.id.output_identify_old)).setText(getIntent().getStringExtra(KEY_CARD_OLD));
        ((TextView) findViewById(R.id.output_full_name)).setText(getIntent().getStringExtra(KEY_FULLNAME));
        ((TextView) findViewById(R.id.output_birth)).setText(getIntent().getStringExtra(KEY_BIRTH));
        ((TextView) findViewById(R.id.output_expire)).setText(getIntent().getStringExtra(KEY_EXPIRE));
        ((TextView) findViewById(R.id.output_ethnic)).setText(getIntent().getStringExtra(KEY_ETHNIC));
        ((TextView) findViewById(R.id.output_religion)).setText(getIntent().getStringExtra(KEY_RELIGION));
        ((TextView) findViewById(R.id.output_gender)).setText(getIntent().getStringExtra(KEY_GENDER));
        ((TextView) findViewById(R.id.output_state)).setText(getIntent().getStringExtra(KEY_STATE));
        ((TextView) findViewById(R.id.output_nationality)).setText(getIntent().getStringExtra(KEY_NATIONALITY));
        ((TextView) findViewById(R.id.output_origin)).setText(getIntent().getStringExtra(KEY_ORIGIN));
        ((TextView) findViewById(R.id.output_residence)).setText(getIntent().getStringExtra(KEY_REGISTER_PLACE_ADDRESS));
        ((TextView) findViewById(R.id.output_identify_character)).setText(getIntent().getStringExtra(KEY_IDENTIFY_CHARACTER));
        ((TextView) findViewById(R.id.output_passive_auth)).setText(getIntent().getStringExtra(KEY_PASSIVE_AUTH));
        ((TextView) findViewById(R.id.output_chip_auth)).setText(getIntent().getStringExtra(KEY_CHIP_AUTH));
        ((TextView) findViewById(R.id.output_father)).setText(getIntent().getStringExtra(KEY_FATHER));
        ((TextView) findViewById(R.id.output_mother)).setText(getIntent().getStringExtra(KEY_MOTHER));
        if (getIntent().hasExtra(KEY_PARTNER)) {
            ((TextView) findViewById(R.id.output_partner)).setText(getIntent().getStringExtra(KEY_PARTNER));
        }

        if (getIntent().hasExtra(KEY_PHOTO)) {
            ((ImageView) findViewById(R.id.view_photo)).setImageBitmap(getIntent().getParcelableExtra(KEY_PHOTO));
        }

        findViewById(R.id.btn_next_step).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(MainActivity.DO_NEXT_STEP, true);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
//                addFragment();
            }
        });

        findViewById(R.id.btn_close_result_nfc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void addFragment() {
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        FaceVideoJavaFragment mFragment = new FaceVideoJavaFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString(MainActivity.TRANSACTION_ID_READ_CARD, getIntent().getStringExtra(KEY_TRANSACTION_ID));
//        mFragment.setArguments(bundle);
//        transaction.replace(R.id.frame_main_liveness, mFragment);
//        transaction.addToBackStack(FaceVideoJavaFragment.class.getName());
//        transaction.commit();

    }
}
