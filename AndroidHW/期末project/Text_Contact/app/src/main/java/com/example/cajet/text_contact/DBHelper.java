package com.example.cajet.text_contact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "contact";
    private static final int VERSION = 1;
//    private static DBHelper instance = null;
    private SQLiteDatabase db;

//    public static DBHelper getInstance(Context context) { //注意static
//        if (instance == null) {
//            instance = new DBHelper(context);
//        }
//        return instance;
//    }

    private void openDatabase() {
        if (db == null) {
            db = this.getWritableDatabase();
        }
    }

    private void closeDatabase() {
        if (db != null) {
            db.close();
        }
    }
    DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE if not exists user "
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, "
                + "mobile TEXT, birthday TEXT, familyPhone TEXT, address TEXT, "
                + "otherContact TEXT, email TEXT, position TEXT, company TEXT, zipCode TEXT, "
                + "remark TEXT, imageId INT, imagePath TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists user");
        onCreate(db);
    }

    long insert(User user) {
        openDatabase();
        ContentValues values = new ContentValues();
        values.put("name", user.username);
        values.put("mobile", user.mobilePhone);
        values.put("birthday", user.birthday);
        values.put("familyPhone", user.familyPhone);
        values.put("address", user.address);
        values.put("otherContact", user.otherContact);
        values.put("email",user.email);
        values.put("position", user.position);
        values.put("company", user.company);
        values.put("zipCode", user.zipCode);
        values.put("remark", user.remark);
        values.put("imageId", user.imageId);
        values.put("imagePath", user.imagePath);
        long res = db.insert("user", null, values);
        closeDatabase();
        return  res;
    }

    void delete(User user) {
        openDatabase();
        db.delete("user", "_id= ?", new String[] {user._id+""});
        closeDatabase();
    }

    void update(User user) {
        openDatabase();
        ContentValues values = new ContentValues();
        values.put("name", user.username);
        values.put("mobile", user.mobilePhone);
        values.put("birthday", user.birthday);
        values.put("familyPhone", user.familyPhone);
        values.put("address", user.address);
        values.put("otherContact", user.otherContact);
        values.put("email",user.email);
        values.put("position", user.position);
        values.put("company", user.company);
        values.put("zipCode", user.zipCode);
        values.put("remark", user.remark);
        values.put("imageId", user.imageId);
        values.put("imagePath", user.imagePath);
        db.update("user", values, "_id=?", new String[] {user._id+""});
        closeDatabase();
    }

    ArrayList<Map<String, Object>> getUserList() {
        openDatabase();
        Cursor cursor = db.query("user", null, null, null, null, null, null);
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("_id", cursor.getString(cursor.getColumnIndex("_id")));
            map.put("name", cursor.getString(cursor.getColumnIndex("name")));
            map.put("mobile", cursor.getString(cursor.getColumnIndex("mobile")));
            map.put("birthday", cursor.getString(cursor.getColumnIndex("birthday")));
            map.put("familyPhone", cursor.getString(cursor.getColumnIndex("familyPhone")));
            map.put("address", cursor.getString(cursor.getColumnIndex("address")));
            map.put("otherContact", cursor.getString(cursor.getColumnIndex("otherContact")));
            map.put("email", cursor.getString(cursor.getColumnIndex("email")));
            map.put("position", cursor.getString(cursor.getColumnIndex("position")));
            map.put("company", cursor.getString(cursor.getColumnIndex("company")));
            map.put("zipCode", cursor.getString(cursor.getColumnIndex("zipCode")));
            map.put("remark", cursor.getString(cursor.getColumnIndex("remark")));
            map.put("imageId", cursor.getInt(cursor.getColumnIndex("imageId")));
            map.put("imagePath", cursor.getString(cursor.getColumnIndex("imagePath")));
            list.add(map);
        }
        cursor.close();
        closeDatabase();
        return list;
    }

    ArrayList<Map<String, Object>> getSearchUserList(String condition) {
        openDatabase();
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        String sql = "select * from " + "user" + " where 1=1 and (name like '%" + condition + "%' " +
                "or mobile like '%" + condition + "%' or familyPhone like '%" + condition + "%' " +
                "or zipCode like '%" + condition + "%' or birthday like '%"+ condition+ "%')";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            HashMap<String, Object> item = new HashMap<>();
            item.put("_id", cursor.getString(cursor.getColumnIndex("_id")));
            item.put("name", cursor.getString(cursor.getColumnIndex("name")));
            item.put("mobile", cursor.getString(cursor.getColumnIndex("mobile")));
            item.put("birthday", cursor.getString(cursor.getColumnIndex("birthday")));
            item.put("familyPhone", cursor.getString(cursor.getColumnIndex("familyPhone")));
            item.put("address", cursor.getString(cursor.getColumnIndex("address")));
            item.put("otherContact", cursor.getString(cursor.getColumnIndex("otherContact")));
            item.put("email", cursor.getString(cursor.getColumnIndex("email")));
            item.put("position", cursor.getString(cursor.getColumnIndex("position")));
            item.put("company", cursor.getString(cursor.getColumnIndex("company")));
            item.put("zipCode", cursor.getString(cursor.getColumnIndex("zipCode")));
            item.put("remark", cursor.getString(cursor.getColumnIndex("remark")));
            item.put("imageId", cursor.getInt(cursor.getColumnIndex("imageId")));
            item.put("imagePath", cursor.getString(cursor.getColumnIndex("imagePath")));
            list.add(item);
        }
        cursor.close();
        closeDatabase();
        return list;
    }
}
