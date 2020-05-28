package com.check.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.check.app.List_Stuff.Category_Search_Dialog;
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

public class MainActivity extends AppCompatActivity implements Create_List_Dialog.CreateListListener, Category_Search_Dialog.SearchCategoryListener { //main logged in menu
    private RecyclerView lLists; //First three variables are necessary for recycler view
    private TListAdapter lListAdapter;
    private RecyclerView.LayoutManager lListLayoutManager;
    private ArrayList<ListObject> listOfLists;
    private ArrayList<String> categories;
    SharedPreferences pref;
    EditText searchBar;
    Button categoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //sets activity view for mainmenu

        Toolbar toolbar = findViewById(R.id.mainToolBar); //generates toolbar for mainmenu
        pref = getApplicationContext().getSharedPreferences("Check", 0);
        setSupportActionBar(toolbar);
        ListStarter();
        searchBar = findViewById(R.id.searchBar);


        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        categoryButton = findViewById(R.id.categoryButton);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category_Search_Dialog category_search_dialog = new Category_Search_Dialog();
                category_search_dialog.show(getSupportFragmentManager(), "Category Search");
            }
        });
    }
    private void filter(String text){
        ArrayList<ListObject> filteredList = new ArrayList<>();

        for(ListObject item : listOfLists){
            if(item.getListName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        lListAdapter.filterList(filteredList);

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
        lListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false); // layout manager that handles which way the recycler view appends items to a list
        lLists.setLayoutManager(lListLayoutManager);// configures layout manager for list page
        lListAdapter = new TListAdapter(listOfLists, this.getSupportFragmentManager()); // constructor for class: TaskAdapter, which handles checkboxes and edits.
        lLists.setAdapter(lListAdapter);//configures adapter to work here.
    }

    private void listUpdater(){
        listOfLists.clear();
        searchBar.getText().clear();
        Map<String, ?> allLists = pref.getAll();
        for(Map.Entry<String, ?> entry : allLists.entrySet()){
            String listKey = entry.getKey();
            mapGrabber(listKey);
        }
        lListAdapter.filterList(listOfLists);
    }

    private void mapGrabber(String listKey){
        HashMap<String, Object> newMap = new HashMap<String, Object>();
        Gson gson = new Gson();
        String savedMap = pref.getString(listKey, "uhoh");
        java.lang.reflect.Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        newMap = gson.fromJson(savedMap, type);
        ListObject newList = new ListObject(newMap);
        listOfLists.add(newList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listUpdater();
    }

    @Override
    public void searchCategory(String _listName) {

    }
}
