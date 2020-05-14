package com.check.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.check.app.List_Stuff.List_Activity;

import java.util.Random;

public class Create_List_Dialog extends DialogFragment {


    private EditText listNameEntry;
    private CreateListListener listListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_list, null);
        dialogBuilder.setView(view)
                .setTitle("Create a new list")
                .setPositiveButton("Create list", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { //any settings should be configured here while you have the chance, and sent to attachListSettings within the interface.
                        if(!listNameEntry.getText().toString().trim().isEmpty()){ //if statement handles empty names.
                            String listName = listNameEntry.getText().toString();
                            listListener.attachListSettings(listName);
                        }
                        else Log.d("False", "It worked");
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
        listListener = (CreateListListener)context;
    }

    public interface CreateListListener{
        void attachListSettings(String _listName);
    }

}
