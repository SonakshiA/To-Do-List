package com.example.todolist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ListProvider extends ContentProvider {
    // declaring IDs for URI matching
    private static final int LISTS = 100;
    private static final int LIST_ID = 101;

    // The code for the matched node (added using addURI), or -1 if there is no matched node.
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // uri matchers
    static{
        uriMatcher.addURI(Contract.CONTENT_AUTHORITY,Contract.PATH,LISTS);
        uriMatcher.addURI(Contract.CONTENT_AUTHORITY,Contract.PATH + "/#",LIST_ID);
    }

    private DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor;
        int match = uriMatcher.match(uri);
        switch(match){
            case LISTS:
                cursor = database.query(Contract.ListEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case LIST_ID:
                selection = Contract.ListEntry.ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))}; // returns id present at the end of the uri request
                cursor = database.query(Contract.ListEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("cannot query unknown uri " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);  // If you call Cursor.setNotificationUri(), Cursor will know what ContentProvider Uri it was created for; notifies
        // watch a content uri for changes
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch (match){
            case LISTS:
                return insertTask(uri,contentValues);
            default:
                throw new IllegalArgumentException();
        }
    }

    private Uri insertTask(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        long id = database.insert(Contract.ListEntry.TABLE_NAME,null,contentValues); // returns id of insertion

        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = uriMatcher.match(uri);
        switch (match){
            case LISTS:
                rowsDeleted =  database.delete(Contract.ListEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case LIST_ID:
                selection = Contract.ListEntry.ID + "=?";
                selectionArgs = selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted =  database.delete(Contract.ListEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException();
        }
        if (rowsDeleted!=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsUpdated;
        final int match = uriMatcher.match(uri);
        switch (match){
            case LIST_ID:
                selection = Contract.ListEntry.ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = database.update(Contract.ListEntry.TABLE_NAME,contentValues,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException();
        }
        if (rowsUpdated!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }
}
