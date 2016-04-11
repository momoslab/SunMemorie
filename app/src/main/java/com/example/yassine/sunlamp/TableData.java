package com.example.yassine.sunlamp;

import android.provider.BaseColumns;

/**
 * Created by YassIne on 09/08/2015.
 */
public class TableData {

    /**
     * Costruttore di default
     */
    public TableData(){

    }

    /**
     * Contiente il nome delle colonne usate nel database
     */
    public static abstract class TableInfo implements BaseColumns {
        //public final String DATE = "date" ;
        public static final String TABLE_ID = "_id" ;
        public static final String HEXDATA = "hex_data" ;
        public static final String TABLE_NAME = "data_info" ;
        public static final String DATABASE_NAME = "bluetooth_data" ;
        public static final String CREATION_TIME = "time";
        public static final String FAVORITE = "favorite";
        public static final String DESCRIPTION = "description";
        public static final String DATA_NAME = "name";
        public static final String POSITION = "position";
        public static final String IMAGE = "image";
    }

}
