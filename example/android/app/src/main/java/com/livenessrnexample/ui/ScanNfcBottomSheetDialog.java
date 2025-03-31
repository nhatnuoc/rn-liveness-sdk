package com.livenessrnexample.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.livenessrnexample.R;

import org.jetbrains.annotations.NotNull;

public class ScanNfcBottomSheetDialog extends BottomSheetDialogFragment {

    private ImageView imgScanNfcDone;
    private TextView tvScanNfcTitle;
    private TextView tvScanNfcDescription;
    private LinearProgressIndicator lpiScanNfc;
    private TextView lpiProgressTitle;

    private Boolean mReadingNFC = false;
    BottomSheetDialog bottomSheetDialog;

    public static ScanNfcBottomSheetDialog newInstance() {
        ScanNfcBottomSheetDialog  scanNfcBottomSheetDialog = new ScanNfcBottomSheetDialog();
        return scanNfcBottomSheetDialog;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View vScanNfcDialog = LayoutInflater.from(getContext()).inflate(R.layout.layout_scan_nfc, null);
        bottomSheetDialog.setContentView(vScanNfcDialog);

        initView(vScanNfcDialog);

        return bottomSheetDialog;
    }

    private void initView(View view) {
        imgScanNfcDone = view.findViewById(R.id.img_scan_nfc_done);
        lpiScanNfc = view.findViewById(R.id.lpi_scan_nfc);
        lpiProgressTitle = view.findViewById(R.id.lpi_progress_title);
        tvScanNfcTitle = view.findViewById(R.id.tv_scan_nfc_title);
        tvScanNfcDescription = view.findViewById(R.id.tv_scan_nfc_description);

        view.findViewById(R.id.btn_close_scan_nfc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mReadingNFC) {
                    bottomSheetDialog.dismiss();
                }
            }
        });

        lpiScanNfc.setVisibility(View.GONE);
        lpiProgressTitle.setVisibility(View.GONE);
        lpiScanNfc.setProgress(0);
        tvScanNfcTitle.setText(R.string.scan_nfc_prepare_title);
        tvScanNfcDescription.setText(R.string.scan_nfc_prepare_description);
        imgScanNfcDone.setVisibility(View.GONE);
    }

    public void startReadingNfc() {
        bottomSheetDialog.setCancelable(false);
        mReadingNFC = true;
        tvScanNfcTitle.setText(R.string.scan_nfc_reading_title);
        tvScanNfcDescription.setText(R.string.scan_nfc_reading_description);
        lpiScanNfc.setVisibility(View.VISIBLE);
        lpiProgressTitle.setText("0%");
        lpiProgressTitle.setVisibility(View.VISIBLE);
    }

    public void completeReadingNfc() {
        mReadingNFC = false;
        imgScanNfcDone.setVisibility(View.VISIBLE);
        lpiScanNfc.setVisibility(View.GONE);
        lpiProgressTitle.setVisibility(View.GONE);
        tvScanNfcTitle.setText(R.string.scan_nfc_done_title);
        tvScanNfcDescription.setText(R.string.scan_nfc_done_description);
        bottomSheetDialog.hide();
        dismiss();
    }

    public void updateProgress(Integer progress) {
        lpiScanNfc.setProgressCompat(progress, true);
        lpiProgressTitle.setText(progress.toString() + "%");
    }

    public void readNfcError(String msg, int scanNfcCount) {
        lpiScanNfc.setProgressCompat(0, true);
        tvScanNfcTitle.setText(R.string.scan_nfc_prepare_title);
        tvScanNfcDescription.setText(R.string.scan_nfc_prepare_description);
        imgScanNfcDone.setVisibility(View.GONE);
        if (scanNfcCount > 1) {
            Toast.makeText(
                            getContext(),
                            msg,
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void endScanNfc() {
        lpiScanNfc.setVisibility(View.GONE);
        lpiProgressTitle.setVisibility(View.GONE);
        mReadingNFC = false;
        bottomSheetDialog.setCancelable(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Activity parentActivity = getActivity();
        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(parentActivity);
        if (mNfcAdapter != null) {
            Intent mIntent = new Intent(parentActivity, parentActivity.getClass());
            mIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent mPendingIntent = PendingIntent.getActivity(parentActivity, 0, mIntent, PendingIntent.FLAG_MUTABLE);
            String[][] mFilter = new String[][]{new String[]{"android.nfc.tech.IsoDep"}};
            mNfcAdapter.enableForegroundDispatch(parentActivity, mPendingIntent, null, mFilter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Activity parentActivity = getActivity();
        NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(parentActivity);
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(parentActivity);
        }
    }

    public BottomSheetDialog getDialog() {
        return bottomSheetDialog;
    }

}
