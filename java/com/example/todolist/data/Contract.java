package com.example.todolist.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {
    public static final String CONTENT_AUTHORITY = "com.example.android.todolist";
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH = "toDo"; // same as TABLE_NAME

    public static final class ListEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI,PATH); /* content://com.example.android.todolist/toDo */
        public static final String TABLE_NAME = "toDo";
        public static final String ID = BaseColumns._ID;
        public static final String TASK = "task";
        public static final String PRIORITY = "priority";

        public static final int PRIORITY_LOW = 0;
        public static final int PRIORITY_MEDIUM = 1;
        public static final int PRIORITY_HIGH = 2;
    }
}
