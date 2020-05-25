package com.check.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.check.app.List_Stuff.List_Activity;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Create_List_Dialog.CreateListListener{ //main logged in menu
    private ArrayList<Map> listOfLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //sets activity view for mainmenu

        Toolbar toolbar = findViewById(R.id.mainToolBar); //generates toolbar for mainmenu
        setSupportActionBar(toolbar);
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
}
