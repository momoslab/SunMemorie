package com.example.yassine.sunlamp.Bluetooth.Connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by YassIne on 29/08/2015.
 */
public class ClassicBTConnection extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final BluetoothAdapter mAdapter;
    byte [] buffer;

    //stati di connessione bluetooth
    final static int IS_CONNECTED = 1;
    final static int IS_DISCONNECTED = 0;

    //stati di trasferimento dati

    //ricenzione
    final static int IS_RECEIVING = 1;
    final static int NOT_RECEIVING = 0;

    //spedizione
    final static int IS_SENDING = 0;
    final static int NOT_SENDING = 0;

    private int mSending;
    private int mReceiving;



    private int mState;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public ClassicBTConnection(BluetoothDevice device) {
        mState = IS_DISCONNECTED;

        BluetoothSocket tmp = null;

        mAdapter = BluetoothAdapter.getDefaultAdapter();

        //Get a BluetoothSocket from a connection whit the given BluetoothDevice
        try {
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("cant create RFcommSocket " + e.getMessage());
        }

        mmSocket = tmp;
        //now make the socket connection in separate thread to avoid FC
        Thread connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //cancel discovery because it will slow down the connection
                mAdapter.cancelDiscovery();

                try {
                    mmSocket.connect();
                    mState = IS_CONNECTED;
                } catch (IOException e) {
                    //if the connection is failed close the socket
                    System.out.println("cant connect to socket " + e.getMessage());
                    try {
                        mmSocket.close();
                        mState = IS_DISCONNECTED;
                    } catch (IOException e2) {
                        e2.printStackTrace();
                        mState = IS_DISCONNECTED;
                    }

                }
            }
        });

        connectionThread.start();

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        //Get the BluetoothSocket input and output streams

        try{
            tmpIn = mmSocket.getInputStream();
            tmpOut = mmSocket.getOutputStream();
            buffer = new byte[2048];
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("socket dosent exist  " + e.getMessage());
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public boolean isConnected(){
        return mState == IS_CONNECTED;
    }

    public void read(){
        while (true){
            try{
                mReceiving = IS_RECEIVING;
                mmInStream.read(buffer);
            }catch (IOException e){
                //message is complete send it to the UI
                mReceiving = NOT_RECEIVING;
                break;
            }
        }
    }

    public void write(byte [] buffer){
        try{
            mSending = IS_SENDING;
            mmOutStream.write(buffer);
            System.out.println("data sent");

        }catch (IOException e){
            mSending = NOT_SENDING;
            e.printStackTrace();
            System.out.println("cant send data " + e.getMessage());
        }
    }

    public void cancel(){
        try{
            mmSocket.close();
            mState = IS_DISCONNECTED;
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
