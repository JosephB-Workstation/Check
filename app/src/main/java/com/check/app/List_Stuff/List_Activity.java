package com.check.app.List_Stuff;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.check.app.R;
import com.check.app.Task_Stuff.Create_Task_Dialog;
import com.check.app.Task_Stuff.Edit_Task_Dialog;
import com.check.app.Task_Stuff.TaskAdapter;
import com.check.app.Task_Stuff.TaskObject;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class List_Activity extends AppCompatActivity implements Create_Task_Dialog.CreateTaskListener, Edit_Task_Dialog.editTaskListener, List_Edit_Settings.ListEditListener, List_Color_Settings.ColorSettingsListListener {
    private RecyclerView lTasks; //First three variables are necessary for recycler view
    private RecyclerView.Adapter lTaskAdapter;
    private RecyclerView.LayoutManager lTaskLayoutManager;
    private String listName, listCategory; // A string to store the list's name
    private ArrayList<TaskObject> taskList; // An arraylist to store tasks
    private LinearLayout background; // a linear layout variable so I can change list backgrounds
    private int storagePointer;
    private double backgroundColorId;
    private BroadcastReceiver minuteUpdateReciever;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    SharedPreferences pref2;
    SharedPreferences.Editor editor2;
    HashMap<String, TaskObject> listMap;
    HashMap<String, Object> listInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view); //activity view set
        Intent intent = getIntent(); // prepares an intent for later use
        pref = getApplicationContext().getSharedPreferences("Check", 0);
        editor = pref.edit();
        pref2 = getApplicationContext().getSharedPreferences("Check-d", 0);
        editor2 = pref2.edit();
        listInfo = new HashMap<String, Object>();
        listMap = new HashMap<String, TaskObject>();
        String savedMap;
        taskListStarter();
        if(intent.getIntExtra("mode", 1) == 0) {

            listName = intent.getStringExtra("listName");
            Gson gson = new Gson();
            gson.newBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
            savedMap = pref2.getString(listName, "");
            java.lang.reflect.Type type = new TypeToken<HashMap<String, TaskObject>>(){}.getType();
            listMap = gson.fromJson(savedMap, type);
            for(int i = 0; i < (listMap.size()); i++){
                taskList.add(listMap.get(Integer.toString(i)));
                taskList.get(i).importTimeCheck();
            }
            Toolbar toolbar = findViewById(R.id.listToolBar); // list toolbar grabbed
            toolbar.setTitle(listName);// sets the local string variable to be the title of the toolbar.
            setSupportActionBar(toolbar);
            backgroundColorId = intent.getDoubleExtra("listBackground", 0.0);
            attachColorSettings(backgroundColorId);
            listCategory = intent.getStringExtra("listCategory");
            listInfo.put("category", listCategory);
            listInfo.put("background", backgroundColorId);
            listInfo.put("name", listName);
            storagePointer = taskList.size();
            lTaskAdapter.notifyDataSetChanged();
        }

        else if(intent.getIntExtra("mode", 1) == 1){ // if the list was created, instead of loaded.
        Toolbar toolbar = findViewById(R.id.listToolBar); // list toolbar grabbed
        listName = intent.getStringExtra("listName"); //grabs the name from MainActivity, which got it from the dialog
        toolbar.setTitle(listName);// sets the local string variable to be the title of the toolbar.
        listInfo.put("name", listName);
        listInfo.put("category", listCategory);
        setSupportActionBar(toolbar);
        backgroundColorId = 0;
        attachColorSettings(backgroundColorId);
        taskListStarter(); // function to start the recycler view
        storagePointer = taskList.size();}
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
                return true;
            case R.id.listSettings:
                List_Edit_Settings editDialog = new List_Edit_Settings();
                Bundle bundle = new Bundle();
                bundle.putString("category", listCategory);
                bundle.putDouble("colorId", backgroundColorId);
                editDialog.setArguments(bundle);
                editDialog.show(getSupportFragmentManager(), "Edit List");
                return true;
            case R.id.listClear:
                taskList.clear(); //clears the arraylist
                lTaskAdapter.notifyDataSetChanged();
                listMap.clear();
            default: //uhoh
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void attachTaskSettings(String _taskName, String _taskDescription) { //listener to bring in created tasks from a successful dialog
        TaskObject newTask = new TaskObject(_taskName, _taskDescription); //New task constructor (TaskObject)
        taskList.add(newTask);//add to arraylist
        lTaskAdapter.notifyDataSetChanged();//update recycler
        storagePointer = taskList.size();
        listMap.put(Integer.toString((storagePointer -1)), taskList.get((storagePointer -1)));
    }

    public void attachTaskSettings(String _taskName, String _taskDescription, Calendar date){
        TaskObject newTask = new TaskObject(_taskName, _taskDescription, date);
        taskList.add(newTask);//add to arraylist
        lTaskAdapter.notifyDataSetChanged();//update recycler
        storagePointer = taskList.size();
        listMap.put(Integer.toString((storagePointer -1)), taskList.get((storagePointer -1)));
    }

    private void taskListStarter(){
        taskList = new ArrayList<>();//init arraylist
        lTasks = findViewById(R.id.taskList); //recycler view pairing
        lTasks.setNestedScrollingEnabled(false); // removes scary scroll wheel
        lTasks.setHasFixedSize(false); // allows for the better scroll wheel to work
        lTaskLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false); // layout manager that handles which way the recycler view appends items to a list
        lTasks.setLayoutManager(lTaskLayoutManager);// configures layout manager for list page
        lTaskAdapter = new TaskAdapter(taskList, this.getSupportFragmentManager()); // constructor for class: TaskAdapter, which handles checkboxes and edits.
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
        listMap.remove(Integer.toString(_position));
        listMap.put(Integer.toString(_position), taskList.get(_position));
    }

    public void attachUpdatedTaskSettings(String _taskName, String _taskDescription, int _position, Calendar _dueDate){
        taskList.get(_position).setTaskName(_taskName);
        taskList.get(_position).setTaskDescription(_taskDescription);
        taskList.get(_position).updateTimer(_dueDate);
        lTaskAdapter.notifyDataSetChanged();
        listMap.remove(Integer.toString(_position));
        listMap.put(Integer.toString(_position), taskList.get(_position));
    }

    @Override
    public void attachColorSettings(int colorId) { // listener for the list color swap (List_Color_Settings)
        backgroundColorId = colorId;
        if(colorId == 0) setBackground(R.drawable.background_yellow);
        else if(colorId == 1) setBackground(R.drawable.background_blue);
        else if(colorId == 2) setBackground(R.drawable.background_green);
        else if(colorId == 3) setBackground(R.drawable.background_purple);
        if(listInfo.containsKey("background")){listInfo.remove("background");}
        listInfo.put("background", backgroundColorId);
    }
    public void attachColorSettings(double colorId){
        backgroundColorId = colorId;
        if(colorId == 0) setBackground(R.drawable.background_yellow);
        else if(colorId == 1) setBackground(R.drawable.background_blue);
        else if(colorId == 2) setBackground(R.drawable.background_green);
        else if(colorId == 3) setBackground(R.drawable.background_purple);
        if(listInfo.containsKey("background")){listInfo.remove("background");}
        listInfo.put("background", backgroundColorId);
    }
    private void setBackground(int backgroundColor){
        background = findViewById(R.id.listLayout);
        background.setBackgroundResource(backgroundColor);
    }

    public void startAutoUpdater(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        minuteUpdateReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                for(int i = 0; i < taskList.size(); i++){
                    if(taskList.get(i).hasTimer()){lTaskAdapter.notifyItemChanged(i);}
                }
            }
        };
        registerReceiver(minuteUpdateReciever, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(minuteUpdateReciever);

        Gson gson = new Gson();
        gson.newBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        String taskMapString = gson.toJson(listMap);

        if(pref2.contains(listName)){editor2.remove(listName);}
        editor2.putString(listName, taskMapString);
        editor2.commit();

        String listSettings = gson.toJson(listInfo);
        if(pref.contains(listName)){editor.remove(listName);}
        editor.putString(listName, listSettings);
        editor.commit();

    }

    protected void onResume() {
        super.onResume();
        startAutoUpdater();
    }

    @Override
    public void attachListSettings(String category) {
        listCategory = category;
        if(listInfo.containsKey("category")){listInfo.remove("category");}
        listInfo.put("category", category);
    }
}

