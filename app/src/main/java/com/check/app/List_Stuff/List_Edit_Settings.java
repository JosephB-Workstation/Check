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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.check.app.R;
import com.check.app.Task_Stuff.Edit_Task_Dialog;


public class List_Edit_Settings extends DialogFragment  implements List_Color_Settings.ColorSettingsListListener{
    private double colorId;
    private String category;
    private Button backgroundPicker;
    private EditText categorySelector;
    private ListEditListener listEditListener;

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_list_settings, null);//bureaucracy of setting up a dialog
        backgroundPicker =  view.findViewById(R.id.backgroundEditor);
        categorySelector = view.findViewById(R.id.categoryEditor);

        if(getArguments().containsKey("colorId")){
            colorId = getArguments().getDouble("colorId");
        } else {colorId = 0;}
        colorTextUpdater(colorId);

        if(getArguments().containsKey("category")){
            category = getArguments().getString("category");
        }else{category = "None";}

        categorySelector.setText(category);
        backgroundPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List_Color_Settings color_settings = new List_Color_Settings();
                color_settings.show(getActivity().getSupportFragmentManager(), "Change color");
            }
        });
        dialogBuilder.setView(view)
                .setTitle("Edit List Settings")
                .setPositiveButton("Save settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!categorySelector.getText().toString().trim().isEmpty()){
                            category = categorySelector.getText().toString();
                        }else{category = "None";}
                        listEditListener.attachListSettings(category, colorId);

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
        void attachListSettings(String category, double colorId);
    }

    @Override
    public void attachColorSettings(int colorId) {
        this.colorId = (double)colorId;
        colorTextUpdater(this.colorId);
    }

    private void colorTextUpdater(double colorId){
        if(colorId == 0){backgroundPicker.setText("Background: Yellow");}
        else if (colorId == 1) {backgroundPicker.setText("Background: Blue");}
        else if (colorId == 2) {backgroundPicker.setText("Background: Green");}
        else if(colorId == 3) {backgroundPicker.setText("Background: Purple");}
    }
}

