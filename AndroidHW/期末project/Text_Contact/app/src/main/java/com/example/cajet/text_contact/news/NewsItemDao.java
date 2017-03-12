package com.example.cajet.text_contact.news;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;


class NewsItemDao {

    private static final int PER_PAGE_ITEM_COUNT = 6;
    private DBHelper mHelper;

    private static final String[] COLUMNS = {"title", "link", "imgLink", "content", "date"};

    NewsItemDao(Context context) {
        mHelper = new DBHelper(context);
    }


    /**
     * 更新表数据
     *
     * @param items items
     */
    void refreshData(int newsType, List<NewsItem> items) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            String sql = "delete from " + DBHelper.TABLE_CSDN + " where newsType=?";
            db.execSQL(sql, new Object[]{newsType});
            for (NewsItem item : items) {
                if (null != item) {
                    ContentValues values = new ContentValues();
                    values.put(COLUMNS[0], item.getTitle());
                    values.put(COLUMNS[1], item.getLink());
                    values.put(COLUMNS[2], item.getImgLink());
                    values.put(COLUMNS[3], item.getContent());
                    values.put(COLUMNS[4], item.getDate());
                    db.insert(DBHelper.TABLE_CSDN, null, values);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * 添加新闻列表
     *
     * @param items items
     */
    void addNewsItems(List<NewsItem> items) {
        if (null == items || items.isEmpty()) {
            return;
        }
        SQLiteDatabase db = mHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            for (NewsItem item : items) {
                if (null == item) {
                    continue;
                }
                ContentValues values = new ContentValues();
                values.put(COLUMNS[0], item.getTitle());
                values.put(COLUMNS[1], item.getLink());
                values.put(COLUMNS[2], item.getImgLink());
                values.put(COLUMNS[3], item.getContent());
                values.put(COLUMNS[4], item.getDate());
                db.insert(DBHelper.TABLE_CSDN, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    List<NewsItem> getNewsItems(int page, int newsType) {
        List<NewsItem> items = new ArrayList<>();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int offset = (page - 1) * PER_PAGE_ITEM_COUNT;
        String sql = "select title,link,imgLink,content,date,newsType "+" from " + DBHelper.TABLE_CSDN + " where newsType=? limit ?,?";
        try {
            db.beginTransaction();
            Cursor cursor = db.rawQuery(sql, new String[]{newsType + "", offset + "", PER_PAGE_ITEM_COUNT + ""});
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                NewsItem item = new NewsItem();
                item.setTitle(cursor.getString(0));
                item.setLink(cursor.getString(1));
                item.setImgLink(cursor.getString(2));
                item.setContent(cursor.getString(3));
                item.setDate(cursor.getString(4));
                items.add(item);
                cursor.moveToNext();
            }
            cursor.close();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return items;
    }
}
