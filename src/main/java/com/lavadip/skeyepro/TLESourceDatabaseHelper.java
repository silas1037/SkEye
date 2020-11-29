package com.lavadip.skeyepro;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.FileInputStream;
import java.io.IOException;

public final class TLESourceDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_TABLE_CREATE = "CREATE TABLE tle_sources (id INTEGER PRIMARY KEY,name TEXT, url TEXT, enabled INTEGER,update_freq_days INTEGER, last_update_timestamp INTEGER,data TEXT);";
    static final String DB_TABLE_NAME = "tle_sources";
    private static final int DB_VERSION = 1;
    private final String visualTLEString;

    private static String readStringFromFile(Context context, String fileName) {
        try {
            AssetFileDescriptor visualFD = context.getAssets().openFd(fileName);
            int length = (int) visualFD.getLength();
            FileInputStream is = visualFD.createInputStream();
            byte[] streamData = new byte[length];
            int totalReadBytes = 0;
            int readBytes = 0;
            while (totalReadBytes < length && readBytes >= 0) {
                readBytes = is.read(streamData, totalReadBytes, length - totalReadBytes);
                totalReadBytes += readBytes;
            }
            return new String(streamData);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public TLESourceDatabaseHelper(Context context) {
        super(context, DB_TABLE_NAME, (SQLiteDatabase.CursorFactory) null, 1);
        Log.d("SKEYE", "got tle string");
        this.visualTLEString = readStringFromFile(context, "sat_tle.jet");
    }

    public void onCreate(SQLiteDatabase db) {
        Log.d("SKEYE", "Creating db table tle_sources version:1");
        db.execSQL(DB_TABLE_CREATE);
        Log.d("SKEYE", "Initialising db");
        String[] names = {"Celestrak Visual (100 brightest)"};
        String[] urls = {"http://celestrak.com/NORAD/elements/visual.txt"};
        int[] enableds = {1};
        int[] update_freq_days = {7};
        long[] last_update_timestamp = new long[1];
        String[] data = {this.visualTLEString};
        for (int i = 0; i < names.length; i++) {
            db.execSQL("INSERT INTO tle_sources (name, url, enabled, update_freq_days, last_update_timestamp, data) VALUES (?,?,?,?,?,?);", new Object[]{names[i], urls[i], Integer.valueOf(enableds[i]), Integer.valueOf(update_freq_days[i]), Long.valueOf(last_update_timestamp[i]), data[i]});
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("SKEYE", "Updating db from version:" + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS tle_sources");
        onCreate(db);
    }
}
