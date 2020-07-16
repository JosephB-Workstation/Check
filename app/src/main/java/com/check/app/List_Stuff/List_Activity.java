package com.check.app.List_Stuff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.check.app.MainActivity;
import com.check.app.R;
import com.check.app.ReminderBroadcast;
import com.check.app.Task_Stuff.Create_Task_Dialog;
import com.check.app.Task_Stuff.Edit_Task_Dialog;
import com.check.app.Task_Stuff.TaskAdapter;
import com.check.app.Task_Stuff.TaskObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class List_Activity extends AppCompatActivity implements Create_Task_Dialog.CreateTaskListener, Edit_Task_Dialog.editTaskListener, List_Edit_Settings.ListEditListener, List_Color_Settings.ColorSettingsListListener, List_Delete_Dialog.ListDeleteListener {
    private RecyclerView lTasks; //First three variables are necessary for recycler view
    private RecyclerView.Adapter lTaskAdapter;
    private RecyclerView.LayoutManager lTaskLayoutManager;
    private String listName, listCategory, listID, mediaURI; // A string to store the list's name
    private ArrayList<TaskObject> taskList; // An arraylist to store tasks
    private LinearLayout background; // a linear layout variable so I can change list backgrounds
    private int storagePointer;
    private double backgroundColorId;
    private BroadcastReceiver minuteUpdateReciever;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    SharedPreferences pref2;
    SharedPreferences.Editor editor2;
    private HashMap<String, TaskObject> listMap;
    private HashMap<String, Object> listInfo;
    private Toolbar toolbar;
    private Boolean save = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.darkTheme);
        }else setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_list_view); //activity view set
        Intent intent = getIntent(); // prepares an intent for later use
        background = findViewById(R.id.listLayout);
        pref = getApplicationContext().getSharedPreferences("Check", 0);
        editor = pref.edit();
        pref2 = getApplicationContext().getSharedPreferences("Check-d", 0);
        editor2 = pref2.edit();
        listInfo = new HashMap<String, Object>();
        listMap = new HashMap<String, TaskObject>();
        String savedMap;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        taskListStarter();



        if(intent.getIntExtra("mode", 1) == 0) {
            listID = intent.getStringExtra("listID");
            listName = intent.getStringExtra("listName");
            if (listID.equals("none")) {
                listID = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("list").push().getKey();
            }

            if(pref2.contains(listID) && !(intent.getBooleanExtra("lastUpdate", false))){ // if the list is saved locally and the local version isn't older than the online version
            savedMap = pref2.getString(listID, "");
            listLoader(savedMap);
            } else { // if load is online only at the moment
                final Map downloadedMap = new HashMap<>();

                DatabaseReference mUserListDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("data");
                readData(mUserListDB, new OnGetDataListener(){
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                                if (listID.equals(childSnapshot.getKey())) { // checks if the list already exists offline and is loaded already. Supposed to skip if it exists.

                                    Log.d("Online load status", "Do you see me now?");
                                    String mapString = (String) childSnapshot.getValue(); // should grab the map string?
                                    downloadedMap.put("stringMap", (String) childSnapshot.getValue());
                                    listLoader((String) downloadedMap.get("stringMap"));
                                    Log.d("Online load status", "SUCCESS??");

                                } else continue;
                            }
                            lTaskAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onStart() {
                        Log.d("Online load status", "Your request hasn't been ignored! Hurray! Loading!");
                    }

                    @Override
                    public void onFailure() {
                        Log.d("Online load status", "FAILED");
                    }
                });

            }

            mediaURI = intent.getStringExtra("mediaURI");

            toolbar = findViewById(R.id.listToolBar); // list toolbar grabbed
            toolbar.setTitle(listName);// sets the local string variable to be the title of the toolbar.
            setSupportActionBar(toolbar);
            backgroundColorId = intent.getDoubleExtra("listBackground", 0.0);
            attachColorSettings(backgroundColorId);
            listCategory = intent.getStringExtra("listCategory");
            listInfo.put("category", listCategory);
            listInfo.put("background", backgroundColorId);
            listInfo.put("name", listName);
            listInfo.put("ID", listID);
            storagePointer = taskList.size();
            lTaskAdapter.notifyDataSetChanged();
        }

        else if(intent.getIntExtra("mode", 1) == 1){ // if the list was created, instead of loaded.
            listID = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("list").push().getKey();
            toolbar = findViewById(R.id.listToolBar); // list toolbar grabbed
            listName = intent.getStringExtra("listName"); //grabs the name from MainActivity, which got it from the dialog
            toolbar.setTitle(listName);// sets the local string variable to be the title of the toolbar.
            listCategory = "None"; // temp fix for nulls
            listInfo.put("name", listName);
            listInfo.put("category", listCategory);
            listInfo.put("ID", listID);
            setSupportActionBar(toolbar);
            backgroundColorId = 0;
            attachColorSettings(backgroundColorId);
            taskListStarter(); // function to start the recycler view
            storagePointer = taskList.size();}
    }

    private void listLoader(String savedMap){
        Gson gson = new Gson();
        gson.newBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        java.lang.reflect.Type type = new TypeToken<HashMap<String, TaskObject>>() {}.getType();
        listMap = gson.fromJson(savedMap, type);
        for (int i = 0; i < (listMap.size()); i++) {
            taskList.add(listMap.get(Integer.toString(i)));
            if (taskList.get(i).getTimerState() == 1){
                taskList.get(i).setNotificationListener(new TaskObject.notificationSender() {
                    @Override
                    public void onNotificationReady(String taskName, Calendar taskCalendar, int taskID) {
                        Intent notificationIntent = new Intent(List_Activity.this, ReminderBroadcast.class);
                        notificationIntent.putExtra("id", taskID);
                        notificationIntent.putExtra("listName", listName);
                        notificationIntent.putExtra("taskName", taskName);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(List_Activity.this, taskID, notificationIntent, 0);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.cancel(pendingIntent);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, taskCalendar.getTimeInMillis(), pendingIntent);
                    }
                });
            }
            taskList.get(i).importTimerHandler();

        }

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
                bundle.putString("name", listName);
                bundle.putDouble("colorId", backgroundColorId);
                bundle.putString("listId", listID);
                editDialog.setArguments(bundle);
                editDialog.show(getSupportFragmentManager(), "Edit List");
                return true;
            case R.id.listClear:
                taskList.clear(); //clears the arraylist
                lTaskAdapter.notifyDataSetChanged();
                listMap.clear();
                return true;
            case R.id.listDelete:
                List_Delete_Dialog delete_dialog = new List_Delete_Dialog();
                Bundle deleteBundle = new Bundle();
                deleteBundle.putString("name", listName);
                delete_dialog.setArguments(deleteBundle);
                delete_dialog.show(getSupportFragmentManager(), "delete list");

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

    @Override
    public void attachTaskSettings(String _taskName, String _taskDescription, Calendar date, int taskTimerToggle){ // created a task from a successful dialog- with a due date
        TaskObject newTask = new TaskObject(_taskName, _taskDescription, date, taskTimerToggle);
        taskList.add(newTask);//add to arraylist
        lTaskAdapter.notifyDataSetChanged();//update recycler
        storagePointer = taskList.size();
        listMap.put(Integer.toString((storagePointer -1)), taskList.get((storagePointer -1)));

        Intent notificationIntent = new Intent(List_Activity.this, ReminderBroadcast.class);
        notificationIntent.putExtra("id", newTask.getTaskID());
        notificationIntent.putExtra("listName", listName);
        notificationIntent.putExtra("taskName", _taskName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(List_Activity.this, newTask.getTaskID(), notificationIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, newTask.getCalendar().getTimeInMillis(), pendingIntent);
    }

    @Override
    public void attachTaskSettings(String _taskName, String _taskDescription, Calendar date, int taskTimerToggle, int recursionTimerToggle) { //created a task from a successful dialog - with a recursion timer
        TaskObject newTask = new TaskObject(_taskName, _taskDescription, date, taskTimerToggle, recursionTimerToggle);
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
    public void attachUpdatedTaskSettings(String _taskName, String _taskDescription, int _position, int timerToggle) { // Listener that updates a task at a specific position in the arraylist with new information, such as name and description. (Dialog created in TaskAdapter, dialog is Edit_Task_Dialog)
        taskList.get(_position).setTaskName(_taskName);
        taskList.get(_position).setTaskDescription(_taskDescription);
        if(taskList.get(_position).getTimerState() != 0){
            taskList.get(_position).updateDueTimer();
        }
        lTaskAdapter.notifyDataSetChanged();
        listMap.remove(Integer.toString(_position));
        listMap.put(Integer.toString(_position), taskList.get(_position));

        Intent notificationIntent = new Intent(List_Activity.this, ReminderBroadcast.class);
        notificationIntent.putExtra("id", taskList.get(_position).getTaskID());
        notificationIntent.putExtra("listName", listName);
        notificationIntent.putExtra("taskName", _taskName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(List_Activity.this, taskList.get(_position).getTaskID(), notificationIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public void attachUpdatedTaskSettings(String _taskName, String _taskDescription, int _position, Calendar _dueDate, int timerToggle){
        taskList.get(_position).setTaskName(_taskName);
        taskList.get(_position).setTaskDescription(_taskDescription);
        taskList.get(_position).updateDueTimer(_dueDate, timerToggle);
        lTaskAdapter.notifyDataSetChanged();
        listMap.remove(Integer.toString(_position));
        listMap.put(Integer.toString(_position), taskList.get(_position));

        Intent notificationIntent = new Intent(List_Activity.this, ReminderBroadcast.class);
        notificationIntent.putExtra("id", taskList.get(_position).getTaskID());
        notificationIntent.putExtra("listName", listName);
        notificationIntent.putExtra("taskName", _taskName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(List_Activity.this, taskList.get(_position).getTaskID(), notificationIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, taskList.get(_position).getCalendar().getTimeInMillis(), pendingIntent);
    }

    @Override
    public void attachUpdatedTaskSettings(String _taskName, String _taskDescription, int _position, Calendar _dueDate, int timerToggle, int recursionToggle) {
        taskList.get(_position).setTaskName(_taskName);
        taskList.get(_position).setTaskDescription(_taskDescription);
        taskList.get(_position).setRecursionCode(recursionToggle);
        taskList.get(_position).updateDueTimer(_dueDate, timerToggle);
        lTaskAdapter.notifyDataSetChanged();
        listMap.remove(Integer.toString(_position));
        listMap.put(Integer.toString(_position), taskList.get(_position));

        Intent notificationIntent = new Intent(List_Activity.this, ReminderBroadcast.class);
        notificationIntent.putExtra("id", taskList.get(_position).getTaskID());
        notificationIntent.putExtra("listName", listName);
        notificationIntent.putExtra("taskName", _taskName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(List_Activity.this, taskList.get(_position).getTaskID(), notificationIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void attachColorSettings(int colorId) { // listener for the list color swap (List_Color_Settings)
        backgroundColorId = colorId;

        if(colorId == 0) {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){setBackground(R.drawable.dbackground_yellow);}
            else {setBackground(R.drawable.background_yellow);}}
        else if(colorId == 1){
            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {setBackground(R.drawable.dbackground_blue);}
            else {setBackground(R.drawable.background_blue);}}
        else if(colorId == 2) {
            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){setBackground(R.drawable.dbackground_green);}
        else {setBackground(R.drawable.background_green);}}
        else if(colorId == 3) {
            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {setBackground(R.drawable.dbackground_purple);}
            else {
                //setBackground(R.drawable.background_purple);
                try {
                    URL url = new URL("https://images.unsplash.com/photo-1577971828613-9872f39c0825?ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80");
                    Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    Drawable image = new BitmapDrawable(getApplicationContext().getResources(), bitmap);
                    background.setBackground(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }}

        if(listInfo.containsKey("background")){listInfo.remove("background");}
        listInfo.put("background", backgroundColorId);
    }
    public void attachColorSettings(double colorId){
        backgroundColorId = colorId;
        if(colorId == 0) {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){setBackground(R.drawable.dbackground_yellow);}
            else {setBackground(R.drawable.background_yellow);}}
        else if(colorId == 1){
            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {setBackground(R.drawable.dbackground_blue);}
            else {setBackground(R.drawable.background_blue);}}
        else if(colorId == 2) {
            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){setBackground(R.drawable.dbackground_green);}
            else {setBackground(R.drawable.background_green);}}
        else if(colorId == 3) {
            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {setBackground(R.drawable.dbackground_purple);}
            else {
                //setBackground(R.drawable.background_purple);
                try {
                    URL url = new URL("https://images.unsplash.com/photo-1577971828613-9872f39c0825?ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80");
                    Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    Drawable image = new BitmapDrawable(getApplicationContext().getResources(), bitmap);
                    background.setBackground(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }}
        else if(colorId == -1){
            try {
                URL url = new URL(mediaURI);
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                Drawable image = new BitmapDrawable(getApplicationContext().getResources(), bitmap);
                background.setBackground(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                lTaskAdapter.notifyDataSetChanged();
            }
        };
        registerReceiver(minuteUpdateReciever, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(minuteUpdateReciever);

            DatabaseReference listDb = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("list").child(listID);
            DatabaseReference dataDb = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("data").child(listID);
            if(!save){
                if(pref2.contains(listID)){ // deletes the task list itself
                    editor2.remove(listID);
                    editor2.commit();
                    dataDb.removeValue();
                }
                if(pref.contains(listID)){
                    editor.remove(listID);
                    editor.commit();
                    listDb.removeValue(); // the id remains in firebase for cleanup of local devices too.
                }
            }
            else if(save) { // if the data isn't flagged for deletion
                String listNameSave = (String) listInfo.get("name");
             String listCategorySave = "";
                if (listInfo.containsKey("category")) {
                listCategorySave = (String) listInfo.get("category");
             } else {
                   listCategorySave = "None";
             }
             double listBackground = (double) listInfo.get("background");
             String listIDSave = (String) listInfo.get("ID");
             Calendar date = Calendar.getInstance();


             if(mediaURI == null){mediaURI = "none";}
             ListObject currentList = new ListObject(listNameSave, listCategorySave, listBackground, listIDSave, date, mediaURI);
             HashMap<String, ListObject> finalList = new HashMap<String, ListObject>();
             finalList.put("list", currentList);

                Gson gson = new Gson();
                gson.newBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

                String taskMapString = gson.toJson(listMap);
                if (pref2.contains(listID)) {
                    editor2.remove(listID);
                }
                editor2.putString(listID, taskMapString);
                editor2.commit();
                dataDb.setValue(taskMapString);

                String listSettings = gson.toJson(finalList);
                if (pref.contains(listID)) {
                    editor.remove(listID);
                }
                editor.putString(listID, listSettings);
                editor.commit();
                listDb.setValue(listSettings);
            }
        }

    protected void onResume() {
        super.onResume();
        startAutoUpdater();
    }

    @Override
    public void attachListSettings(String category, String name, String mediaID, String mediaURI, double colorId, boolean imagebackgroundupdate) {
        listCategory = category;
        listName = name;
        if (listInfo.containsKey("category")) {
            listInfo.remove("category");
        }
        if (listInfo.containsKey("name")) {
            listInfo.remove("name");
        }
        listInfo.put("category", listCategory);
        listInfo.put("name", listName);
        toolbar.setTitle(listName);

        if (imagebackgroundupdate) {
            backgroundColorId = colorId;
            if (listInfo.containsKey("background")) {
                listInfo.remove("background");
            }
            listInfo.put("background", backgroundColorId);

            if (!mediaID.equals("None")) {
                if (listInfo.containsKey("mediaID")) {
                    listInfo.remove("mediaID");
                    listInfo.put("mediaID", mediaID);
                } else {
                    listInfo.put("mediaID", "None");
                }
            }
            if (!mediaURI.equals("None")) {
                if (listInfo.containsKey("mediaURI")) {
                    listInfo.remove("mediaURI");
                }
                listInfo.put("mediaURI", mediaURI);
                try {
                    URL url = new URL(mediaURI);
                    Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    Drawable image = new BitmapDrawable(getApplicationContext().getResources(), bitmap);
                    background.setBackground(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void readData(DatabaseReference ref, final OnGetDataListener listener){
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }

    @Override
    public void deleteList() {
        save = false;// removes saving flag
        Intent intent = new Intent(getApplicationContext(), MainActivity.class); // sends user back to mainactivity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    public interface OnGetDataListener {
        void onSuccess(DataSnapshot dataSnapshot);
        void onStart();
        void onFailure();
    }
}

