package com.example.todolist;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.todolist.data.Contract;

public class ListCursorAdapter extends CursorAdapter {
    public ListCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView taskName = view.findViewById(R.id.name);
        TextView taskPriority = view.findViewById(R.id.priority);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(Contract.ListEntry.TASK));
        int priority = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.ListEntry.PRIORITY));

        taskName.setText(name);

        if (priority==0){
            taskPriority.setText("Low");
        } else if (priority==1){
            taskPriority.setText("Medium");
        } else{
            taskPriority.setText("High");
        }
    }
}
