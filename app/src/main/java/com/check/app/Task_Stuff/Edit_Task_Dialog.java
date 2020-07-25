package com.check.app.Task_Stuff;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import com.check.app.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Calendar;

public class Edit_Task_Dialog extends DialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private EditText taskNameEntry, taskDescriptionEntry;
    private editTaskListener taskListener;
    private int position, year, month, day, hour, minute, timerToggle, recursionToggle;
    private Button mdueDate, mdueTime, mdueToggle, mrecursionChanger;
    private TextView dayViewer, timeViewer, taskTimeMessage;
    private RelativeLayout dataholder;
    private Boolean attachCalendar, darkMode;
    private Calendar dueDate, defaultCalendar;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //init darkmode
        //state set
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            darkMode = true;
        } else darkMode = false;

        //builder of the builder for the dialog
        MaterialAlertDialogBuilder dialogBuilder = null;
        if(darkMode){
            dialogBuilder = new MaterialAlertDialogBuilder(getActivity(), R.style.DarkDialogCustom);}
        else {dialogBuilder = new MaterialAlertDialogBuilder(getActivity(), R.style.LightDialogCustom);}

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_task, null); //bureaucracy to make custom dialog


        taskNameEntry = view.findViewById(R.id.taskName);//instantiate name and description
        taskDescriptionEntry = view.findViewById(R.id.taskDescription);
        taskNameEntry.setText(getArguments().getString("taskName"), TextView.BufferType.EDITABLE); // because this is an edit I wanted to have the ability to see the old text in the edits, the next handful of lines pull necessary data for later in bundle
        taskDescriptionEntry.setText(getArguments().getString("taskDescription"), TextView.BufferType.EDITABLE);
        position = getArguments().getInt("position"); // array list index, because we're editing a specific entry.


        //due date Buttons
        mdueDate =  view.findViewById(R.id.dueDateButton);
        mdueTime = view.findViewById(R.id.dueTimeButton);
        mdueToggle = view.findViewById(R.id.dueToggleButton);
        mrecursionChanger = view.findViewById(R.id.recursionRateButton);

        //due date Texts
        dayViewer = view.findViewById(R.id.dayView);
        timeViewer = view.findViewById(R.id.timeView);
        taskTimeMessage = view.findViewById(R.id.taskTimeMessage);

        dataholder = view.findViewById(R.id.toggleDueView);

        mrecursionChanger.setText("Recurs Daily");
        recursionToggle = 1; // default recursion of daily.

        if(getArguments().containsKey("calendar")) { // if a duedate or recursion date exists
            int timeCode = getArguments().getInt("timecode");
            dueDate = (Calendar) getArguments().getSerializable("calendar");
            attachCalendar = true; //fill in information about the calendar to edit
            dataholder.setVisibility(View.VISIBLE);
            mdueToggle.setText("Due Toggle: on");
            year = dueDate.get(dueDate.YEAR) ;
            month = dueDate.get(dueDate.MONTH);
            day = dueDate.get(dueDate.DAY_OF_MONTH);
            hour = dueDate.get(dueDate.HOUR_OF_DAY);
            minute = dueDate.get(dueDate.MINUTE);
            dayViewer.setText(new StringBuilder().append(this.month +1).append("/").append(this.day).append("/").append(this.year).append(" "));
            TimeBuilder(); // private function to properly build time in military time (01:05, as opposed to 1:5)
            if(timeCode == 1){// if the edited task has a duedate, special features here
                mrecursionChanger.setVisibility(View.INVISIBLE);
                mrecursionChanger.setText("Recurs Daily");
                timerToggle = 1;
                recursionToggle = 1;
                mdueToggle.setText("Due Toggle: on");
            }
            else if (timeCode == 2){//if the edited task has a recursion date, special features added here
                recursionToggle = getArguments().getInt("recursion");
                switch (recursionToggle){
                    case 0:
                        mrecursionChanger.setText("Recurs Annually");
                        break;
                    case 1:
                        mrecursionChanger.setText("Recurs Daily");
                        break;
                    case 2:
                        mrecursionChanger.setText("Recurs Weekly");
                        break;
                    case 3:
                        mrecursionChanger.setText("Recurs Bi-Weekly");
                        break;
                    case 4:
                        mrecursionChanger.setText("Recurs Monthly");
                        break;
                    case 5:
                        mrecursionChanger.setText("Recurs Quarterly");
                        break;
                }
                timerToggle = 2;
                mrecursionChanger.setVisibility(View.VISIBLE);
                mdueToggle.setText("Recurring Task: on");
                taskTimeMessage.setText("Recursion Date: ");
            }

        }else{ // list has no timer.
            attachCalendar = false;
            dataholder.setVisibility(View.INVISIBLE);
            mdueToggle.setText("Due Toggle: off");
            defaultCalendar = Calendar.getInstance();
            year = defaultCalendar.get(defaultCalendar.YEAR) ;
            month = defaultCalendar.get(defaultCalendar.MONTH);
            day = defaultCalendar.get(defaultCalendar.DAY_OF_MONTH);
            hour = defaultCalendar.get(defaultCalendar.HOUR_OF_DAY);
            minute = defaultCalendar.get(defaultCalendar.MINUTE);
            dayViewer.setText(new StringBuilder().append(this.month +1).append("/").append(this.day).append("/").append(this.year).append(" "));
            TimeBuilder();
            mdueToggle.setText("Timer: off");
            timerToggle = 0;
            mrecursionChanger.setVisibility(View.INVISIBLE);
        }


        mdueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // date picker
                DatePickerDialog datePickerDialog = null;
                if(!darkMode)
                {datePickerDialog = new DatePickerDialog(getActivity(), Edit_Task_Dialog.this,  year, month, day);}
                else {datePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK, Edit_Task_Dialog.this, year, month, day);}
                datePickerDialog.show();
            }
        });

        mdueTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //time picker
                TimePickerDialog timePickerDialog = null;
                if(!darkMode){
                    timePickerDialog = new TimePickerDialog(getActivity(), Edit_Task_Dialog.this, hour, minute, true);}
                else{timePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK, Edit_Task_Dialog.this, hour, minute, true);}
                timePickerDialog.show();
            }
        });

        mdueToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // timer toggle button
                if(timerToggle == 0){ // no timer goes to due timer
                    attachCalendar = true;
                    timerToggle++;
                    dataholder.setVisibility(View.VISIBLE);
                    mrecursionChanger.setVisibility(View.INVISIBLE);
                    taskTimeMessage.setText("Due Date: ");
                    mdueToggle.setText("Due Toggle: on");
                }
                else if (timerToggle == 1){ // due timer goes to recursion timer
                    timerToggle++;
                    dataholder.setVisibility(View.VISIBLE);
                    mrecursionChanger.setVisibility(View.VISIBLE);
                    taskTimeMessage.setText("Recursion Date: ");
                    mdueToggle.setText("Recurring Task: on");

                }
                else{ // recursion timer goes to no timer
                    attachCalendar = false;
                    timerToggle = 0;
                    mrecursionChanger.setVisibility(View.INVISIBLE);
                    dataholder.setVisibility(View.INVISIBLE);
                    mdueToggle.setText("Timer: off");
                }
            }
        });
        mrecursionChanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // toggles recursion mode if recurring time is active.
                switch(recursionToggle){
                    case 0: // this does mean that annual is label 0, daily is 1, etc.
                        recursionToggle++;
                        mrecursionChanger.setText("Recurs Daily");
                        break;
                    case 1:
                        recursionToggle++;
                        mrecursionChanger.setText("Recurs Weekly");
                        break;
                    case 2:
                        recursionToggle++;
                        mrecursionChanger.setText("Recurs Bi-Weekly");
                        break;
                    case 3:
                        recursionToggle++;
                        mrecursionChanger.setText("Recurs Monthly");
                        break;
                    case 4:
                        recursionToggle++;
                        mrecursionChanger.setText("Recurs Quarterly");
                        break;
                    case 5:
                        recursionToggle = 0;
                        mrecursionChanger.setText("Recurs Annually");
                        break;
                }
            }
        });
        String title = ("Editing " + taskNameEntry.getText().toString() + ":"); // editing <task name>:

        dialogBuilder.setView(view)
                .setTitle(title)
                .setPositiveButton("Save Task", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            if (!taskNameEntry.getText().toString().trim().isEmpty()) {
                                String taskName = taskNameEntry.getText().toString();
                                String taskDescription = taskDescriptionEntry.getText().toString();
                                if(!attachCalendar)
                                taskListener.attachUpdatedTaskSettings(taskName,  taskDescription, position, timerToggle); //puts position into the mix so we can edit the entry directly/
                                else{
                                    if(dueDate == null){
                                    dueDate = defaultCalendar;}
                                    dueDate.set(year, month, day, hour, minute);
                                    if(timerToggle == 1){
                                    taskListener.attachUpdatedTaskSettings(taskName, taskDescription, position, dueDate, timerToggle);}
                                    else if (timerToggle == 2){
                                        taskListener.attachUpdatedTaskSettings(taskName, taskDescription, position, dueDate, timerToggle, recursionToggle); }
                                }
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.day = dayOfMonth;
        dayViewer.setText(new StringBuilder().append(this.month + 1).append("/").append(this.day).append("/").append(this.year).append(" "));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
        TimeBuilder();
    }

    public interface editTaskListener{
        void attachUpdatedTaskSettings(String _taskName, String _taskDescription, int _position, int timerToggle);//sends the dialog information and position information to the List_Activity
        void attachUpdatedTaskSettings(String _taskName, String _taskDescription, int _position, Calendar dueDate, int timerToggle);
        void attachUpdatedTaskSettings(String _taskName, String _taskDescription, int _position, Calendar dueDate, int timerToggle, int recursionToggle);
    }

    private void TimeBuilder(){
        StringBuilder timeBuilder = new StringBuilder();
        if(this.hour < 10) timeBuilder.append("0");
        timeBuilder.append(this.hour)
                .append(":");
        if(this.minute < 10) timeBuilder.append("0");
        timeBuilder.append(this.minute);
        timeViewer.setText(timeBuilder);
    }

}

