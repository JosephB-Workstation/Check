package com.check.app.Task_Stuff;

import android.widget.Button;

import com.check.app.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TaskObject {
    private String taskName;
    private String taskDescription;
    private int checkboxState;
    private int checkboxStateSource;
    private Calendar dueDate;
    public TaskObject(String taskName, String taskDescription){
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.checkboxState = 0;
        this.checkboxStateSource = R.drawable.notchecked;
    }
    public TaskObject(String taskName, String taskDescription, Calendar dueDate){
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.checkboxState = 0;
        this.checkboxStateSource = R.drawable.notchecked;
        this.dueDate = dueDate;
        Timer dueTimer = new Timer();
        TimerTask dueExecutor = new TimerTask() {
            @Override
            public void run() {
                setLateCheckBoxState();
            }
        };
        dueTimer.schedule(dueExecutor, dueDate.getTime());
    }

    public String getTaskName() {
        return taskName;
    }
    public String getTaskDescription(){
        return taskDescription;
    }
    public int getcheckboxState(){return checkboxState;}
    public int getCheckboxStateSource(){return checkboxStateSource;}

    public void setCheckBoxState(){
        if(checkboxState == 0 || checkboxState == 2){
            checkboxState = 1;
            checkboxStateSource = R.drawable.ischecked;
        }
        else if(checkboxState == 1){
            checkboxState = 0;
            checkboxStateSource = R.drawable.notchecked;
        }
    }

    public void setLateCheckBoxState(){
        if(checkboxState == 0){
            checkboxState = 2;
            checkboxStateSource = R.drawable.islate;
            
        }
    }
    public void setTaskName(String newName){
        taskName = newName;
    }
    public void setTaskDescription(String newDescription){
        taskDescription = newDescription;
    }
}
