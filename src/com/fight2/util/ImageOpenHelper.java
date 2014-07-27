package com.fight2.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ImageOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Fight2";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME = "image";
    public static final String KEY = "web";
    public static final String VALUE = "local";
    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + KEY + " TEXT, " + VALUE + " TEXT);";

    public ImageOpenHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        Log.w(ImageOpenHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
