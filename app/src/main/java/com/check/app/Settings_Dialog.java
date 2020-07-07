package com.check.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

public class Settings_Dialog extends DialogFragment {
    private Boolean darkMode;
    private Button darkModeToggle;
    private AppSettingsEditor appSettingsEditor;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        //Dark mode button handling
        //state init
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            darkMode = true;
        } else darkMode = false;
        //if(darkMode) AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.darkTheme));
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater(); // inflater for custom layout
        View view = inflater.inflate(R.layout.dialog_settings, null); // view for the custom inflater, and setting the layout file.

        //Dark mode button handling
        //state init
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            darkMode = true;
        } else darkMode = false;

        //button init
        darkModeToggle = view.findViewById(R.id.darkButton);
        if(darkMode){
            darkModeToggle.setText("Dark Mode: True");
        }else darkModeToggle.setText("Dark Mode: False");

        //tap handler
        darkModeToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(darkMode) darkMode = false; else darkMode = true;
                if(darkMode) darkModeToggle.setText("Dark Mode: True"); else darkModeToggle.setText("Dark Mode: False");
            }
        });

        dialogBuilder.setView(view)
                .setTitle("App Settings: ")
                .setPositiveButton("Update settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appSettingsEditor.attachSettings(darkMode);
                    }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        darkModeToggle = view.findViewById(R.id.darkButton);
        AlertDialog dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(R.attr.cardBackground));
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        appSettingsEditor = (AppSettingsEditor)context; // instantiates listener
    }

    public interface AppSettingsEditor{
        void attachSettings(Boolean darkMode);
    }
}
