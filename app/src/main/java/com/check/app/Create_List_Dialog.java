package com.check.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;


public class Create_List_Dialog extends DialogFragment {
    private Boolean darkMode;
    private EditText listNameEntry;
    private CreateListListener listListener;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //dark mode handle
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            darkMode = true;
        }else darkMode = false;

        MaterialAlertDialogBuilder dialogBuilder = null;
        if(darkMode){
            dialogBuilder = new MaterialAlertDialogBuilder(getActivity(), R.style.DarkDialogCustom);}
        else {dialogBuilder = new MaterialAlertDialogBuilder(getActivity(), R.style.LightDialogCustom);}

        //AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity()); //custom layout
        LayoutInflater inflater = requireActivity().getLayoutInflater(); // inflater for custom layout
        View view = inflater.inflate(R.layout.dialog_create_list, null); // view for the custom inflater, and setting the layout file.
        dialogBuilder.setView(view)
                .setTitle("Create a new list")//title
                .setPositiveButton("Create list", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { //any settings should be configured here while you have the chance, and sent to attachListSettings within the interface.
                        if(!listNameEntry.getText().toString().trim().isEmpty()){ //if statement handles empty names.
                            String listName = listNameEntry.getText().toString();
                            listListener.attachListSettings(listName); //adds listed variables into listener for delivery
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        listNameEntry = view.findViewById(R.id.listName);
        return dialogBuilder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listListener = (CreateListListener)context; // instantiates listener
    }

    public interface CreateListListener{
        void attachListSettings(String _listName); // interface to attach to the main page that uses this.
    }

}
