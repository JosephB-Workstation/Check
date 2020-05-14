package com.check.app.List_Stuff;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.check.app.Create_List_Dialog;
import com.check.app.R;
import com.check.app.Task_Stuff.Create_Task_Dialog;
import com.check.app.Task_Stuff.TaskAdapter;
import com.check.app.Task_Stuff.TaskObject;

import java.util.ArrayList;

public class List_Activity extends AppCompatActivity implements Create_Task_Dialog.CreateTaskListener {
    private RecyclerView lTasks;
    private RecyclerView.Adapter lTaskAdapter;
    private RecyclerView.LayoutManager lTaskLayoutManager;
    private String listName;
    private ArrayList<TaskObject> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        Intent intent = getIntent();
        Toolbar toolbar = findViewById(R.id.listToolBar);
        listName = intent.getStringExtra("listName"); //grabs the name from MainActivity, which got it from the dialog
        toolbar.setTitle(listName);// sets the local string variable to be the title of the toolbar.
        setSupportActionBar(toolbar);
        taskListStarter();
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
                Create_Task_Dialog taskdialog = new Create_Task_Dialog();
                taskdialog.show(getSupportFragmentManager(), "Create Task");
                return true;
            case R.id.listSettings:
                //code here for dialog to edit a task
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void attachTaskSettings(String _taskName) {
        Log.d("Task created", _taskName);
        TaskObject newTask = new TaskObject(_taskName);
        taskList.add(newTask);
        lTaskAdapter.notifyDataSetChanged();
    }

    private void taskListStarter(){
        taskList = new ArrayList<>();
        lTasks = findViewById(R.id.taskList);
        lTasks.setNestedScrollingEnabled(false);
        lTasks.setHasFixedSize(false);
        lTaskLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        lTasks.setLayoutManager(lTaskLayoutManager);
        lTaskAdapter = new TaskAdapter(taskList);
        lTasks.setAdapter(lTaskAdapter);

    }
}
