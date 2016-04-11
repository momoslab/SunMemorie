package com.example.yassine.sunlamp;

import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.yassine.sunlamp.Adapter.ColorDataAdapter;
import com.example.yassine.sunlamp.Model.ColorData;
import com.example.yassine.sunlamp.R;

import java.util.List;

public class SearchActivity extends AppCompatActivity implements AbsListView.OnItemClickListener{

    //---------------------------------------------------------
    //Inizializzazione variabili
    //---------------------------------------------------------

    ListView mListResult;
    DatabaseOperations db;
    private ArrayAdapter mAdapter;
    private final static String LOG = "SearchActivity";

    List<ColorData> mListOfColors = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ActionBar actionBar = getSupportActionBar();
        Drawable SearchActionBar = getResources().getDrawable(R.drawable.search_action_bar);
        actionBar.setBackgroundDrawable(SearchActionBar);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mListResult = (ListView) findViewById(R.id.list_result);
        mListResult.setOnItemClickListener(this);

        //---------------------------------------------------------
        //Database operation per recuperare la lista
        //---------------------------------------------------------
        db = new DatabaseOperations(this);
        mListOfColors = db.getAllData();
        Log.e(LOG, "mListoOfColors --> " + mListOfColors.size());

    }

    public void onStart() {
        super.onStart();
        mAdapter = new ColorDataAdapter(this, mListOfColors);
        mListResult.setAdapter(mAdapter);
    }

    protected void doMySearch(String query){
        if(query.equals("")){
            mListOfColors.clear();
            mListOfColors.addAll(db.getAllData());
            mAdapter.notifyDataSetChanged();
            return;
        }
        mListOfColors.clear();
        mListOfColors.addAll(db.selectFromDatabase(query));
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setBackgroundResource(R.color.tabbed_color);

        SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query != null){
                    doMySearch(query);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null ) {
                    doMySearch(newText);
                    return true;
                }
                return false;
            }
        };
        searchView.setOnQueryTextListener(onQueryTextListener);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ViewSingleItemActivity.class);
        intent.putExtra("colorItemSelected", mListOfColors.get(position));
        this.startActivity(intent);
    }
}
