package com.check.app.List_Stuff;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import com.check.app.R;
import com.check.app.Task_Stuff.Edit_Task_Dialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class List_Edit_Settings extends DialogFragment  implements List_Color_Settings.ColorSettingsListListener{
    private double colorId;
    private String category, name;
    private Button backgroundPicker;
    private EditText categorySelector, namePicker;
    private ListEditListener listEditListener;
    private Boolean darkMode;

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Dark mode button handling
        //state init
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            darkMode = true;
        } else darkMode = false;

        //builder of the builder
        MaterialAlertDialogBuilder dialogBuilder = null;
        if(darkMode){
            dialogBuilder = new MaterialAlertDialogBuilder(getActivity(), R.style.DarkDialogCustom);}
        else {dialogBuilder = new MaterialAlertDialogBuilder(getActivity(), R.style.LightDialogCustom);}

       // AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_list_settings, null);//bureaucracy of setting up a dialog
        backgroundPicker =  view.findViewById(R.id.backgroundEditor);
        categorySelector = view.findViewById(R.id.categoryEditor);
        namePicker = view.findViewById(R.id.nameEditor);



        if(getArguments().containsKey("colorId")){//grabs background color data
            colorId = getArguments().getDouble("colorId");
        } else {colorId = 0;}

        if(getArguments().containsKey("category")){// grabs category
            category = getArguments().getString("category");
        }else{category = "None";}

        if(getArguments().containsKey("name")){// grabs name
            name = getArguments().getString("name");
        }else{name = "?";}

        categorySelector.setText(category);
        namePicker.setText(name);
        backgroundPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List_Color_Settings color_settings = new List_Color_Settings();
                color_settings.show(getActivity().getSupportFragmentManager(), "Change color"); //change color button
            }
        });

        dialogBuilder.setView(view)
                .setTitle("Edit List Settings")
                .setPositiveButton("Save settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!categorySelector.getText().toString().trim().isEmpty()){
                            category = categorySelector.getText().toString();
                            name = namePicker.getText().toString();
                        }else{category = "None";}
                        listEditListener.attachListSettings(category, name);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return dialogBuilder.create();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listEditListener = (List_Edit_Settings.ListEditListener)context;//instantiate listener
    }

    public interface ListEditListener{
        void attachListSettings(String category, String name);
    }

    @Override
    public void attachColorSettings(int colorId) {
        this.colorId = (double)colorId;
    }

}

