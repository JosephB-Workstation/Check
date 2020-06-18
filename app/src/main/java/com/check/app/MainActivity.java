package com.check.app;

import androidx.annotation.NonNull;
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

import com.check.app.List_Stuff.ListObject;
import com.check.app.List_Stuff.List_Activity;
import com.check.app.List_Stuff.TListAdapter;
import com.check.app.Task_Stuff.TaskAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Create_List_Dialog.CreateListListener, AdapterView.OnItemSelectedListener { //main logged in menu
    private RecyclerView lLists; //First three variables are necessary for recycler view
    private TListAdapter lListAdapter;
    private RecyclerView.LayoutManager lListLayoutManager;
    private ArrayList<ListObject> listOfLists;
    private ArrayList<ListObject> categoryFilteredLists;
    private ArrayList<String> categories;
    private ArrayList<String> listIDs;
    SharedPreferences pref;
    EditText searchBar;
    Spinner categorySpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //sets activity view for mainmenu

        listIDs = new ArrayList<String>();

        Toolbar toolbar = findViewById(R.id.mainToolBar); //generates toolbar for mainmenu
        pref = getApplicationContext().getSharedPreferences("Check", 0);
        setSupportActionBar(toolbar);
        ListStarter(); // function to initiate the main menu list
        searchBar = findViewById(R.id.searchBar);



        searchBar.addTextChangedListener(new TextWatcher() { //search bar text listener
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }//unused
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            } // unused
            @Override
            public void afterTextChanged(Editable s) { // after the text has been edited
                filter(s.toString());
            } // uses filter function. See below.
        });

        categories = new ArrayList<String>(); // list of categories
        categoryFilteredLists = new ArrayList<ListObject>(); // filter by category

    }


    private void filter(String text){ // makes a new list of lists to filter with, containing any list that has the text input match
        ArrayList<ListObject> filteredList = new ArrayList<>();
        if(categoryFilteredLists.size() == 0) { // if the categories are not influencing the list (aka is 0) then it will filter out from the entire list of lists
            for (ListObject item : listOfLists) {
                if (item.getListName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        else{
            for(ListObject item : categoryFilteredLists){ // if the category system is infact filtering content, it will filter items from the content that survived the category filter.
                if ((item.getListName().toLowerCase().contains(text.toLowerCase()))){
                    filteredList.add(item);
                }
            }
        }
        lListAdapter.filterList(filteredList); // tells the adapter to just look at these
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
                list_dialog.show(getSupportFragmentManager(), "Create List"); //create list dialog generated
                return true;

            case R.id.logOut:
                FirebaseAuth.getInstance().signOut(); //firebase log out
                GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);// large chunk seemingly necessary for google logout
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                GoogleSignIn.getClient(this, gso).signOut();
                Intent intent = new Intent(getApplicationContext(), Login_Activity.class); // send the logged out user back to the login screen
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

    private void ListStarter(){ // starts recycler view.
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
        listOfLists.clear(); //clears all lists incase of adding of lists
        listIDs.clear(); // clears list of active ids
        categories.clear(); // clears all categories incase of removal or addition of categories.
        categories.add("All"); // adds the all category
        searchBar.getText().clear(); // clears search bar after you return to the main screen
        Map<String, ?> allLists = pref.getAll(); // grabs all list of lists
        for(Map.Entry<String, ?> entry : allLists.entrySet()){ // offline map grabbing.
            String listKey = entry.getKey(); //grabs key of list
            mapGrabber(listKey); // activates map grabber function, see below.
        }

        DatabaseReference mUserListDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("list");

        readData(mUserListDB, new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot childSnapshot:dataSnapshot.getChildren()){
                        if(!listIDs.contains(childSnapshot.getKey())) { // checks if the list already exists offline and is loaded already. Supposed to skip if it exists.
                            String mapString = (String) childSnapshot.getValue(); // should grab the map string from firebase
                            listConvert(mapString); // helper method to assist in online conversion
                        }
                        else { // gets ran if the list is loaded offline already.
                            String mapString = (String) childSnapshot.getValue(); // should grab the map string from firebase
                            listConvertAndCompare(mapString); //if the list exists on both platforms, this will check to see which one is newer. If firebase's version is newer, it will be flagged as such for firebase loading instead.


                        }
                    }
                    lListAdapter.notifyDataSetChanged(); // update the list after the process of getting online data down
                }
            }

            @Override
            public void onStart() {
                Log.d("MainListListener", "active!");
            }

            @Override
            public void onFailure() {
                Log.e("MainListListener", "FAILED!");
            }
        });

        lListAdapter.notifyDataSetChanged(); // loads local content
        categorySpinner = findViewById(R.id.categorySelectSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories); // enables category selector
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(this);

        lListAdapter.filterList(listOfLists);// resets the category spinner whenever the page refreshes.
        if(categorySpinner != null){
        categorySpinner.setSelection(0);}
    }



    private void mapGrabber(String listKey) { // offline load
        HashMap<String, Object> newMap;
        Gson gson = new Gson();
        gson.newBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        java.lang.reflect.Type type = new TypeToken<HashMap<String, ListObject>>() {
        }.getType();
        String savedMap = pref.getString(listKey, "uhoh"); // gets the gson string for each list of lists
        newMap = gson.fromJson(savedMap, type); // pulls map off of the gson string
        ListObject newList = (ListObject) newMap.get("list");
        if (newList.getListOwnerId().equals(FirebaseAuth.getInstance().getUid())) {
            listOfLists.add(newList);// adds list object to list of lists
            if (!(categories.contains(newList.getListCategory().toLowerCase())) && !(newList.getListCategory().equals("None") && !(newList.getListCategory().equals("All")))) { // checks to see if the list happened to contain a new category it should document.
                categories.add(newList.getListCategory().toLowerCase());
            }

            if (!(listIDs.contains(newList.getListID()) && !newList.getListID().equals("none")))
                listIDs.add(newList.getListID()); // adds to offline stored list of lists.
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        listUpdater(); // updates list whenever you come back to the app
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { // process to filter lists by category
        String text = parent.getItemAtPosition(position).toString(); //grabs category
        if (text.equals("All")){ // if category is all, reset category filter and search filter
            lListAdapter.filterList(listOfLists);
            if(categoryFilteredLists != null){
            categoryFilteredLists.clear();}
            searchBar.getText().clear();
        }
        else{ // filter out any list that doesn't contain the specified category. Resets search
            text = text.toLowerCase();
            categoryFilteredLists.clear();
            for(ListObject item : listOfLists){ // loops through loaded lists.
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


    public interface OnGetDataListener { // listener interface enabling proper loading from firebase
        void onSuccess(DataSnapshot dataSnapshot);
        void onStart();
        void onFailure();
    }

    private void readData(DatabaseReference ref, final OnGetDataListener listener){ // dictates how the listeners will be used
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot); //successful downloads send the data off.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }

    private void listConvert(String listString){ // online->main menu list conversion. works largely the same as mapgrabber, but isn't actively grabbing data.
        HashMap<String, ListObject> newMap = new HashMap<String, ListObject>();
        Gson gson = new Gson();
        gson.newBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        java.lang.reflect.Type type = new TypeToken<HashMap<String, ListObject>>() {}.getType();
        newMap = gson.fromJson(listString, type); // pulls map off of the gson string
        ListObject newList = (ListObject) newMap.get("list");
        listOfLists.add(newList);
        if(!(categories.contains(newList.getListCategory().toLowerCase())) && !(newList.getListCategory().equals("None") && !(newList.getListCategory().equals("All")))){ // checks to see if the list happened to contain a new category it should document.
            categories.add(newList.getListCategory().toLowerCase());
        }
    }

    private void listConvertAndCompare(String listString){ // online->main menu list conversion. works largely the same as mapgrabber, but isn't actively grabbing data. It just checks if firebase has a newer copy of the list than the local copy
        HashMap<String, ListObject>newMap;
        Gson gson = new Gson();
        gson.newBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        java.lang.reflect.Type type = new TypeToken<HashMap<String, ListObject>>() {}.getType();
        newMap = gson.fromJson(listString, type); // pulls map off of the gson string
        ListObject newList = (ListObject) newMap.get("list");
        Calendar online = newList.getLastEdit();
        int pos = 0;
        for(ListObject item : listOfLists){
            if(item.getListID().equals(newList.getListID())){
                break;
            }else{pos++; continue;}
        }
        Calendar offline = listOfLists.get(pos).getLastEdit();
        double status = online.compareTo(offline);
        if(status == 1){
            listOfLists.get(pos).isNewerOnline();
            listOfLists.get(pos).setListName(newList.getListName());
            listOfLists.get(pos).setListCategory(newList.getListCategory());
            listOfLists.get(pos).setListBackgroundId(newList.getListBackgroundId());
            lListAdapter.notifyDataSetChanged();
        }
        else{}


    }
}
