package com.example.yassine.sunlamp.Adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yassine.sunlamp.R;

import java.util.ArrayList;

/**
 * Created by YassIne on 28/09/2015.
 */
//creare un Adapter per la lista dei dispositivi trovati
public class BTDeviceListAdapter extends BaseAdapter {
    private ArrayList<BluetoothDevice> mLeDevices;
    private Context context;

    public BTDeviceListAdapter(Context context){
        super();
        this.context = context;
        mLeDevices = new ArrayList<>();
    }

    public void addDevice(BluetoothDevice device){
        if(!mLeDevices.contains(device)){
            mLeDevices.add(device);
        }
    }

    public BluetoothDevice getDevice(int position){
        return mLeDevices.get(position);
    }

    public void clear(){
        mLeDevices.clear();
    }

    @Override
    public int getCount(){
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i){
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i){
        return i;
    }

    public View getView(int i, View view, ViewGroup viewGroup){
        ViewHolder viewHolder;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.list_item_device, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.bt_device_address);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.bt_device_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDevice device = mLeDevices.get(i);
        final String deviceName = device.getName();
        if(deviceName != null && deviceName.length() > 0){
            viewHolder.deviceName.setText(deviceName);
        } else
            viewHolder.deviceName.setText(R.string.unknown_device);
        viewHolder.deviceAddress.setText(device.getAddress());

        return view;
    }

     static class ViewHolder{
         TextView deviceName;
         TextView deviceAddress;
    }
}
