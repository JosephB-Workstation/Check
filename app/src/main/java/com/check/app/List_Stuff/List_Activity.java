package com.check.app.List_Stuff;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.check.app.Create_List_Dialog;
import com.check.app.R;

public class List_Activity extends AppCompatActivity {

    private String listName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        Intent intent = getIntent();
        Toolbar toolbar = findViewById(R.id.listToolBar);
        listName = intent.getStringExtra("listName"); //grabs the name from MainActivity, which got it from the dialog
        toolbar.setTitle(listName);// sets the local string variable to be the title of the toolbar.
        setSupportActionBar(toolbar);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.listactivity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addTask:
                //code here to add tasks
                return true;
            case R.id.listSettings:
                //code here for dialog to edit a task
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

