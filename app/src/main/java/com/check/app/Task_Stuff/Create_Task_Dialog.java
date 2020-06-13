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
import androidx.fragment.app.DialogFragment;

import com.check.app.R;

import java.util.Calendar;

public class Create_Task_Dialog extends DialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private EditText taskNameEntry, taskDescriptionEntry;
    private CreateTaskListener taskListener;
    private int year, month, day, hour, minute, timerToggle, recursionToggle;
    private Button mdueDate, mdueTime, mdueToggle, mrecursionChanger;
    private TextView dayViewer, timeViewer, taskTimeMessage;
    private RelativeLayout dataholder;
    private Boolean attachCalendar;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_task, null);//bureaucracy of setting up a dialog

        //text fields
        taskNameEntry = view.findViewById(R.id.taskName);// instantiates the name and description edittext
        taskDescriptionEntry = view.findViewById(R.id.taskDescription);

        //due date Buttons
        mdueDate =  view.findViewById(R.id.dueDateButton);
        mdueTime = view.findViewById(R.id.dueTimeButton);
        mdueToggle = view.findViewById(R.id.dueToggleButton);
        mrecursionChanger = view.findViewById(R.id.recursionRateButton);

        //due date Texts
        dayViewer = view.findViewById(R.id.dayView);
        timeViewer = view.findViewById(R.id.timeView);
        taskTimeMessage = view.findViewById(R.id.taskTimeMessage);

        //toggle due
        attachCalendar = false;
        dataholder = view.findViewById(R.id.toggleDueView);
        dataholder.setVisibility(View.INVISIBLE);
        mrecursionChanger.setVisibility(View.INVISIBLE);
        mrecursionChanger.setText("Recurs Daily");
        timerToggle = 0;
        recursionToggle = 1;

        //set to current date
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        dayViewer.setText(new StringBuilder().append(this.month + 1).append("/").append(this.day).append("/").append(this.year).append(" "));

        //String builder for time
        TimeBuilder();

        mdueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //date picker
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), Create_Task_Dialog.this,  year, month, day);
                datePickerDialog.show();
            }
        });

        mdueTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // time picker
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), Create_Task_Dialog.this, hour, minute, true);
                timePickerDialog.show();
            }
        });

        mdueToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// timer toggle
                if(timerToggle == 0){
                    attachCalendar = true;
                    timerToggle++;
                    dataholder.setVisibility(View.VISIBLE);
                    mrecursionChanger.setVisibility(View.INVISIBLE);
                    taskTimeMessage.setText("Due Date: ");
                    mdueToggle.setText("Due Toggle: on");
                }
                else if (timerToggle == 1){
                    timerToggle++;
                    dataholder.setVisibility(View.VISIBLE);
                    mrecursionChanger.setVisibility(View.VISIBLE);
                    taskTimeMessage.setText("Recursion Date: ");
                    mdueToggle.setText("Recurring Task: on");

                }
                else{
                    attachCalendar = false;
                    timerToggle = 0;
                    mrecursionChanger.setVisibility(View.INVISIBLE);
                    dataholder.setVisibility(View.INVISIBLE);
                    mdueToggle.setText("Timer Functions: off");
                }
            }
        });
        mrecursionChanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // recursion period toggle.
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
        dialogBuilder.setView(view)
                .setTitle("Create a new task")
                .setPositiveButton("Create Task", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            if (!taskNameEntry.getText().toString().trim().isEmpty()) {
                                String taskName = taskNameEntry.getText().toString();// copies name and description into the attach
                                String taskDescription = taskDescriptionEntry.getText().toString();
                                if(!attachCalendar){
                                taskListener.attachTaskSettings(taskName, taskDescription);}
                                else if(attachCalendar){
                                    Calendar date = Calendar.getInstance();
                                    date.set(year, month, day, hour, minute);
                                    if(timerToggle == 1){
                                    taskListener.attachTaskSettings(taskName, taskDescription, date, timerToggle);}
                                    else if(timerToggle == 2){
                                        taskListener.attachTaskSettings(taskName, taskDescription, date, timerToggle, recursionToggle);
                                    }
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
        taskListener = (CreateTaskListener)context;//instantiate listener
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

    public interface CreateTaskListener{
        void attachTaskSettings(String _taskName, String _taskDescription ); //interface function to deliver to list activity
        void attachTaskSettings(String _taskName, String _taskDescription, Calendar date, int taskTimerToggle); //interface function to deliver to list activity if a due date exists
        void attachTaskSettings(String _taskName, String _taskDescription, Calendar date, int taskTimerToggle, int recursionTimerToggle); // interface function to deliver recursion timings if recursion is applied.

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

