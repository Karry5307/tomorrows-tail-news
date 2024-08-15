package com.java.tomorrowstailnews.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.java.tomorrowstailnews.entity.HistoryInfo;

import java.util.ArrayList;
import java.util.List;

public class HistoryDbHelper extends SQLiteOpenHelper {
    private static HistoryDbHelper helper;
    private static final String DB_NAME = "history.db";
    private static final int VERSION = 1;

    public HistoryDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public synchronized static HistoryDbHelper getInstance(Context context) {
        if (helper == null) {
            helper = new HistoryDbHelper(context, DB_NAME, null, VERSION);
        }
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table history_table(history_id integer primary key autoincrement, " +
                "news_id text, news_abstract text, news_json text, is_starred integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean queryIsAdded(String newsId) {
        SQLiteDatabase db = getReadableDatabase();
        String sqlCommand = "select history_id, news_id, news_json, is_starred from history_table where news_id = ?";
        Cursor cursor = db.rawQuery(sqlCommand, new String[]{newsId});
        boolean returnValue = cursor.moveToNext();
        cursor.close();
        db.close();
        return returnValue;
    }

    public int addHistory(String newsId, String newsAbstract, String newsJson, int isStarred) {
        if (queryIsAdded(newsId)) {
            return 0;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("news_id", newsId);
        values.put("news_abstract", newsAbstract);
        values.put("news_json", newsJson);
        values.put("is_starred", isStarred);
        String nullColumnHack = "values(null, ?, ?, ?, ?)";
        int insert = (int) db.insert("history_table", nullColumnHack, values);
        db.close();
        return insert;
    }

    @SuppressLint("Range")
    public List<HistoryInfo> queryHistoryInfoList() {
        SQLiteDatabase db = getReadableDatabase();
        List<HistoryInfo> historyInfoList = new ArrayList<>();
        String sqlCommand = "select history_id, news_id, news_json, is_starred from history_table";
        Cursor cursor = db.rawQuery(sqlCommand, null);
        while (cursor.moveToNext()) {
            int historyId = cursor.getInt(cursor.getColumnIndex("history_id"));
            String newsId = cursor.getString(cursor.getColumnIndex("news_id"));
            String newsJSON = cursor.getString(cursor.getColumnIndex("news_json"));
            int isStarred = cursor.getInt(cursor.getColumnIndex("is_starred"));
            historyInfoList.add(new HistoryInfo(historyId, isStarred, newsId, newsJSON));
        }
        cursor.close();
        db.close();
        return historyInfoList;
    }

    @SuppressLint("Range")
    public int queryIsStarred(String newsId) {
        SQLiteDatabase db = getReadableDatabase();
        String sqlCommand = "select history_id, news_id, news_json, is_starred from history_table where news_id = ?";
        Cursor cursor = db.rawQuery(sqlCommand, new String[]{newsId});
        int isStarred = -1;
        if (cursor.moveToNext()) {
            isStarred = cursor.getInt(cursor.getColumnIndex("is_starred"));
        }
        cursor.close();
        db.close();
        return isStarred;
    }

    public int modifyIsStarred(String newsId, int isStarred) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_starred", isStarred);
        int update = (int) db.update("history_table", values, "news_id = ?", new String[]{newsId});
        db.close();
        return update;
    }

    @SuppressLint("Range")
    public List<HistoryInfo> queryStarredHistoryInfoList() {
        SQLiteDatabase db = getReadableDatabase();
        List<HistoryInfo> historyInfoList = new ArrayList<>();
        String sqlCommand = "select history_id, news_id, news_json, is_starred from history_table where is_starred = 1";
        Cursor cursor = db.rawQuery(sqlCommand, null);
        while (cursor.moveToNext()) {
            int historyId = cursor.getInt(cursor.getColumnIndex("history_id"));
            String newsId = cursor.getString(cursor.getColumnIndex("news_id"));
            String newsJSON = cursor.getString(cursor.getColumnIndex("news_json"));
            int isStarred = cursor.getInt(cursor.getColumnIndex("is_starred"));
            historyInfoList.add(new HistoryInfo(historyId, isStarred, newsId, newsJSON));
        }
        cursor.close();
        db.close();
        return historyInfoList;
    }

    @SuppressLint("Range")
    public String queryAbstract(String newsId) {
        SQLiteDatabase db = getReadableDatabase();
        String sqlCommand = "select news_abstract from history_table where news_id = ?";
        Cursor cursor = db.rawQuery(sqlCommand, new String[]{newsId});
        String newsAbstract = "";
        if (cursor.moveToNext()) {
            newsAbstract = cursor.getString(cursor.getColumnIndex("news_abstract"));
        }
        cursor.close();
        db.close();
        return newsAbstract;
    }

    public int modifyAbstract(String newsId, String newsAbstract) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("news_abstract", newsAbstract);
        int update = (int) db.update("history_table", values, "news_id = ?", new String[]{newsId});
        db.close();
        return update;
    }
}