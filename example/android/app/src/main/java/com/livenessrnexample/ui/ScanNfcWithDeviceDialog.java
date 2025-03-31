package com.livenessrnexample.ui;

import static com.google.gson.internal.$Gson$Types.arrayOf;

import static kotlinx.coroutines.DelayKt.delay;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.livenessrnexample.R;
import com.springcard.pcscaiot.SCardAiot;

import org.jetbrains.annotations.NotNull;
import org.jmrtd.lds.icao.MRZInfo;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ScanNfcWithDeviceDialog extends BottomSheetDialogFragment {

  private ImageView imgScanNfcDone;
  private TextView tvScanNfcTitle;
  private TextView tvScanNfcDescription;
  private LinearProgressIndicator lpiScanNfc;
  private TextView lpiProgressTitle;
  private Button btStartScan;

  private Boolean mReadingNFC = false;
  BottomSheetDialog bottomSheetDialog;
  private SCardAiot cardDevice;
  private UsbDevice mDevice;
  private MRZInfo mrzInfo;
  ExecutorService executor;
  private OnScanCardListener mCallback;
  private Context mContext;
  BroadcastReceiver mUsbAttachReceiver;
  Handler handler;

  public ScanNfcWithDeviceDialog() {

  }

  public ScanNfcWithDeviceDialog(UsbDevice mDevice, MRZInfo mrzInfo, OnScanCardListener callback) {
    this.mDevice = mDevice;
    this.mrzInfo = mrzInfo;
    this.mCallback = callback;
    cardDevice = new SCardAiot();
    cardDevice.init_Device(mDevice);
    mUsbAttachReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())) {
          Log.d("+++", "detach");
          if (usbDevice == null) return;
          if (usbDevice.getDeviceId() == mDevice.getDeviceId()) {
            if (executor != null && !executor.isShutdown()) executor.shutdownNow();
            if (handler != null) handler.removeCallbacksAndMessages(null);
            getActivity().runOnUiThread(() -> {
              Toast.makeText(mContext, "Mất kết nối thiết bị", Toast.LENGTH_SHORT).show();
              bottomSheetDialog.hide();
              dismiss();
            });
          }
        }
      }
    };
  }

  @Override
  public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = (Context) getActivity();
  }

  @NonNull
  @NotNull
  @Override
  public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
    bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
    View vScanNfcDialog = LayoutInflater.from(getContext()).inflate(R.layout.layout_scan_nfc_device, null);
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
    btStartScan = view.findViewById(R.id.btStartScan);

    view.findViewById(R.id.btn_close_scan_nfc).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!mReadingNFC) {
          bottomSheetDialog.dismiss();
        }
      }
    });

    btStartScan.setOnClickListener(v -> {
      readCard();
//      testConnect();
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
    btStartScan.setVisibility(View.GONE);
  }

  public void completeReadingNfc(RawScanData rawScanData) {
    mReadingNFC = false;
    imgScanNfcDone.setVisibility(View.VISIBLE);
    lpiScanNfc.setVisibility(View.GONE);
    lpiProgressTitle.setVisibility(View.GONE);
    tvScanNfcTitle.setText(R.string.scan_nfc_done_title);
    tvScanNfcDescription.setText(R.string.scan_nfc_done_description);
    bottomSheetDialog.hide();
    if (mCallback != null) {
      mCallback.onSuccess(rawScanData);
    }
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
    mReadingNFC = false;
    updateProgress(0);
    lpiScanNfc.setVisibility(View.GONE);
    lpiProgressTitle.setVisibility(View.GONE);
    tvScanNfcTitle.setText(R.string.scan_nfc_prepare_title);
    tvScanNfcDescription.setText(R.string.scan_nfc_device_prepare_description);
    imgScanNfcDone.setVisibility(View.GONE);
    if (scanNfcCount > 1) {
      getActivity().runOnUiThread(()-> Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show());
    }
    if (executor != null && !executor.isShutdown()) {
      executor.shutdownNow();
    }
    if (handler != null) handler.removeCallbacksAndMessages(null);
    btStartScan.setVisibility(View.VISIBLE);
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
    connectDevice();
    mContext.registerReceiver(mUsbAttachReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED));
  }

  private void connectDevice() {
    try {
      if (cardDevice.getConnectToNewDevice()) {
        cardDevice.connectToDevice(mDevice, mContext);
        cardDevice.setConnectToNewDevice(false);
        endScanNfc();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void readCard() {
    executor = Executors.newSingleThreadExecutor();
    handler = new Handler(Looper.getMainLooper());
    try {
      if (cardDevice.ConnectCard()) {
        startReadingNfc();
        executor.execute(() -> {
          try {
            TimeUnit.SECONDS.sleep(4);
            cardDevice.readData_DG1(mrzInfo.getDocumentNumber(), mrzInfo.getDateOfBirth(), mrzInfo.getDateOfExpiry());
            int count = 0;
            while (!cardDevice.isSuccess_DG1() && count < 16) {
              TimeUnit.SECONDS.sleep(1);
              count++;
            }

            if (cardDevice.getDg1() != null) {
              handler.post(() -> updateProgress(15));
            } else {
              handler.post(() ->  readNfcError("Read DG1 error", 2));
//              getActivity().runOnUiThread(() -> readNfcError("Read DG1 error", 2));
            }
            cardDevice.readData_DG2(mrzInfo.getDocumentNumber(), mrzInfo.getDateOfBirth(), mrzInfo.getDateOfExpiry());
            count = 0;
            while (!cardDevice.isSuccess_DG2() && count < 16) {
              TimeUnit.SECONDS.sleep(1);
              count++;
            }

            if (cardDevice.getDg2() != null) {
              handler.post(() -> updateProgress(40));
            } else {
//              getActivity().runOnUiThread(() -> readNfcError("Read DG2 error", 2));
              handler.post(() -> readNfcError("Read DG2 error", 2));
            }

            cardDevice.readData_DG13(mrzInfo.getDocumentNumber(), mrzInfo.getDateOfBirth(), mrzInfo.getDateOfExpiry());
            count = 0;
            while (!cardDevice.isSuccess_DG13() && count < 16) {
              TimeUnit.SECONDS.sleep(1);
              count++;
            }

            if (cardDevice.getDg13() != null) {
              handler.post(() -> updateProgress(55));
            } else {
              handler.post(() -> readNfcError("Read DG13 error", 2));
//              getActivity().runOnUiThread(() -> readNfcError("Read DG13 error", 2));
            }
            cardDevice.readData_SOD(mrzInfo.getDocumentNumber(), mrzInfo.getDateOfBirth(), mrzInfo.getDateOfExpiry());
            count = 0;
            while (!cardDevice.isSuccess_SOD() && count < 16) {
              TimeUnit.SECONDS.sleep(1);
              count++;
            }

            if (cardDevice.getSod() != null) {
              handler.post(() -> updateProgress(70));
            } else {
//              getActivity().runOnUiThread(() -> readNfcError("Read SOD error", 2));
              handler.post(() -> readNfcError("Read SOD error", 2));
            }
            cardDevice.readData_DG14(mrzInfo.getDocumentNumber(), mrzInfo.getDateOfBirth(), mrzInfo.getDateOfExpiry());
            count = 0;
            while (!cardDevice.isSuccess_DG14() && count < 16) {
              TimeUnit.SECONDS.sleep(1);
              count++;
            }

            if (cardDevice.getDg14() != null) {
              handler.post(() -> updateProgress(85));
            } else {
//              getActivity().runOnUiThread(() -> readNfcError("Read DG14 error", 2));
              handler.post(() -> readNfcError("Read DG14 error", 2));
            }
            cardDevice.readData_DG15(mrzInfo.getDocumentNumber(), mrzInfo.getDateOfBirth(), mrzInfo.getDateOfExpiry());
            count = 0;
            while (!cardDevice.isSuccess_DG15() && count < 16) {
              TimeUnit.SECONDS.sleep(1);
              count++;
            }

            if (cardDevice.getDg15() != null) {
              handler.post(() -> {
                updateProgress(100);
//                String dg1 = new String(cardDevice.getDg1(), StandardCharsets.UTF_8);
//                String dg2 = new String(cardDevice.getDg2(), StandardCharsets.UTF_8);
//                String dg13 = new String(cardDevice.getDg13(), StandardCharsets.UTF_8);
//                String sod = new String(cardDevice.getSod(), StandardCharsets.UTF_8);
//                String dg14 = new String(cardDevice.getDg14(), StandardCharsets.UTF_8);
//                String dg15 = new String(cardDevice.getDg1(), StandardCharsets.UTF_8);

//                String dg1 = bytesToString(cardDevice.getDg1());
//                String dg2 = bytesToString(cardDevice.getDg2());
//                String dg13 = bytesToString(cardDevice.getDg13());
//                String sod = bytesToString(cardDevice.getSod());
//                String dg14 = bytesToString(cardDevice.getDg14());
//                String dg15 = bytesToString((cardDevice.getDg15()));

                String dg1 = bytesToBase64(cardDevice.getDg1());
                String dg2 = bytesToBase64(cardDevice.getDg2());
                String dg13 = bytesToBase64(cardDevice.getDg13());
                String sod = bytesToBase64(cardDevice.getSod());
                String dg14 = bytesToBase64(cardDevice.getDg14());
                String dg15 = bytesToBase64((cardDevice.getDg15()));
                RawScanData rawScanData = new RawScanData(dg1, dg2, dg13, sod, dg14, dg15);
                completeReadingNfc(rawScanData);
                cardDevice.setConnectToNewDevice(false);
//                cardDevice.quitAndDisconnect();
              });
            } else {
//              getActivity().runOnUiThread(() -> readNfcError("Read DG15 error", 2));
              handler.post(() -> readNfcError("Read DG15 error", 2));
            }
          } catch (InterruptedException e) {
            e.printStackTrace();
//            getActivity().runOnUiThread(() -> {
//              readNfcError("Error reading card, please try again.", 2);
//            });
            readNfcError("Error reading card, please try again.", 2);
          } catch (Exception e) {
            e.printStackTrace();
//            getActivity().runOnUiThread(() -> readNfcError(e.getMessage(), 2));
            getActivity().runOnUiThread(() -> readNfcError(e.getMessage(), 2));
          }
        });
      } else {
        readNfcError("Please insert card on yours Scanner", 2);
      }
    } catch (Exception e) {
      e.printStackTrace();
      readNfcError("Please insert card on yours Scanner", 2);
    }
  }

  public String bytesToString(byte[] input) {
    if (input == null) {
      return "";
    }
    StringBuilder output = new StringBuilder();
    for (byte b : input) {
      output.append(String.format("%02x", b));
    }
    return output.toString();
  }

  public String bytesToBase64(byte[] bytes) {
    return Base64.encodeToString(bytes, 0);
  }

  public void testConnect() {
    try {
      if (cardDevice.ConnectCard()) {
        startReadingNfc();
      } else {
        readNfcError("Can't connect card", 2);
      }
      cardDevice.setConnectToNewDevice(false);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    mContext.unregisterReceiver(mUsbAttachReceiver);
  }

  @Override
  public void onDestroy() {
    if (executor != null && !executor.isShutdown()) {
      executor.shutdownNow();
    }

    if (handler != null) {
      handler.removeCallbacksAndMessages(null);
    }
    try {
      cardDevice.quitAndDisconnect();
    } catch (Exception e) {
      e.printStackTrace();
    }
    super.onDestroy();
  }

  public BottomSheetDialog getDialog() {
    return bottomSheetDialog;
  }

  public interface OnScanCardListener {
    void onSuccess(RawScanData data);
  }

}
