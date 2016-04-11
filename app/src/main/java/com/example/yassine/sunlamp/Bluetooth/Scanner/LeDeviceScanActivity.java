package com.example.yassine.sunlamp.Bluetooth.Scanner;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;


import com.example.yassine.sunlamp.R;
import com.skyfishjy.library.RippleBackground;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class LeDeviceScanActivity extends AppCompatActivity {

    private BluetoothAdapter mAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private Button mBluetoothScanButton;
    private ProgressBar mScanningProgress;
    private BluetoothLeScanner mBluetoothScanner;
    private BTDeviceListAdapter mLeDeviceListAdapter;
    private ListView mDeviceList;

    private RippleBackground rippleBackground;

    private BluetoothManager mBluetoothManager;
    private static final int REQUEST_ENABLE_BT = 1;

    private static long SCAN_PERIOD = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setTitle("Scansiona");

        mHandler = new Handler();

        rippleBackground=(RippleBackground)findViewById(R.id.content);
        ImageView imageView=(ImageView)findViewById(R.id.centerImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rippleBackground.startRippleAnimation();
            }
        });


        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mAdapter = mBluetoothManager.getAdapter();
        mLeDeviceListAdapter = new BTDeviceListAdapter(getApplicationContext());

        if(Build.VERSION.SDK_INT >= 21)
            mBluetoothScanner = mAdapter.getBluetoothLeScanner();


        mBluetoothScanButton =  (Button) findViewById(R.id.btnScanDevices);

        mBluetoothScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanLeDevice(true);
            }
        });

        mScanningProgress = (ProgressBar) findViewById(R.id.device_scan_progress_bar);
        mDeviceList = (ListView) findViewById(R.id.device_list);
        mDeviceList.setAdapter(mLeDeviceListAdapter);

        scanLeDevice(true);


    }

    private void showProgress(boolean show){
        if(show){
            if(mScanning){
                mScanningProgress.setVisibility(View.VISIBLE);
                mBluetoothScanButton.setVisibility(View.GONE);
            }
        }
        else{
            if(!mScanning){
                mScanningProgress.setVisibility(View.GONE);
                mBluetoothScanButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void scanLeDevice(final boolean enable){
        if(enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //if(Build.VERSION.SDK_INT < 21) {
                    mAdapter.stopLeScan(mLeScanCallback);
                    //}
                    //else {
                        //mBluetoothScanner.stopScan(mScanCallback);
                    //}
                    mScanning = false;
                    showProgress(false);

                }
            }, SCAN_PERIOD);

            showProgress(true);
            mScanning = true;
            //if(Build.VERSION.SDK_INT < 21){
                mAdapter.startLeScan(mLeScanCallback);
            //}
            //else {
                //mBluetoothScanner.startScan(mScanCallback);
            //}

        }
        else {
            //if(Build.VERSION.SDK_INT < 21) {
                mAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
            showProgress(false);
            //}
            //else {
                //mBluetoothScanner.stopScan(mScanCallback);
            //}
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_le_device_scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    private ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            textScrivi.setText("Dispositivo trovato:");
        }
        @Override
        public void onScanResult(int callbackType, final android.bluetooth.le.ScanResult result){

            textScrivi.setText("Dispositivo trovato: " + result.getDevice().getAddress());
            //Log.w("LeDeviceScan","dispositivo trovato");
            mLeDeviceListAdapter.addDevice(result.getDevice());
            mLeDeviceListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onScanFailed(int errorCode) {
            textScrivi.setText("Scansione fallita " + errorCode);
        }
    };
    */

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    private class BTDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;
        private Context context;

        public BTDeviceListAdapter(Context context){
            super();
            mInflator = LeDeviceScanActivity.this.getLayoutInflater();
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
                //LayoutInflater.from(getContext()).inflate(R.layout.item_color, parent, false);
                //view = LayoutInflater.from(context).inflate(R.layout.list_item_device, viewGroup, false);
                view = mInflator.inflate(R.layout.list_item_device, null);
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


    }
    static class ViewHolder{
        TextView deviceName;
        TextView deviceAddress;
    }
}
