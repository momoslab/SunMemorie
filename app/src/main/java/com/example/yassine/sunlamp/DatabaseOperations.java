package com.example.yassine.sunlamp;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.yassine.sunlamp.Model.ColorData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by YassIne on 09/08/2015.
 */
public class DatabaseOperations extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String LOG = "DatabaseOperations";
    private Context context;
    private SQLiteDatabase mDatabase;

    public String CREATE_QUERY =
                            "CREATE TABLE " + TableData.TableInfo.TABLE_NAME +" ("
                            + TableData.TableInfo.TABLE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT ,"
                            + TableData.TableInfo.FAVORITE + " INTEGER ,"
                            + TableData.TableInfo.HEXDATA + " TEXT ,"
                            + TableData.TableInfo.DESCRIPTION + " TEXT ,"
                            + TableData.TableInfo.DATA_NAME + " TEXT ,"
                            + TableData.TableInfo.CREATION_TIME + " TEXT ,"
                            + TableData.TableInfo.POSITION + " TEXT ,"
                            + TableData.TableInfo.IMAGE +" TEXT "
                            + ") ";

    private static final String FTS_VIRTUAL_TABLE = "FastColorTable";

    private static final String CREATE_FTS_DATABASE =
            "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE + " USING fts3( "
                    + TableData.TableInfo.TABLE_ID + ", "
                    + TableData.TableInfo.FAVORITE + ", "
                    + TableData.TableInfo.HEXDATA + ", "
                    + TableData.TableInfo.DESCRIPTION + ", "
                    + TableData.TableInfo.DATA_NAME + ", "
                    + TableData.TableInfo.CREATION_TIME + ", "
                    + TableData.TableInfo.POSITION + ", "
                    + TableData.TableInfo.IMAGE + ");";

    private static final String POPULATE_FST = "INSERT INTO " + FTS_VIRTUAL_TABLE + " SELECT DISTINCT * FROM " + TableData.TableInfo.TABLE_NAME;


    public DatabaseOperations(Context context){
        super(context, TableData.TableInfo.DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        mDatabase = getReadableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase database){
        //Crea le tabelle richieste
        onUpgrade(database, DATABASE_VERSION, 8);
        database.execSQL(CREATE_QUERY);
        database.execSQL(CREATE_FTS_DATABASE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        if(newVersion > oldVersion){
            database.execSQL("DROP TABLE IF EXISTS "+ TableData.TableInfo.TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
        }
    }

    /**
     * carica tutti i dati dal database
     * @return
     */
    public  List<ColorData> getAllData(){
        List<ColorData> listOfData = new ArrayList<>();
        String SELECT_QUERY = "SELECT * FROM " + TableData.TableInfo.TABLE_NAME;
        Log.e(LOG , SELECT_QUERY);

        Cursor cursor = mDatabase.rawQuery(SELECT_QUERY, null);

        if(cursor.moveToFirst()){
            do{
                ColorData data = new ColorData();

                data.setData(cursor.getString(cursor.getColumnIndex(TableData.TableInfo.HEXDATA)));

                data.setId(cursor.getInt(cursor.getColumnIndex(TableData.TableInfo.TABLE_ID)));

                data.setFavorite(cursor.getInt(cursor.getColumnIndex(TableData.TableInfo.FAVORITE)) > 0);

                data.setDescription(cursor.getString(cursor.getColumnIndex(TableData.TableInfo.DESCRIPTION)));

                data.setName(cursor.getString(cursor.getColumnIndex(TableData.TableInfo.DATA_NAME)));

                data.setPosition(cursor.getString(cursor.getColumnIndex(TableData.TableInfo.POSITION)));

                data.setImagePath(cursor.getString(cursor.getColumnIndex(TableData.TableInfo.IMAGE)));

                data.setCreation_time(cursor.getString(cursor.getColumnIndex(TableData.TableInfo.CREATION_TIME)));

                listOfData.add(data);

            } while (cursor.moveToNext());
        }
        return listOfData;
    }

    /**
     * ottiene gli elementi favoriti
     * @return
     */
    public  List<ColorData> getFavoriteData(){
        List<ColorData> listOfData = new ArrayList<>();
        String SELECT_QUERY = "SELECT * FROM " + TableData.TableInfo.TABLE_NAME +" WHERE " + TableData.TableInfo.FAVORITE + " = 1 ";
        Log.e(LOG, SELECT_QUERY);

        Cursor cursor = mDatabase.rawQuery(SELECT_QUERY, null);

        if(cursor.moveToFirst()){
            do{
                ColorData data = new ColorData();

                data.setData(cursor.getString(cursor.getColumnIndex(TableData.TableInfo.HEXDATA)));

                data.setId(cursor.getInt(cursor.getColumnIndex(TableData.TableInfo.TABLE_ID)));

                data.setFavorite(cursor.getInt(cursor.getColumnIndex(TableData.TableInfo.FAVORITE)) > 0);

                data.setDescription(cursor.getString(cursor.getColumnIndex(TableData.TableInfo.DESCRIPTION)));

                data.setName(cursor.getString(cursor.getColumnIndex(TableData.TableInfo.DATA_NAME)));

                data.setCreation_time(cursor.getString(cursor.getColumnIndex(TableData.TableInfo.CREATION_TIME)));

                data.setPosition(cursor.getString(cursor.getColumnIndex(TableData.TableInfo.POSITION)));

                data.setImagePath(cursor.getString(cursor.getColumnIndex(TableData.TableInfo.IMAGE)));

                data.setCreation_time(cursor.getString(cursor.getColumnIndex(TableData.TableInfo.CREATION_TIME)));

                listOfData.add(data);

            } while (cursor.moveToNext());
        }
        return listOfData;
    }

    /**
     * inserisce un dato nel data base
     *
     * @param data : dato da inserire nel database
     */
    public long insertData(ColorData data){
        Log.e(LOG,"actual time creation: " + getDateTime());

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(TableData.TableInfo.HEXDATA, data.getHexData());
        values.put(TableData.TableInfo.FAVORITE, data.isFavorite() ? 1 : 0);
        values.put(TableData.TableInfo.CREATION_TIME, data.getCreation_time());
        values.put(TableData.TableInfo.DESCRIPTION, data.getDescription());
        values.put(TableData.TableInfo.DATA_NAME, data.getName());
        values.put(TableData.TableInfo.POSITION, data.getPosition());
        values.put(TableData.TableInfo.IMAGE, data.getImagePath());
        long data_id = database.insert(TableData.TableInfo.TABLE_NAME, null, values);

        populateFST();

        return data_id;
    }

    private void populateFST() {
        mDatabase.execSQL("DELETE FROM " + FTS_VIRTUAL_TABLE);
        Log.e(LOG, "DELETE FROM " + FTS_VIRTUAL_TABLE);
        mDatabase.execSQL(POPULATE_FST);
        Log.e(LOG, "POPULATE_FST ");

    }

    /**
     * Seleziona gli elementi interessati
     * @return
     */
    public  List<ColorData> selectFromDatabase(String query){
        populateFST();

        List<ColorData> listOfData = new ArrayList<>();
        String SELECT_QUERY = "SELECT * FROM " + FTS_VIRTUAL_TABLE +" WHERE " + FTS_VIRTUAL_TABLE + " MATCH '" + query +"*';";
        Log.e(LOG , SELECT_QUERY);

        //mDatabase = this.getReadableDatabase();

        Cursor cursor = mDatabase.rawQuery(SELECT_QUERY, null);

        if(cursor == null) return null;

        if(cursor.moveToFirst()){
            do{
                ColorData data = new ColorData();

                data.setData(cursor.getString(cursor.getColumnIndex(TableData.TableInfo.HEXDATA)));

                data.setId(cursor.getInt(cursor.getColumnIndex(TableData.TableInfo.TABLE_ID)));

                data.setFavorite(cursor.getInt(cursor.getColumnIndex(TableData.TableInfo.FAVORITE)) > 0);

                data.setDescription(cursor.getString(cursor.getColumnIndex(TableData.TableInfo.DESCRIPTION)));

                data.setName(cursor.getString(cursor.getColumnIndex(TableData.TableInfo.DATA_NAME)));

                data.setCreation_time(cursor.getString(cursor.getColumnIndex(TableData.TableInfo.CREATION_TIME)));

                data.setPosition(cursor.getString(cursor.getColumnIndex(TableData.TableInfo.POSITION)));

                data.setImagePath(cursor.getString(cursor.getColumnIndex(TableData.TableInfo.IMAGE)));

                listOfData.add(data);

            } while (cursor.moveToNext());
        }
        Log.e(LOG, "List of data lenght " + listOfData.size());
        return listOfData;
    }


    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * Chiude la conessione con il database
     */
    public void closeDatabase(){
        SQLiteDatabase database = this.getReadableDatabase();
        if (database != null && database.isOpen())
            database.close();
    }

    /**
     * Serve ad rimuovere un elemento dall'array
     */
    public boolean removeData(ColorData color){

        SQLiteDatabase database = this.getReadableDatabase();

        return database.delete( TableData.TableInfo.TABLE_NAME, TableData.TableInfo.TABLE_ID + "=" + color.getId(), null) > 0;

    }

    public void update(ColorData data) {

        String strFilter = TableData.TableInfo.TABLE_ID + " = " + data.getId();

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(TableData.TableInfo.HEXDATA, data.getHexData());
        values.put(TableData.TableInfo.FAVORITE, data.isFavorite() ? 1 : 0);
        values.put(TableData.TableInfo.CREATION_TIME, data.getCreation_time());
        values.put(TableData.TableInfo.DESCRIPTION, data.getDescription());
        values.put(TableData.TableInfo.DATA_NAME, data.getName());
        values.put(TableData.TableInfo.POSITION, data.getPosition());
        values.put(TableData.TableInfo.IMAGE, data.getImagePath());
        database.update(TableData.TableInfo.TABLE_NAME, values, strFilter, null);

    }
}
