package com.example.cajet.text_contact.note;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class MyDataBaseAdapter {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "myplandatabase.db";
    private static final String DB_TABLE = "myplan";
    private static final String KEY_ID = "_id";
    static final String KEY_THEME = "theme";
    static final String KEY_TEXT = "text";
    static final String KEY_TIME = "time";
    static final String KEY_DEADLINE = "deadline";
    static final String KEY_STATE = "state";
    private Context mContext = null;
    private SQLiteDatabase mSQLiteDatabase = null;
//    private MyDatabaseHelper mDatabaseHelper = null;

    private static final String DB_CREATE = "CREATE TABLE "+DB_TABLE+" ("+
            KEY_ID+" INTEGER PRIMARY KEY, "+
            KEY_THEME+" TEXT, "+
            KEY_TEXT+" TEXT, "+
            KEY_TIME+" TEXT, "+
            KEY_STATE+" TEXT, "+
            KEY_DEADLINE+" TEXT ) ";

    private static class MyDatabaseHelper extends SQLiteOpenHelper
    {
        MyDatabaseHelper(Context context)
        {
            super(context, DB_NAME, null, DB_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    MyDataBaseAdapter(Context context)
    {
        mContext = context;
    }

    public void open() throws SQLException
    {
        MyDatabaseHelper mDatabaseHelper = new MyDatabaseHelper(mContext);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
    }


    long insertData(String theme, String text, String time, String deadline, String state)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_THEME, theme);
        initialValues.put(KEY_TEXT, text);
        initialValues.put(KEY_TIME, time);
        initialValues.put(KEY_DEADLINE, deadline);
        initialValues.put(KEY_STATE, state);
        return mSQLiteDatabase.insert(DB_TABLE, KEY_ID, initialValues);
    }

    public boolean deleteData(long rowId)
    {
        return mSQLiteDatabase.delete(DB_TABLE, KEY_ID + "=" + rowId, null) > 0;
    }

    boolean deleteData(String date)
    {
        return mSQLiteDatabase.delete(DB_TABLE, KEY_TIME + " = ?", new String [] {date}) > 0;
    }

    Cursor fetchAllData()
    {
        return mSQLiteDatabase.query(DB_TABLE,
                new String[] {KEY_ID, KEY_THEME, KEY_TEXT, KEY_TIME, KEY_DEADLINE, KEY_STATE},
                null, null, null, null, null);
    }
    //根据id获取
    public Cursor fetchData(long rowId) throws SQLException
    {
        Cursor mCursor =
                mSQLiteDatabase.query(true, DB_TABLE,
                        new String[] {KEY_ID, KEY_THEME, KEY_TEXT, KEY_TIME, KEY_DEADLINE, KEY_STATE},
                        KEY_ID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null)
        {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    //根据date获取state
    Cursor fetchDataForState(String date) {
        Cursor mCursor = mSQLiteDatabase.query(true, DB_TABLE, new String [] {KEY_STATE},
                KEY_TIME + " = ?", new String [] {date}, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    //根据主题/内容/状态进行搜索
    Cursor fetchData(String themeOrTextOrState) {
        Cursor mCursor =
                mSQLiteDatabase.query(true, DB_TABLE,
                        new String[] {KEY_ID, KEY_THEME, KEY_TEXT, KEY_TIME, KEY_DEADLINE, KEY_STATE},
                        KEY_THEME + " = ?" + " or " + KEY_TEXT + " = ?" + " or " + KEY_STATE + " = ?",
                        new String [] {themeOrTextOrState, themeOrTextOrState, themeOrTextOrState},
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    boolean updateData(String oldTime, String theme, String text, String time, String deadline, String state)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_THEME, theme);
        args.put(KEY_TEXT, text);
        //args.put(KEY_TIME, time);
        args.put(KEY_DEADLINE, deadline);
        args.put(KEY_STATE, state);
        return mSQLiteDatabase.update(DB_TABLE, args, KEY_TIME + " = ?", new String [] {oldTime}) > 0;
    }
    boolean updateData(String oldTime, String state) {
        ContentValues args = new ContentValues();
        args.put(KEY_STATE, state);
        return mSQLiteDatabase.update(DB_TABLE, args, KEY_TIME + " = ?", new String [] {oldTime}) > 0;
    }
}
