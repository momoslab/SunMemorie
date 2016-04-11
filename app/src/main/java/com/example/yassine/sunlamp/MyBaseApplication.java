package com.example.yassine.sunlamp;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/**
 * Created by YassIne on 02/10/2015.
 */
public class MyBaseApplication extends Application {
    public BluetoothDevice mLampDevice;
    public BluetoothDevice mSunAcquisitionDevice;

    @Override
    public void onCreate(){
        super.onCreate();
        mLampDevice = null;
        mSunAcquisitionDevice = null;
    }

    public BluetoothDevice getLampDevice() {
        return mLampDevice;
    }

    public void setLampDevice(BluetoothDevice mLampDevice) {
        this.mLampDevice = mLampDevice;
    }

    public BluetoothDevice getSunAcquisitionDevice() {
        return mSunAcquisitionDevice;
    }

    public void setSunAcquisitionDevice(BluetoothDevice mSunAcquisitionDevice) {
        this.mSunAcquisitionDevice = mSunAcquisitionDevice;
    }
}
