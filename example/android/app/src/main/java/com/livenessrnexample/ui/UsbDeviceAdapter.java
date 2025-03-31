package com.livenessrnexample.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.livenessrnexample.R;

import java.util.List;

public class UsbDeviceAdapter extends BaseAdapter {

    private Context mContext;
    private List<DeviceModel> mListDevice;
    private LayoutInflater inflater;


    public UsbDeviceAdapter() {
    }

    public UsbDeviceAdapter(Context context, List<DeviceModel> mListDevice) {
        this.mContext = context;
        this.mListDevice = mListDevice;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (mListDevice == null) {
            return 0;
        } else {
            return mListDevice.size();
        }
    }

    @Override
    public Object getItem(int i) {
        return mListDevice.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = inflater.inflate(R.layout.item_device_list, viewGroup, false);

        TextView tvDeviceName = rowView.findViewById(R.id.vl_item_name);

        TextView tvDeviceInfo = rowView.findViewById(R.id.vl_item_rssi);

        DeviceModel device = (DeviceModel) getItem(i);

        tvDeviceName.setText(device.getName());
//        subtitleTextView.text = device.info.toString()
//        titleTextView.text = "Device AIot"
        tvDeviceInfo.setText(device.getInfo());
        return rowView;
    }
}
