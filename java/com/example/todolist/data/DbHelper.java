package com.example.todolist.data;

import android.content.Context;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "tasks.db";
    public static final String DELETE_SQL_ENTRIES = "";

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + Contract.ListEntry.TABLE_NAME + " ( " +
                Contract.ListEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Contract.ListEntry.TASK + " TEXT, " +
                Contract.ListEntry.PRIORITY + " INTEGER NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DELETE_SQL_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
