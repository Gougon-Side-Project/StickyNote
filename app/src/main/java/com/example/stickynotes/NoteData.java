package com.example.stickynotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteData extends SQLiteOpenHelper {

    private final static String DB = "NOTE_DB.db";      // database
    private final static String TB = "NOTE_TB";     // table
    private final static int VS = 2;    // version

    private static SQLiteDatabase database;

    public NoteData(Context context){
        super(context, DB, null, VS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL = "CREATE TABLE IF  NOT EXISTS " + TB + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, _title VARCHAR(20), _content VARCHAR(2000))";
        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL = "DROP TABLE " + TB;
        db.execSQL(SQL);
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new NoteData(context).getWritableDatabase();
        }

        return database;
    }

    public static void insert(String title, String content){
        ContentValues values = new ContentValues();
        values.put("_title", title);
        values.put("_content", content);
        database.insert("NOTE_TB", null, values);
        sort();
    }

    public static void delete(String id){
        database.execSQL("DELETE FROM NOTE_TB WHERE _id = '" + id + "';");
        sort();
    }

    public static void update(String id, String title, String content){
        database.execSQL("UPDATE " + TB + " SET _title = '" + title + "' WHERE _id = '" + id + "'");
        database.execSQL("UPDATE " + TB + " SET _content = '" + content + "' WHERE _id = '" + id + "'");
        sort();
    }

    public static void deleteAll(){
        database.delete(TB, null, null);
    }

    public static String searchTitle(int id){
        Cursor c = database.rawQuery("SELECT _title FROM NOTE_TB WHERE _id = '" + String.valueOf(id) + "'", null);
        c.moveToNext();
        return c.getString(c.getColumnIndex("_title"));
    }

    public static String searchContent(int id){
        Cursor c = database.rawQuery("SELECT * FROM NOTE_TB WHERE _id = '" + String.valueOf(id) + "'", null);
        c.moveToNext();
        return c.getString(c.getColumnIndex("_content"));
    }

    private static void sort(){
        int order = 0;
        Cursor cursor = database.rawQuery("SELECT * FROM NOTE_TB", null);
        while (cursor.moveToNext()) {
            String previousId = cursor.getString(cursor.getColumnIndex("_id"));
            database.execSQL("UPDATE " + TB + " SET _id = '" + String.valueOf(order) + "' WHERE _id = '" + previousId + "'");
            order++;
        }
    }
}