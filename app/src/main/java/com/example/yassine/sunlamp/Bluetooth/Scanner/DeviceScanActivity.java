package com.example.yassine.sunlamp.Bluetooth.Scanner;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yassine.sunlamp.Adapter.BTDeviceListAdapter;
import com.example.yassine.sunlamp.Bluetooth.Connection.ClassicBTConnection;
import com.example.yassine.sunlamp.MyBaseApplication;
import com.example.yassine.sunlamp.R;

import java.util.Set;

public class DeviceScanActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothDevice;
    ClassicBTConnection mBluetoothConnection;
    private ArrayAdapter<String> mFoundDevicesAdapter;
    private ArrayAdapter<String> mPairedDevicesAdapter;
    private Button mScanDevices;
    private BTDeviceListAdapter mBluetoothListAdapter;
    private BTDeviceListAdapter mPairedListAdapter;
    private Set<BluetoothDevice> mPairedDevices;

    private static final int BLUETOOTH_ON = 1000;


    private ListView mDeviceList;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(foundDevice.getBondState() != BluetoothDevice.BOND_BONDED){
                    mBluetoothListAdapter.addDevice(foundDevice);
                    mBluetoothListAdapter.notifyDataSetChanged();
                }
            }
            else
                if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                    setProgressBarIndeterminateVisibility(false);

                    if (mFoundDevicesAdapter.getCount() == 0){
                        String noDevices = getResources().getText(R.string.no_paired).toString();
                       // mBluetoothListAdapter.addDevice(noDevices);

                    }
                }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);

        mBluetoothConnection = null;

        //Device connection
        mBluetoothDevice = BluetoothAdapter.getDefaultAdapter();

        //Inizializzazione degli adapter
        mBluetoothListAdapter = new BTDeviceListAdapter(this.getApplicationContext());
        mPairedListAdapter = new BTDeviceListAdapter(this.getApplicationContext());

        mDeviceList = (ListView) findViewById(R.id.device_list);
        mDeviceList.setAdapter(mBluetoothListAdapter);

        mDeviceList.setOnItemClickListener(mDeviceListener);

        ListView pairedListView = (ListView) findViewById(R.id.paired_list);
        pairedListView.setAdapter(mPairedListAdapter);

        pairedListView.setOnItemClickListener(mDeviceListener);

        //Nuova ricerca dispositivi
        mScanDevices = (Button) findViewById(R.id.btnScanDevices);
        mScanDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btStartDiscovery();
            }
        });

        //-----------------------------------------
        // Inizializzazione reciever
        //-----------------------------------------
        IntentFilter foundDevicesFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, foundDevicesFilter);

    }

    //-----------------------------------------
    // Ottieni Dispositivi Associati
    //-----------------------------------------

    public void getPairedDevices(){
        //ottieni i dispositivi già associati
        mPairedDevices = mBluetoothDevice.getBondedDevices();

        //stampa a video i dispositivi associati
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice device : mPairedDevices) {
                mPairedListAdapter.addDevice(device);

            }
        }
    }

    @Override
    public void onStart(){
        super.onStart();


        //Bluetooth not active
        if(!mBluetoothDevice.isEnabled()){
            Toast.makeText(this, "Bluetooth verrà abilitato", Toast.LENGTH_SHORT).show();
            Intent btOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(btOn, BLUETOOTH_ON);

            //Check if BLE is active
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {

            }
        }
        //Bluetooth already active
        else {
            btStartDiscovery();
            getPairedDevices();
        }


    }

    public void btStartDiscovery(){
        //se sta già effettuando una ricerca interrompi la ricerca ed effettua una nuova
        if(mBluetoothDevice.isDiscovering())
            mBluetoothDevice.cancelDiscovery();

        //inizia ricerca nuovi dispositivi
        if(mBluetoothConnection == null || !mBluetoothConnection.isConnected()){
            mBluetoothDevice.startDiscovery();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_scan, menu);
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

    private AdapterView.OnItemClickListener mDeviceListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //blocca ogni attività di ricerca
            mBluetoothDevice.cancelDiscovery();

            //Ottenere il MAC address
            TextView txtName = (TextView) view.findViewById(R.id.bt_device_name);
            TextView txtAddress = (TextView) view.findViewById(R.id.bt_device_address);

            String name = txtName.getText().toString();
            String address = txtAddress.getText().toString();

            //creare un device bluetooth per la connessione
            BluetoothDevice device = mBluetoothDevice.getRemoteDevice(address);

            returnThisDevice(device);
        }
    };

    /**
     * Ritorna alla classe chiamante il device bluetooth scelto dalla lista
     * @param device
     */
    public void returnThisDevice(BluetoothDevice device){
        Intent deviceToSend = new Intent();
        deviceToSend.putExtra("BLUETOOTH_DEVICE", device);
        if(getParent() == null){
            setResult(Activity.RESULT_OK, deviceToSend);
        }
        else{
            getParent().setResult(Activity.RESULT_OK, deviceToSend);
        }
        finish();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);

        // Make sure we're not doing discovery anymore
        if (mBluetoothDevice != null) {
            mBluetoothDevice.cancelDiscovery();
        }
        if(mBluetoothConnection != null){
            if(mBluetoothConnection.isConnected()){
                mBluetoothConnection.cancel();
            }
        }


    }

    public void onPause(){
        super.onPause();
        if(this.mBluetoothConnection != null){

        }
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        if(requestCode == BLUETOOTH_ON){
            //Controlla che sia un successo
            if(resultCode == RESULT_OK){
                btStartDiscovery();
                getPairedDevices();
            }

            else if(resultCode ==RESULT_CANCELED){
                finish();
            }
        }
    }
}
