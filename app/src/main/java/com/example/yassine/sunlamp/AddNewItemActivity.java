package com.example.yassine.sunlamp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yassine.sunlamp.Model.ColorData;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.security.Provider;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.util.Log;

public class AddNewItemActivity extends AppCompatActivity {

    private static final String LOG = "AddNewItemActivity";

    //---------------------------------------------------------
    //Acquisizione Foto
    //---------------------------------------------------------

    private final static String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";
    private final static String CAPTURED_PHOTO_URI_KEY = "mCapturedImageURI";

    private String mCurrentPhotoPath = null;
    private Uri mCapturedImageURI = null;
    private Uri mCroppedImageURI = null;

    static final int REQUEST_TAKE_PHOTO = 11111;


    //---------------------------------------------------------
    //Gestione Geolocalizzazione
    //---------------------------------------------------------
    private OnPositionClick mPositionClickListener;
    private LocationManager mLocationManager;
    private String mProvider;

    ImageView mItemThumbnail;
    EditText mItemName;
    EditText mItemDescription;
    EditText mItemPosition;
    EditText mItemActualDate;
    ImageButton mPositionButton;
    DatabaseOperations mDatabase;
    FloatingActionButton mFloatActionButton;
    ColorData mColorDataToEdit = null;
    boolean mToEdit = false;
    AlertDialog mAlertDialog;


    RelativeLayout mTopLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        //Inizializzo la Geoloacalizzazione
        mLocationManager =(LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        mProvider = mLocationManager.getBestProvider(criteria,false);
        mDatabase = new DatabaseOperations(this);
        mItemThumbnail = (ImageView) findViewById(R.id.item_thumbnail);
        mFloatActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        Intent intent = getIntent();
        //Ottieni il colore scelto dall'utente
        mColorDataToEdit = (ColorData) intent.getSerializableExtra("colorItemToEdit");

        mAlertDialog = new AlertDialog.Builder(AddNewItemActivity.this).create();
        mAlertDialog.setTitle("Oops....");
        mAlertDialog.setMessage("Ci sono alcune informazioni mancanti...");

        mAlertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                onSave();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_clear_black_24dp));
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setIcon(R.drawable.luogo);

        mPositionButton = (ImageButton) findViewById(R.id.button_get_position);
        mItemName = (EditText) findViewById(R.id.item_name);
        mItemDescription = (EditText) findViewById(R.id.item_description);
        mItemPosition = (EditText) findViewById(R.id.item_position);
        mItemActualDate = (EditText) findViewById(R.id.item_date);
        mTopLayout = (RelativeLayout) findViewById(R.id.toplayout);

        mPositionClickListener = new OnPositionClick();

        if(mColorDataToEdit != null){
                if(mColorDataToEdit.getCreation_time() != null && !mColorDataToEdit.getCreation_time().equals("")){
                mItemActualDate.setText(mColorDataToEdit.getCreation_time());
                }
                else {
                    mItemActualDate.setText(Utils.getActualDate());
                }
            mToEdit = true;
            if(mColorDataToEdit.getImagePath() != null)
                ImageHelper.setRoundedImageFromFilePath(getApplicationContext(), mColorDataToEdit.getImagePath(), mItemThumbnail);
                mFloatActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_white_36dp));

            if(mColorDataToEdit.getName() != null)
                mItemName.setText(mColorDataToEdit.getName());
            if(mColorDataToEdit.getDescription() != null){
                mItemDescription.setText(mColorDataToEdit.getDescription());
            }
            if(mColorDataToEdit.getPosition() != null){
                mItemPosition.setText(mColorDataToEdit.getPosition());
            }
           // if(mColorDataToEdit.getHexData() != null)
        }
        else{
            mItemActualDate.setText(Utils.getActualDate());
        }
        mPositionButton.setOnClickListener(mPositionClickListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            onSave();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (mCurrentPhotoPath != null) {
            savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY, mCurrentPhotoPath);
        }
        if (mCapturedImageURI != null) {
            savedInstanceState.putString(CAPTURED_PHOTO_URI_KEY, mCapturedImageURI.toString());
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {
            mCurrentPhotoPath = savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY);
        }
        if (savedInstanceState.containsKey(CAPTURED_PHOTO_URI_KEY)) {
            mCapturedImageURI = Uri.parse(savedInstanceState.getString(CAPTURED_PHOTO_URI_KEY));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Start the camera by dispatching a camera intent.
     */
    protected void dispatchTakePictureIntent() {

        // Check if there is a camera.
        Context context = getApplicationContext();
        PackageManager packageManager = context.getPackageManager();
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false){
            Toast.makeText(getApplicationContext(), "This device does not have a camera.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // Camera exists? Then proceed...
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go.
            // If you don't do this, you may get a crash in some devices.
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast toast = Toast.makeText(this, "There was a problem saving the photo...", Toast.LENGTH_SHORT);
                toast.show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri fileUri = Uri.fromFile(photoFile);
                setCapturedImageURI(fileUri);
                setCurrentPhotoPath(fileUri.getPath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        getCapturedImageURI());
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * The activity returns with the photo.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if(resultCode == Activity.RESULT_OK) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int windowW = displayMetrics.widthPixels;
                Crop.of(getCapturedImageURI(), getCapturedImageURI()).withAspect(windowW,330).start(this);
                // Show the full sized image.

            } else {
                Toast.makeText(this , "Operazione fallita!", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else if(requestCode == Crop.REQUEST_CROP){
            if(requestCode == Activity.RESULT_OK){
                setCroppedImageURI(Crop.getOutput(data));
                //setCapturedImageURI(getCroppedPhotoURI());
                setCurrentPhotoPath(getCapturedImageURI().getPath());
                ImageHelper.setRoundedImageFromFilePath(getApplicationContext(), getCroppedPhotoURI().getPath(), mItemThumbnail);
                mFloatActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_white_24dp));
                addPhotoToGallery();

            }else if (resultCode == Crop.RESULT_ERROR) {
                Log.i(LOG, Crop.getError(data).getMessage());
            }

            else {
                ImageHelper.setRoundedImageFromFilePath(getApplicationContext(), getCurrentPhotoPath(), mItemThumbnail);
                mFloatActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_white_24dp));
            }

        }



    }

    protected File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        //file temporaneo
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        setCurrentPhotoPath("file:" + image.getAbsolutePath());
        return image;
    }

    public String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public Uri getCroppedPhotoURI() {
        return mCroppedImageURI;
    }

    public void setCurrentPhotoPath(String mCurrentPhotoPath) {
        this.mCurrentPhotoPath = mCurrentPhotoPath;
    }

    public Uri getCapturedImageURI() {
        return mCapturedImageURI;
    }

    public void setCapturedImageURI(Uri mCapturedImageURI) {
        this.mCapturedImageURI = mCapturedImageURI;
    }

    public void setCroppedImageURI(Uri imageURI) {
        this.mCroppedImageURI = imageURI;
    }

    /**
     * Add the picture to the photo gallery.
     * Must be called on all camera images or they will
     * disappear once taken.
     */
    protected void addPhotoToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(getCurrentPhotoPath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    void onSave(){
        String name;
        String description;
        String position;
        if (mItemName.getText() == null || mItemName.getText().equals("")) {
            mAlertDialog.show();
            return;
        }
        if (mItemDescription.getText() == null || mItemDescription.getText().equals("")) {
            mAlertDialog.show();
            return;
        }
        if (mItemPosition.getText() == null || mItemPosition.getText().equals("")) {
            mAlertDialog.show();
            return;
        }
        name = mItemName.getText().toString();
        description = mItemDescription.getText().toString();
        position = mItemPosition.getText().toString();
        String date = mItemActualDate.getText().toString();

        if(mToEdit){
            mColorDataToEdit.setName(name);
            mColorDataToEdit.setDescription(description);
            mColorDataToEdit.setPosition(position);
            mColorDataToEdit.setCreation_time(date);
            if(mCurrentPhotoPath != null && !mCurrentPhotoPath.equals("")){
                mColorDataToEdit.setImagePath(getCurrentPhotoPath());
            }
            mDatabase.update(mColorDataToEdit);
            finishNow();
        }
        else{
            ColorData data = new ColorData();
            String hexData =
                            "35,d71,b79;" +
                            "b39,d74,b7d;" +
                            "b3e,d79,b81;" +
                            "b3b,d75,b7d;" +
                            "bf5,c3a,b43;" +
                            "bc8,c15,b1e;" +
                            "bcd,c18,b1f;" +
                            "bf8,c3c,b42;" +
                            "bf7,c3a,b40;" +
                            "bee,c33,b38;" +
                            "bca,c15,b1b;" +
                            "b9b,cef,af3;" +
                            "af6,c3d,b3f;" +
                            "b7c,da9,baa;" +
                            "bbd,de2,be4;" +
                            "b1a,e35,c38;" +
                            "cc9,ec5,cc3;" +
                            "c6e,da4,ba4;" +
                            "bf8,c31,b2d;" +
                            "b38,d55,b4a;" +
                            "b8f,d99,b8c;" +
                            "b2b,e31,c27;" +
                            "c6d,e6d,c66;" +
                            "c59,d87,b82;" +
                            "bf9,d13,c0a;" +
                            "cea,d0a,c05;" +
                            "c5d,d89,b85;" +
                            "b2e,e41,c3b;" +
                            "c02,ff3,ce6;" +
                            "ce4,ed3,cc3;" +
                            "c79,e75,c64;" +
                            "c46,e4d,c3d;" +
                            "c32,d61,b53;" +
                            "b84,cc8,ab8;" +
                            "a65,cab,a9a;" +
                            "adc,b36,a24;" +
                            "a43,bad,99a;" +
                            "9cd,b22,a0a;" +
                            "a44,c7f,a62;" +
                            "a5f,bb7,9a0;" +
                            "97f,af1,8dd;" +
                            "84d,b9e,983;" +
                            "9c9,a2b,914;" +
                            "9ff,983,874;" +
                            "85d,9fb,7f5;" +
                            "7ee,977,86a;" +
                            "8b1,948,83c;" +
                            "87c,918,80e;" +
                            "898,931,829;" +
                            "8fb,988,87f;" +
                            "869,ae1,8d6;" +
                            "801,b60,952;" +
                            "9c9,cdf,aca;" +
                            "a60,e34,c1c;" +
                            "c74,f1a,d05;" +
                            "d90,1003,ee8;" +
                            "d70,11bd,e9b;" +
                            "e4c,1278,f53;" +
                            "f01,1313,10e9;" +
                            "f56,142a,11f7;" +
                            "107e,151c,12e4;" +
                            "113a,16ba,127d;" +
                            "12b0,161d,13db;" +
                            "12ad,17ee,13a6;" +
                            "13cf,170f,14c6;" +
                            "137a,17c7,137e;" +
                            "1370,188f,1442;" +
                            "14fa,1805,15b5;" +
                            "1419,191c,15c8;" +
                            "1440,193e,15e8;" +
                            "1425,1af2,1596;" +
                            "154b,193e,15e5;" +
                            "1429,1921,15c9;" +
                            "144a,193c,15e2;" +
                            "14c2,199e,1541;" +
                            "15d4,19a9,154b;" +
                            "1535,1afd,159b;" +
                            "1568,1b04,1798;" +
                            "16d8,1b69,17fa;";

            data.setData(hexData);
            data.setName(name);
            data.setDescription(description);
            data.setPosition(position);
            data.setImagePath(getCurrentPhotoPath());
            data.setCreation_time(date);
            mDatabase.insertData(data);
            finishNow();
         }
    }

    private void finishNow() {
        Intent intent = new Intent();
        if(getParent() == null){
            setResult(Activity.RESULT_OK, intent);
        }
        else{
            getParent().setResult(Activity.RESULT_OK, intent);
        }
        finish();
    }

    /*******************************************************
     * GeoLocation Stuff start here
     ********************************************************/

    public void getActualPosition(){
        mLocationManager.requestLocationUpdates(mProvider, 400, 1, mLocationListener);

    }


    /*********************************************************
     * Geolocation Listener per gestire la geolocalizzazione
     * @param location
     **********************************************************/
    private void getAddressFromPosition(Location location){
        Log.e(LOG, "getAddressFromPosition ");
        double lng = location.getLongitude();
        double lat = location.getLatitude();
        Geocoder geocoder = new Geocoder(this , Locale.getDefault());
        Log.e(LOG, "getAddressFromPosition --> geoCoder lang - lat " + lng +" "+ lat );
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geocoder.getFromLocation(lat,lng,1);
            Log.e(LOG, "Address lenght --> " + address.size());
            if(address.size() > 0) {
                Address singleAddress = address.get(0);
                builder.append(singleAddress.getCountryName());
                builder.append(" ");
                builder.append(singleAddress.getLocality());

            }
            for(Address adrs: address){
                for(int i = 0; i < adrs.getMaxAddressLineIndex(); i++)
                    Log.e(LOG, "Address = " + adrs.getAddressLine(i));
            }
            /*
            if(address != null){
                int maxLines = address.get(0).getMaxAddressLineIndex();
                for(int i = 0; i < maxLines; i++){
                    String addressString = address.get(0).getAddressLine(i);
                    builder.append(addressString);
                    builder.append(" ");
                }
            }*/
        }catch (IOException e){
            Log.e(LOG, e.getMessage());
        }
        mItemPosition.setText(builder);
    }

    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(location != null){
                Log.e(LOG, "Location != null");
                getAddressFromPosition(location);
                mLocationManager.removeUpdates(this);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void onResume(){
        super.onResume();
        mLocationManager.removeUpdates(mLocationListener);
    }

    public void onPause(){
        super.onPause();
        mLocationManager.removeUpdates(mLocationListener);
    }

    /*******************************************************
     * GeoLocation Stuff start here
     ********************************************************/
    class OnPositionClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
           // Toast.makeText(getContext(),"On position click",Toast.LENGTH_LONG).show();
            Log.e(LOG,"Tasto premuto");
            getActualPosition();
        }
    }


}