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

public class MainActivity extends AppCompatActivity implements Create_List_Dialog.CreateListListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.mainToolBar); //generates toolbar for mainmenu
        setSupportActionBar(toolbar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainactivity_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.addList:
              // startActivity(new Intent(getApplicationContext(), Create_List_Activity.class));
                Create_List_Dialog list_dialog = new Create_List_Dialog();
                list_dialog.show(getSupportFragmentManager(), "Create List");
                return true;
            case R.id.logOut:
                //code here for logging out when  firebase support is added
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void attachListSettings(String _listName) { // handles pulling data from the dialog box for making lists. Passes list metadata via intent.
        Intent listIntent = new Intent(MainActivity.this, List_Activity.class);
        listIntent.putExtra("listName", _listName);
        startActivity(listIntent);

    }
}
