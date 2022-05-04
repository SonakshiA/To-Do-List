package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.lights.LightsManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todolist.data.Contract;

public class EditorActivity extends AppCompatActivity {

    private Spinner prioritySpinner;
    private int taskPriority = -1;
    private TextView taskName;

    /** Content URI for the existing task (null if it's a new task) */
    private Uri CurrentTaskUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        CurrentTaskUri = intent.getData();

        if (CurrentTaskUri==null){
            setTitle("Add Task");
        } else{
            setTitle("Edit Task");
        }

        taskName = (TextView) findViewById(R.id.task_name);
        prioritySpinner = (Spinner) findViewById(R.id.spinner);

        setupSpinner();
    }

    private void setupSpinner(){
        ArrayAdapter prioritySpinnerAdapter = ArrayAdapter.createFromResource(this,R.array.priority_options, android.R.layout.simple_spinner_item);
        prioritySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        prioritySpinner.setAdapter(prioritySpinnerAdapter);

        prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selection = (String) adapterView.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)){
                    if (selection.equals("Low")){
                        taskPriority = Contract.ListEntry.PRIORITY_LOW;
                    } else if (selection.equals("Medium")){
                        taskPriority = Contract.ListEntry.PRIORITY_MEDIUM;
                    } else {
                        taskPriority = Contract.ListEntry.PRIORITY_HIGH;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){
                taskPriority = -1;
            }
        });
    }

    private void showDeleteConfirmation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete task?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteTask();
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveTask(){
        EditText task = (EditText) findViewById(R.id.task_name);
        String taskName = task.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(Contract.ListEntry.TASK,taskName);
        values.put(Contract.ListEntry.PRIORITY,taskPriority);

        if (CurrentTaskUri==null){
            Uri newUri = getContentResolver().insert(Contract.ListEntry.CONTENT_URI,values);
            if (newUri==null){
                Toast.makeText(this,"Error saving task", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this,"Task saved", Toast.LENGTH_SHORT).show();
            }
        }else{
            int rowsAffected = getContentResolver().update(CurrentTaskUri,values,null,null);
            if (rowsAffected==0){
                Toast.makeText(this,"Error while updating task", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this,"Task updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteTask(){
        if (CurrentTaskUri!=null){
            int rowsDeleted = getContentResolver().delete(CurrentTaskUri,null,null);
            if (rowsDeleted==0){
                Toast.makeText(this,"Error while deleting", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this,"Task deleted", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.save:
                saveTask();
                finish();
                return true;
            case R.id.delete:
                showDeleteConfirmation();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu); //why?
        if (CurrentTaskUri==null){
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }
}