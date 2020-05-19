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

public class Create_Task_Dialog extends DialogFragment {
    private EditText taskNameEntry, taskDescriptionEntry;
    private CreateTaskListener taskListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_task, null);
        taskNameEntry = view.findViewById(R.id.taskName);
        taskDescriptionEntry = view.findViewById(R.id.taskDescription);
        dialogBuilder.setView(view)
                .setTitle("Create a new task")
                .setPositiveButton("Create Task", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            if (!taskNameEntry.getText().toString().trim().isEmpty()) {
                                String taskName = taskNameEntry.getText().toString();
                                String taskDescription = taskDescriptionEntry.getText().toString();
                                taskListener.attachTaskSettings(taskName, taskDescription);

                            } else
                                Log.d("Success Check", "Task should not have been added! was empty!");
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
        taskListener = (CreateTaskListener)context;
    }

    public interface CreateTaskListener{
        void attachTaskSettings(String _taskName, String _taskDescription);
    }
}

