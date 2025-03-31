package com.livenessrnexample.ui;

import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.livenessrnexample.MainActivity;
import com.livenessrnexample.R;
import com.springcard.pcscaiot.SCardAiot;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChooseScannerDialog extends DialogFragment {

  private OnScannerListener mCallback;
  private UsbDeviceAdapter mAdapter;
  private List<DeviceModel> mListData;
  private List<UsbDevice> mListUsbDevice;
  private SCardAiot cardDevice;
  private List<String> deviceFilter;
  private MainActivity mActivity;
  private final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
  private final String TAG = "ChooseScannerDialog";
  private UsbManager usbManager;

  private PendingIntent mPermissionIntent;
  private TextView tvEmptyData;
  BroadcastReceiver mUsbAttachReceiver;

  public ChooseScannerDialog() {
  }


  public static ChooseScannerDialog newInstance(MainActivity activity, OnScannerListener callback) {
    ChooseScannerDialog dialog = new ChooseScannerDialog();
    dialog.mActivity = activity;
    dialog.mCallback = callback;
    return dialog;

  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
  }


  public void initializeUsbManager() {
    mListData = new ArrayList<>();
    mListUsbDevice = new ArrayList<>();
    cardDevice = new SCardAiot();
    deviceFilter = new ArrayList<>();
    usbManager = (UsbManager) mActivity.getSystemService(Context.USB_SERVICE);
    mPermissionIntent = PendingIntent.getBroadcast(mActivity, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_MUTABLE);
  }

  protected boolean missingSystemFeature(PackageManager packageManager, String name) {
    return !packageManager.hasSystemFeature(name);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    initializeUsbManager();
    if (missingSystemFeature(mActivity.getPackageManager(), PackageManager.FEATURE_USB_HOST)) {
      Toast.makeText(mActivity, "usb host not supported", Toast.LENGTH_SHORT).show();
    }
    mUsbAttachReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())) {
          addDevice(usbDevice);
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())) {
          removeDevice(usbDevice);
        } else if (ACTION_USB_PERMISSION.equals(intent.getAction())) {
          synchronized (this) {
            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
              if (mCallback != null) {
                Log.d(TAG, "Granted for device: " + usbDevice);
                mCallback.onRead(usbDevice);
                if (mUsbAttachReceiver != null) {
                  mActivity.getApplicationContext().unregisterReceiver(mUsbAttachReceiver);
                }
                dismiss();
              }
            } else {
              Log.d(TAG, "Permission denied for device " + usbDevice);
            }
          }
        }
      }
    };

    mActivity.getApplicationContext().registerReceiver(mUsbAttachReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED));
    mActivity.getApplicationContext().registerReceiver(mUsbAttachReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED));
    mActivity.getApplicationContext().registerReceiver(mUsbAttachReceiver, new IntentFilter(ACTION_USB_PERMISSION));

    return inflater.inflate(R.layout.dialog_choose_scanner, container);
  }

//  @Override
//  public void onDismiss(DialogInterface dialog) {
//    if(mUsbAttachReceiver!=null){
  //    mActivity.getApplicationContext().unregisterReceiver(mUsbAttachReceiver);
//  }
//    super.onDismiss(dialog);
//  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ListView lvDevice = view.findViewById(R.id.lvListDevice);
    tvEmptyData = view.findViewById(R.id.tvEmptyData);

    XmlPullParser xmlResourceParser = requireContext().getResources().getXml(R.xml.device_filter);
    int eventType = 0;
    try {
      eventType = xmlResourceParser.getEventType();
    } catch (XmlPullParserException e) {
      throw new RuntimeException(e);
    }
    try {
      while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG) {
          if (xmlResourceParser.getName().equals("usb-device")) {
            String vid = xmlResourceParser.getAttributeValue("1", "0");
            String pid = xmlResourceParser.getAttributeValue("0", "0");
            if (pid != null && vid != null) {
              deviceFilter.add(getDeviceIdsAsString(vid, pid));
            }
          }
        }
        eventType = xmlResourceParser.next();
      }
    } catch (XmlPullParserException | IOException e) {
      Log.e(TAG, "Error processing XML resource parser", e);
    }
    Log.d(TAG, "deviceList: " + deviceFilter);

    mAdapter = new UsbDeviceAdapter(mActivity.getApplicationContext(), mListData);
    lvDevice.setAdapter(mAdapter);
    lvDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "Device " + mListUsbDevice.get(position).getDeviceName() + " selected");
        usbManager.requestPermission(mListUsbDevice.get(position), mPermissionIntent);
      }
    });

  }

  @Override
  public void onDestroy() {
    if (mUsbAttachReceiver != null) {
      //    mActivity.getApplicationContext().unregisterReceiver(mUsbAttachReceiver);
    }
    super.onDestroy();
  }

  @Override
  public void onResume() {
    super.onResume();
    mListData.clear();
    mListUsbDevice.clear();
    mAdapter.notifyDataSetChanged();
    usbManager = (UsbManager) mActivity.getSystemService(Context.USB_SERVICE);
    HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
    deviceList.values().forEach(device -> addDevice(device));
  }

  private String getDeviceIdsAsString(String vid, String pid) {
    return "VID: " + vid + ", PID: " + pid;
  }

  private Context requireContext() {
    return mActivity.getApplicationContext();
  }

  private void addDevice(UsbDevice device) {
    DeviceModel newItem = new DeviceModel(device.getManufacturerName() + " " + device.getProductName(), cardDevice.getDeviceExtraInfo(device));

    String deviceIdentifier = cardDevice.getDeviceIdsAsString(device.getVendorId(), device.getProductId());
    if (!deviceFilter.contains(deviceIdentifier)) {
      Log.d(TAG, "Device is not in device filter list (not a SpringCard device?)");
    }

    if (!mListData.contains(newItem)) {
      mListData.add(newItem);
      mListUsbDevice.add(device);
      if (mAdapter != null) {
        mAdapter.notifyDataSetChanged();
      }
      showEmptyData();
      Log.d(TAG, "New device found: " + device.getManufacturerName() + " " + device.getProductName() + " " + cardDevice.getDeviceExtraInfo(device) + " (Thread = " + Thread.currentThread().getName() + ")");
    }
  }

  private void showEmptyData() {
    if (mListData.isEmpty()) {
      tvEmptyData.setVisibility(View.VISIBLE);
    } else {
      tvEmptyData.setVisibility(View.GONE);
    }
  }

  private void removeDevice(UsbDevice device) {
    DeviceModel item = new DeviceModel(device.getManufacturerName() + " " + device.getProductName(), cardDevice.getDeviceExtraInfo(device));

    if (mListUsbDevice.contains(device)) {
      mListData.removeIf(deviceModel -> item.getName().equals(deviceModel.getName()));
      mListUsbDevice.remove(device);
      if (mAdapter != null) {
        mAdapter.notifyDataSetChanged();
      }
      showEmptyData();
      Log.d(TAG, "Device removed: " + device.getManufacturerName() + " " + device.getProductName() + " " + cardDevice.getDeviceExtraInfo(device));
    }

  }

  public interface OnScannerListener {
    void onRead(UsbDevice usbDevice);
  }
}
