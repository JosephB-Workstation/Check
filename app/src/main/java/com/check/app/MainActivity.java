package com.check.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.check.app.List_Stuff.ListObject;
import com.check.app.List_Stuff.List_Activity;
import com.check.app.List_Stuff.TListAdapter;
import com.check.app.Task_Stuff.TaskAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Create_List_Dialog.CreateListListener{ //main logged in menu
    private RecyclerView lLists; //First three variables are necessary for recycler view
    private RecyclerView.Adapter lListAdapter;
    private RecyclerView.LayoutManager lTaskLayoutManager;
    private ArrayList<ListObject> listOfLists;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //sets activity view for mainmenu

        Toolbar toolbar = findViewById(R.id.mainToolBar); //generates toolbar for mainmenu
        pref = getApplicationContext().getSharedPreferences("Check", 0);
        setSupportActionBar(toolbar);
        ListStarter();
        listUpdater();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){ //activates toolbar menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainactivity_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){ // switch case for the toolbar menu
        switch (item.getItemId()){
            case R.id.addList://creates a new list
                Create_List_Dialog list_dialog = new Create_List_Dialog();
                list_dialog.show(getSupportFragmentManager(), "Create List");
                return true;
            case R.id.logOut:
                //code here for logging out when  firebase support is added
                return true;
            default://if this ever gets run, something has gone wrong.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void attachListSettings(String _listName) { // handles pulling data from the dialog box for making lists. Passes list metadata via intent.
        Intent listIntent = new Intent(MainActivity.this, List_Activity.class); //creates an intent
        listIntent.putExtra("listName", _listName); //places the list name into the intent so it can be used in the list activity
        listIntent.putExtra("mode", 1);
        startActivity(listIntent); // starts list activity with the packaged intent. (List_Activity)
    }

    private void ListStarter(){
        listOfLists = new ArrayList<>();//init arraylist
        lLists = findViewById(R.id.listOfLists); //recycler view pairing
        lLists.setNestedScrollingEnabled(false); // removes scary scroll wheel
        lLists.setHasFixedSize(false); // allows for the better scroll wheel to work
        lTaskLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false); // layout manager that handles which way the recycler view appends items to a list
        lLists.setLayoutManager(lTaskLayoutManager);// configures layout manager for list page
        lListAdapter = new TListAdapter(listOfLists, this.getSupportFragmentManager()); // constructor for class: TaskAdapter, which handles checkboxes and edits.
        lLists.setAdapter(lListAdapter);//configures adapter to work here.
    }

    private void listUpdater(){
        listOfLists.clear();
        Map<String, ?> allLists = pref.getAll();
        for(Map.Entry<String, ?> entry : allLists.entrySet()){
            String listKey = entry.getKey();
            mapGrabber(listKey);
        }
        lListAdapter.notifyDataSetChanged();
    }

    private void mapGrabber(String listKey){
        HashMap<String, Object> newMap = new HashMap<String, Object>();
        Gson gson = new Gson();
        String savedMap = pref.getString(listKey, "uhoh");
        java.lang.reflect.Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        newMap = gson.fromJson(savedMap, type);
/*        try{
            String jsonString = pref.getString(listKey, (new JSONObject()).toString());
            JSONObject jsonObject = new JSONObject((jsonString));
            Iterator<String> keysItr = jsonObject.keys();
            while(keysItr.hasNext()){
                String key = keysItr.next();
                Object value = null;
                if(key.equals("name")){
                    value = jsonObject.get(key);
                    newMap.put(key, value);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }*/
        ListObject newList = new ListObject(newMap);
        listOfLists.add(newList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listUpdater();
    }
}
