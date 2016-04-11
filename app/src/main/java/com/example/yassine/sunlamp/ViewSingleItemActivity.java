package com.example.yassine.sunlamp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.support.v7.widget.PopupMenu;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.util.Log;


import com.example.yassine.sunlamp.Bluetooth.Connection.ClassicBTConnection;
import com.example.yassine.sunlamp.Bluetooth.Scanner.DeviceScanActivity;
import com.example.yassine.sunlamp.Bluetooth.Scanner.LeDeviceScanActivity;
import com.example.yassine.sunlamp.Model.ColorData;
import com.example.yassine.sunlamp.View.RoundedFrameLayout;

import android.support.design.widget.FloatingActionButton;

import java.lang.reflect.Field;

public class ViewSingleItemActivity extends AppCompatActivity implements ColorPicker.OnColorChangedListener{


    RelativeLayout mRelativeLayout;
    ColorData mColorData = null;
    DatabaseOperations mDatabaseOp;
    RelativeLayout mParentLayout;
    ColorPicker.OnColorChangedListener mColorListener;
    ColorPicker.ColorPickerView mColorPickerView;
    Activity activity;
    ImageView mItemImage;
    SendElementOnClickListener mSendOnClickListener;
    BluetoothDevice mNewLampDevice;
    ClassicBTConnection mMainLampConnection;
    static final int REQUEST_DEVICE_AQUIRE = 111;
    private ShareActionProvider mShareActionProvider;
    TextView mItemName;
    final static String TAG = "ViewSingleItemActivity";
    OnOverflowClickListener mOverflowListener;
    RelativeLayout mToolbar;
    LinearLayout mBackToBluer;
    LinearLayout mBottomToBluer;
    FloatingActionButton mSendElement;
    LinearLayout mMainLayout;

    //---------------------------------------------------------
    //AlertDialog
    //---------------------------------------------------------
    AlertDialog.Builder mRemoveDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_view_single_item);
        setContentView(R.layout.activity_view_single_item_extended_background);
        this.activity = this;

        //-----------------------------------------------------
        //ACTION BAR SETUP
        //-----------------------------------------------------
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        //actionBar.setLogo(R.drawable.ic_luogo);

        mItemImage = (ImageView) findViewById(R.id.single_view_image);

        int width = mItemImage.getWidth();

        mItemImage.setMinimumWidth(width);

        mItemName = (TextView) findViewById(R.id.single_view_item_name);


        //Inizializza la connessione principare con la lampada
        mMainLampConnection = null;

        //Recupera il dispositivo connesso
        mNewLampDevice = ((MyBaseApplication)this.getApplicationContext()).getLampDevice();

        //
        mToolbar = (RelativeLayout) findViewById(R.id.relative_toolbar);

        mBackToBluer = (LinearLayout) findViewById(R.id.top_view);

        mBottomToBluer = (LinearLayout) findViewById(R.id.bottomlayout);

        mMainLayout = (LinearLayout) findViewById(R.id.single_view_main_layout);

        //Creazione di un database
        mDatabaseOp = new DatabaseOperations(this);

        Intent intent = getIntent();
        //Ottieni il colore scelto dall'utente
        mColorData = (ColorData) intent.getSerializableExtra("colorItemSelected");

        if(mColorData != null)
            if (mColorData.getName() != null)
                mItemName.setText(mColorData.getName());

        //-------------------------------------------------------------------------
        // COLOR PICKER INIT
        //-------------------------------------------------------------------------
        final RoundedFrameLayout colorPicker = (RoundedFrameLayout) findViewById(R.id.colorPicker);

        colorPicker.setCornerRadius(10);

        int [] colorArray = Utils.fromHexStringToColorArray(mColorData.getHexData());

        mColorPickerView = new ColorPicker.ColorPickerView(activity, this, Color.WHITE, Color.BLACK, colorArray);

        colorPicker.addView(mColorPickerView);

        this.setTitle(mColorData.getPosition());

        ImageView image  = (ImageView) findViewById(R.id.single_view_image);

        ///----------------------------------------------------------------------------
        //AlertDialog INIT
        //----------------------------------------------------------------------------

        mRemoveDialogBuilder = new AlertDialog.Builder(this);
        mRemoveDialogBuilder.setTitle("Rimuovi");
        mRemoveDialogBuilder.setMessage("Sicuro di rimuovere l'elemento?");
        mRemoveDialogBuilder.setPositiveButton("Elimina", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteThis();
            }
        });
        mRemoveDialogBuilder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        //----------------------------------------------------------------------------
        //OVERFLOW MENU
        //----------------------------------------------------------------------------
        ImageButton overflowMenu = (ImageButton) findViewById(R.id.overflow_button);
        mOverflowListener = new OnOverflowClickListener(this, mColorData);
        overflowMenu.setOnClickListener(mOverflowListener);

        //----------------------------------------------------------------------------
        //SEND ELEMENT TO LAMP
        //----------------------------------------------------------------------------
        mSendElement = (FloatingActionButton) findViewById(R.id.send_memory);
        mSendElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNewLampDevice == null){
                    getBluetoothDevice();
                }
                else{
                    sendToLamp();
                }
            }
        });


        ImageButton editElement = (ImageButton) findViewById(R.id.edit_button);
        editElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editData();
            }
        });

        //************************************************
        // Effetto sfumato background
        //************************************************

        //blurView(mBackToBluer);
        //blurView(mBottomToBluer);
        //blurView(mMainLayout);
        uploadImage();

    }

    private void blurView(View view){
        if(mColorData.getImagePath() != null)
            if(!mColorData.getImagePath().equals("")){
                Bitmap bitmapToBlur = BitmapFactory.decodeFile(mColorData.getImagePath());
                ImageHelper.blur(bitmapToBlur, view, getActivity());
        }
        else{
                Bitmap bitmapToBlur = BitmapFactory.decodeResource(getResources(),R.drawable.placeholder);
                ImageHelper.blur(bitmapToBlur, mBackToBluer, getActivity());
            }
    }

    private void setfavorite() {
        boolean actualFavState = !mColorData.isFavorite();
        mColorData.setFavorite(actualFavState);
        mDatabaseOp.update(mColorData);
        mOverflowListener.updateColorState(mColorData);
    }

    private void deleteThis() {
        if(mDatabaseOp.removeData(mColorData)){
            Toast.makeText(this,"elemento eliminato con successo",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            if(getParent() == null){
                setResult(Activity.RESULT_OK, intent);
            }
            else{
                getParent().setResult(Activity.RESULT_OK, intent);
            }
            finish();
        }
    }

    public void uploadImage(){
        if(mColorData != null && mColorData.getImagePath() != null){
            Log.i(TAG, mColorData.getImagePath());
            //ImageHelper.setFullImageFromFilePath(getApplicationContext(), mColorData.getImagePath(), mItemImage);
            ImageHelper.setRoundedImageFromFilePath(getApplicationContext(), mColorData.getImagePath(), mItemImage);
        }
    }

    public void editData(){
        Intent intent = new Intent(getActivity(), AddNewItemActivity.class);
        intent.putExtra("colorItemToEdit", mColorData);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_single_item, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        mShareActionProvider.setShareIntent(getDefaultShare());
        return true;
    }

    private Intent getDefaultShare(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        return shareIntent;
    }

    private void setShareIntent(Intent shareIntent){
        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(shareIntent);
        }
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

    public Activity getActivity() {
        return activity;
    }

    @Override
    public void colorChanged(String key, int color) {
        mSendElement.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private class SendElementOnClickListener implements Button.OnClickListener{
        @Override
        public void onClick(View v) {
            if(mNewLampDevice == null){
                getBluetoothDevice();
            }
            else{
                sendToLamp();
            }
        }
    }

    public void toastIt(String testo){
        Toast.makeText(this.getApplicationContext(),testo, Toast.LENGTH_LONG).show();
    }
    public void getBluetoothDevice(){
        Intent intent;
        intent = new Intent(getActivity(), LeDeviceScanActivity.class);
        startActivityForResult(intent, REQUEST_DEVICE_AQUIRE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        //Controlla la risposta a qui stiamo rispondendo
        if(requestCode == REQUEST_DEVICE_AQUIRE){
            //Controlla che sia un successo
            if(resultCode == RESULT_OK){
                Bundle bundle = data.getExtras();
                BluetoothDevice selectedDevice = (BluetoothDevice) bundle.get("BLUETOOTH_DEVICE");
                if(selectedDevice != null){
                    setMainLampDevice(selectedDevice);
                    sendToLamp();
                }
            }
        }
    }

    public void setMainLampDevice(BluetoothDevice newDevice){
        if(newDevice != null){
            ((MyBaseApplication)this.getApplicationContext()).setLampDevice(newDevice);
            mNewLampDevice = newDevice;
        }
    }

    /**
     * Invia i dati alla lampada connessa se è connessa
     */
    public void sendToLamp(){
        if(mNewLampDevice != null){
            if(mMainLampConnection == null)
                mMainLampConnection = new ClassicBTConnection(mNewLampDevice);

            String dataToSend = mColorData.getHexData();
            mMainLampConnection.write(dataToSend.getBytes());
        }
    }


    public class OnOverflowClickListener implements View.OnClickListener{
        Context mContext;
        ColorData mColor;
        PopupMenu mPopupMenu;

        public OnOverflowClickListener(Context context, ColorData colorData){
            mContext = context;
            mColor = colorData;
        }

        public void updateColorState(ColorData newColor){
            this.mColor = newColor;
            setFavorite();
        }

        protected void setFavorite(){
            if(mColor.isFavorite()){
                mPopupMenu.getMenu().findItem(R.id.item_overflow_favorite).setTitle(R.string.favorite_item_not);
            }
            else{
                mPopupMenu.getMenu().findItem(R.id.item_overflow_favorite).setTitle(R.string.favorite_item);
            }
        }

        @Override
        public void onClick(View v) {
             mPopupMenu = new PopupMenu(mContext, v){
                public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item){
                    switch(item.getItemId()){
                        case R.id.item_overflow_remove:
                            mRemoveDialogBuilder.show();
                            return true;

                        case R.id.item_overflow_favorite:
                            setfavorite();
                            return true;

                        default:
                            return super.onMenuItemSelected(menu, item);
                    }
                }
            };

            mPopupMenu.inflate(R.menu.item_overflow_menu);
            // Force icons to show
            Object menuHelper;
            Class[] argTypes;
            try {
                Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                fMenuHelper.setAccessible(true);
                menuHelper = fMenuHelper.get(mPopupMenu);
                argTypes = new Class[] { boolean.class };
                menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
            } catch (Exception e) {
                // Possible exceptions are NoSuchMethodError and NoSuchFieldError
                //
                // In either case, an exception indicates something is wrong with the reflection code, or the
                // structure of the PopupMenu class or its dependencies has changed.
                //
                // These exceptions should never happen since we're shipping the AppCompat library in our own apk,
                // but in the case that they do, we simply can't force icons to display, so log the error and
                // show the menu normally.
                Log.w(TAG, "error forcing menu icons to show", e);
                mPopupMenu.show();
                return;
            }

            setFavorite();

            mPopupMenu.show();
        }
    }

}
