package com.lavadip.skeye.config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public final class LocationDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DB_TABLE_CREATE = "CREATE TABLE locations (id INTEGER PRIMARY KEY, name TEXT, longitude FLOAT, latitude FLOAT,altitude FLOAT);";
    static final String DB_TABLE_NAME = "locations";
    private static final String DB_TABLE_UPDATE_VER_2TO3 = "ALTER TABLE locations ADD COLUMN altitude FLOAT DEFAULT 3.14;";
    private final boolean addDebugLocations;

    public LocationDatabaseHelper(Context context) {
        super(context, DB_TABLE_NAME, (SQLiteDatabase.CursorFactory) null, 3);
        this.addDebugLocations = (context.getApplicationInfo().flags & 2) != 0;
    }

    public void onCreate(SQLiteDatabase db) {
        Log.d("SKEYE", "Creating db version:3");
        db.execSQL(DB_TABLE_CREATE);
        if (this.addDebugLocations) {
            Log.d("SKEYE", "Initialising db");
            float[] locations = {12.97647f, 77.53122f, 0.0f, 15.8438f, 74.4973f, 600.0f, -22.95f, -43.2f, 0.0f};
            int i = 0;
            for (String name : new String[]{"Bangalore", "Belgaum", "Rio de Janeiro"}) {
                db.execSQL("INSERT INTO locations (name, latitude, longitude, altitude) VALUES (?,?,?, ?);", new Object[]{name, Float.valueOf(locations[i * 3]), Float.valueOf(locations[(i * 3) + 1]), Float.valueOf(locations[(i * 3) + 2])});
                i++;
            }
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("SKEYE", "Updating db from version:" + oldVersion + " to " + newVersion);
        if (oldVersion == 2 && newVersion == 3) {
            db.execSQL(DB_TABLE_UPDATE_VER_2TO3);
            return;
        }
        db.execSQL("DROP TABLE IF EXISTS locations");
        onCreate(db);
    }
}
