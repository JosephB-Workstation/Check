package com.josephb.check.List_Stuff;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import com.josephb.check.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class List_Color_Settings extends DialogFragment {
    private boolean darkMode;
    int colorId = 0;
    private String[] colors = new String[]{"Yellow", "Blue", "Green", "Purple"}; //list of options.
    private ColorSettingsListListener colorSettingsListListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //dark state init
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            darkMode = true;
        } else darkMode = false;
        //dialog builder builder
        MaterialAlertDialogBuilder dialogBuilder = null;
        if(darkMode){
            dialogBuilder = new MaterialAlertDialogBuilder(getActivity(), R.style.DarkDialogCustom);}
        else {dialogBuilder = new MaterialAlertDialogBuilder(getActivity(), R.style.LightDialogCustom);}

        LayoutInflater inflater = requireActivity().getLayoutInflater();//bureaucracy to make custom dialog
        View view = inflater.inflate(R.layout.dialog_color_settings, null);
        dialogBuilder.setView(view)
                .setTitle("Change List Color")
                .setSingleChoiceItems(colors, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        colorId = which; // takes the array and sets the number to which ever option in the array you picked
                    }
                })
                .setPositiveButton("Change Color", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        colorSettingsListListener.attachColorSettings(colorId); //sends off your picked option for collection and use
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
        colorSettingsListListener = (ColorSettingsListListener) context; //instantiate listener
    }



    public interface ColorSettingsListListener{
        void attachColorSettings(int colorId);//sends to List_Activity.
    }

}

