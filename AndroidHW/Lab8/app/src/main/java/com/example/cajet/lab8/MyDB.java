package com.example.cajet.lab8;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cajet on 2016/11/15.
 */

public class MyDB extends SQLiteOpenHelper {

    private static final String DB_NAME = "Bithday.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "Birth_Table";

    public MyDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String temp= "CREATE TABLE if not exists "
                + TABLE_NAME
                +" (_id INTEGER PRIMARY KEY,name TEXT,birth TEXT,gift TEXT)";
        db.execSQL(temp);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String name, String birthday, String gift) {
        SQLiteDatabase db= getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("birth", birthday);
        values.put("gift", gift);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void delete(String name) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, "name=?", new String[]{name+""} );
        db.close();
    }

    public void update(String name, String birthday, String gift) {
        SQLiteDatabase db= getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("birth", birthday);
        values.put("gift", gift);
        db.update(TABLE_NAME, values, "name=?", new String[]{name+""});
    }

    public Cursor getAll() {
        SQLiteDatabase db= getWritableDatabase();
        Cursor cursor= db.query(TABLE_NAME, new String[]{"_id", "name", "birth", "gift"}, null, null, null, null, null);
        return cursor;
    }

    public boolean query(String name) {
        SQLiteDatabase db= getWritableDatabase();
        Cursor cursor= getAll();
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex("name")).equals(name)) {
                return true;
            }
        }
        return false;
    }

}
