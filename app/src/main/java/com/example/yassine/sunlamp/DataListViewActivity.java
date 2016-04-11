package com.example.yassine.sunlamp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.yassine.sunlamp.Adapter.ColorDataAdapter;
import com.example.yassine.sunlamp.Model.ColorData;

import java.util.List;

public class DataListViewActivity extends AppCompatActivity {
    private ListView listOfData;
    private List<ColorData> colorsList;
    private Drawable imageToround;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_list_view);

        listOfData = (ListView) findViewById(R.id.list);

        //recupero i dati dal database

        DatabaseOperations db = new DatabaseOperations(this);

        colorsList = db.getAllData();

        //setto l'adapter della listview
        listOfData.setAdapter(new ColorDataAdapter(this,colorsList));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_list_view, menu);
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
}
