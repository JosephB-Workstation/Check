package com.check.app.List_Stuff;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.check.app.Create_List_Dialog;
import com.check.app.R;
import com.check.app.Task_Stuff.Create_Task_Dialog;
import com.check.app.Task_Stuff.Edit_Task_Dialog;
import com.check.app.Task_Stuff.TaskAdapter;
import com.check.app.Task_Stuff.TaskObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class List_Activity extends AppCompatActivity implements Create_Task_Dialog.CreateTaskListener, Edit_Task_Dialog.editTaskListener, List_Color_Settings.ColorSettingsListListener {
    private RecyclerView lTasks; //First three variables are necessary for recycler view
    private RecyclerView.Adapter lTaskAdapter;
    private RecyclerView.LayoutManager lTaskLayoutManager;
    private String listName; // A string to store the list's name
    private ArrayList<TaskObject> taskList; // An arraylist to store tasks
    private static Context context; // a context because I think I need it for editing tasks
    private LinearLayout background; // a linear layout variable so I can change list backgrounds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view); //activity view set

        Intent intent = getIntent(); // prepares an intent for later use
        Toolbar toolbar = findViewById(R.id.listToolBar); // list toolbar grabbed
        listName = intent.getStringExtra("listName"); //grabs the name from MainActivity, which got it from the dialog
        toolbar.setTitle(listName);// sets the local string variable to be the title of the toolbar.
        setSupportActionBar(toolbar);
        taskListStarter(); // function to start the recycler view
    }

    public boolean onCreateOptionsMenu(Menu menu) { // makes toolbar options
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.listactivity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // list toolbar options list
        switch (item.getItemId()) {
            case R.id.addTask:
                Create_Task_Dialog taskdialog = new Create_Task_Dialog(); // creates a dialog for adding a new task (Create_Task_Dialog)
                taskdialog.show(getSupportFragmentManager(), "Create Task");
                final Map newMessageMap = new HashMap<>();

                return true;
            case R.id.listSettings:
                List_Color_Settings colordialog = new List_Color_Settings(); // creates a dialog for changing the color (List_Color_Settings)
                colordialog.show(getSupportFragmentManager(), "Change Color");
                return true;
            case R.id.listClear:
                taskList.clear(); //clears the arraylist
                lTaskAdapter.notifyDataSetChanged();
            default: //uhoh
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void attachTaskSettings(String _taskName, String _taskDescription) { //listener to bring in created tasks from a successful dialog
        TaskObject newTask = new TaskObject(_taskName, _taskDescription); //New task constructor (TaskObject)
        taskList.add(newTask);//add to arraylist
        lTaskAdapter.notifyDataSetChanged();//update recycler
    }

    public void attachTaskSettings(String _taskName, String _taskDescription, Calendar date){
        TaskObject newTask = new TaskObject(_taskName, _taskDescription, date);
        taskList.add(newTask);//add to arraylist
        lTaskAdapter.notifyDataSetChanged();//update recycler
    }

    private void taskListStarter(){
        taskList = new ArrayList<>();//init arraylist
        lTasks = findViewById(R.id.taskList); //recycler view pairing
        lTasks.setNestedScrollingEnabled(false); // removes scary scroll wheel
        lTasks.setHasFixedSize(false); // allows for the better scroll wheel to work
        lTaskLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false); // layout manager that handles which way the recycler view appends items to a list
        lTasks.setLayoutManager(lTaskLayoutManager);// configures layout manager for list page
        lTaskAdapter = new TaskAdapter(context, taskList, this.getSupportFragmentManager()); // constructor for class: TaskAdapter, which handles checkboxes and edits.
        lTasks.setAdapter(lTaskAdapter);//configures adapter to work here.

    }

    @Override
    public void attachUpdatedTaskSettings(String _taskName, String _taskDescription, int _position) { // Listener that updates a task at a specific position in the arraylist with new information, such as name and description. (Dialog created in TaskAdapter, dialog is Edit_Task_Dialog)
        taskList.get(_position).setTaskName(_taskName);
        taskList.get(_position).setTaskDescription(_taskDescription);
        if(taskList.get(_position).getTimerState()){
            taskList.get(_position).updateTimer();
        }
        lTaskAdapter.notifyDataSetChanged();
    }

    public void attachUpdatedTaskSettings(String _taskName, String _taskDescription, int _position, Calendar _dueDate){
        taskList.get(_position).setTaskName(_taskName);
        taskList.get(_position).setTaskDescription(_taskDescription);
        taskList.get(_position).updateTimer(_dueDate);
        lTaskAdapter.notifyDataSetChanged();
    }

    @Override
    public void attachColorSettings(int colorId) { // listener for the list color swap (List_Color_Settings)
        background = findViewById(R.id.listLayout);
        int backgroundColorId = colorId;
        if(colorId == 0) backgroundColorId = R.drawable.background_yellow;
        else if(colorId == 1) backgroundColorId = R.drawable.background_blue;
        else if(colorId == 2) backgroundColorId = R.drawable.background_green;
        else if(colorId == 3) backgroundColorId = R.drawable.background_purple;
        background.setBackgroundResource(backgroundColorId);

    }
}

