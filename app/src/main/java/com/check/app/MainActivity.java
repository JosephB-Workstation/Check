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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.check.app.List_Stuff.Category_Search_Dialog;
import com.check.app.List_Stuff.ListObject;
import com.check.app.List_Stuff.List_Activity;
import com.check.app.List_Stuff.TListAdapter;
import com.check.app.Task_Stuff.TaskAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Create_List_Dialog.CreateListListener, Category_Search_Dialog.SearchCategoryListener, AdapterView.OnItemSelectedListener { //main logged in menu
    private RecyclerView lLists; //First three variables are necessary for recycler view
    private TListAdapter lListAdapter;
    private RecyclerView.LayoutManager lListLayoutManager;
    private ArrayList<ListObject> listOfLists;
    private ArrayList<ListObject> categoryFilteredLists;
    private ArrayList<String> categories;
    SharedPreferences pref;
    EditText searchBar;
    Spinner categorySpinner;


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

        categories = new ArrayList<String>();
        categoryFilteredLists = new ArrayList<ListObject>();

    }
    private void filter(String text){
        ArrayList<ListObject> filteredList = new ArrayList<>();
        if(categoryFilteredLists.size() == 0) {
            for (ListObject item : listOfLists) {
                if (item.getListName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        else{
            for(ListObject item : categoryFilteredLists){
                if ((item.getListName().toLowerCase().contains(text.toLowerCase()))){
                    filteredList.add(item);
                }
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
                FirebaseAuth.getInstance().signOut();
                GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                GoogleSignIn.getClient(this, gso).signOut();
                Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
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
        categories.clear();
        categories.add("All");
        searchBar.getText().clear();
        Map<String, ?> allLists = pref.getAll();
        for(Map.Entry<String, ?> entry : allLists.entrySet()){
            String listKey = entry.getKey();
            mapGrabber(listKey);
        }

        categorySpinner = findViewById(R.id.categorySelectSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(this);

        lListAdapter.filterList(listOfLists);
        if(categorySpinner != null){
        categorySpinner.setSelection(0);}
    }

    private void mapGrabber(String listKey){
        HashMap<String, Object> newMap = new HashMap<String, Object>();
        Gson gson = new Gson();
        String savedMap = pref.getString(listKey, "uhoh");
        java.lang.reflect.Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        newMap = gson.fromJson(savedMap, type);
        ListObject newList = new ListObject(newMap);
        listOfLists.add(newList);
        if(!(categories.contains(newList.getListCategory().toLowerCase())) && !(newList.getListCategory().equals("None") && !(newList.getListCategory().equals("All")))){
            categories.add(newList.getListCategory().toLowerCase());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        listUpdater();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        if (text.equals("All")){
            lListAdapter.filterList(listOfLists);
            if(categoryFilteredLists != null){
            categoryFilteredLists.clear();}
            searchBar.getText().clear();
        }
        else{
            text = text.toLowerCase();
            categoryFilteredLists.clear();
            for(ListObject item : listOfLists){
                if(item.getListCategory().toLowerCase().equals(text)){{
                    categoryFilteredLists.add(item);
                }}
            }
            lListAdapter.filterList(categoryFilteredLists);
            searchBar.getText().clear();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void searchCategory(String _listName) {

    }
}
