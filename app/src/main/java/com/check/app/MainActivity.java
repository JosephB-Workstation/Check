package com.check.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements Create_List_Dialog.CreateListListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.mainToolBar);
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
    public void attatchListSettings(String listName) {
        String name;
        name = listName;
        Log.d("Passthrough Checker", name);
    }
}
