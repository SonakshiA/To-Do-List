package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.todolist.data.Contract;
import com.example.todolist.data.DbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private DbHelper dbHelper;
    ListCursorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this,EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView view = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        view.setEmptyView(emptyView);

        //displayDatabaseInfo();

        adapter = new ListCursorAdapter(this,null);
        view.setAdapter(adapter);

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent intent = new Intent(CatalogActivity.this,EditorActivity.class);
                Uri CurrentTaskUri = ContentUris.withAppendedId(Contract.ListEntry.CONTENT_URI,id);
                intent.setData(CurrentTaskUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(0,null,this); //what is loaderCallbacks?
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void showDeleteConfirmation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete all tasks?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteAllEntries();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    // testing:
    public void displayDatabaseInfo() {
        dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        Cursor cursor = db.rawQuery("Select * from " + Contract.ListEntry.TABLE_NAME, null);
        try {
            TextView textView = (TextView) findViewById(R.id.text_view);
            textView.setText("Number of tasks: " + cursor.getCount());
        }finally {
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_catalog,menu);
        return true;
    }

    private void deleteAllEntries(){
        getContentResolver().delete(Contract.ListEntry.CONTENT_URI,null,null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.delete_all_entries:
                showDeleteConfirmation();
                return true;
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {Contract.ListEntry.ID, Contract.ListEntry.TASK, Contract.ListEntry.PRIORITY};

        return new CursorLoader(this,Contract.ListEntry.CONTENT_URI,projection,null,null,"priority desc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}