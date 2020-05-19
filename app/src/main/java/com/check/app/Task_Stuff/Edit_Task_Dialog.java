package com.check.app.Task_Stuff;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.check.app.R;

import java.util.ArrayList;

public class Edit_Task_Dialog extends DialogFragment {
    private EditText taskNameEntry, taskDescriptionEntry;
    private editTaskListener taskListener;
    private int position;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_task, null); //bureaucracy to make custom dialog
        taskNameEntry = view.findViewById(R.id.taskName);//instantiate name and description
        taskDescriptionEntry = view.findViewById(R.id.taskDescription);
        taskNameEntry.setText(getArguments().getString("taskName"), TextView.BufferType.EDITABLE); // because this is an edit I wanted to have the ability to see the old text in the edits, the next handful of lines pull necessary data for later in bundle
        taskDescriptionEntry.setText(getArguments().getString("taskDescription"), TextView.BufferType.EDITABLE);
        position = getArguments().getInt("position"); // array list index, because we're editing a specific entry.
        String title = ("Editing " + getArguments().get("taskName") + ":"); // editing <task name>:
        dialogBuilder.setView(view)
                .setTitle(title)
                .setPositiveButton("Save Task", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            if (!taskNameEntry.getText().toString().trim().isEmpty()) {
                                String taskName = taskNameEntry.getText().toString();
                                String taskDescription = taskDescriptionEntry.getText().toString();
                                taskListener.attachUpdatedTaskSettings(taskName,  taskDescription, position); //puts position into the mix so we can edit the entry directly/
                            }
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
        taskListener = (editTaskListener)context;//instantiate listener
    }

    public interface editTaskListener{
        void attachUpdatedTaskSettings(String _taskName, String _taskDescription, int _position);//sends the dialog information and position information to the List_Activity
    }

}

