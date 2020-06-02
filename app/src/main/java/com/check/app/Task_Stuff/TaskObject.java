package com.check.app.Task_Stuff;

import android.widget.Button;

import com.check.app.R;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TaskObject implements Serializable {
    private String taskName;
    private String taskDescription;
    private int checkboxState;
    private int checkboxStateSource;
    private Calendar dueDate;
    transient private  Timer dueTimer;

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
        dueTimer = new Timer();
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
    public boolean getTimerState(){
        if (dueTimer != null){
            return true;
        } else return false;
    }
    public Calendar getCalendar(){
        return dueDate;
    }
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
        dueTimer = null;
        //dueDate = null;
    }
    public void setTaskName(String newName){
        taskName = newName;
    }
    public void setTaskDescription(String newDescription){
        taskDescription = newDescription;
    }


    public void updateTimer(Calendar _calendar){
                dueDate = _calendar;
                dueDate.set(_calendar.get(_calendar.YEAR), _calendar.get(_calendar.MONTH), _calendar.get(_calendar.DAY_OF_MONTH), _calendar.get(_calendar.HOUR_OF_DAY), _calendar.get(_calendar.MINUTE));
                if (getTimerState()) {
                    dueTimer.cancel();
                    dueTimer = new Timer();
                    TimerTask dueExecutor = new TimerTask() {
                        @Override
                        public void run() {
                            setLateCheckBoxState();
                        }
                    };
                    dueTimer.schedule(dueExecutor, dueDate.getTime());

                }
                else{
                    dueTimer = new Timer();
                    TimerTask dueExecutor = new TimerTask() {
                        @Override
                        public void run() {
                            setLateCheckBoxState();
                        }
                    };
                    dueTimer.schedule(dueExecutor, dueDate.getTime());
                }
    }
    public void updateTimer(){
        dueTimer.cancel();
        dueTimer = null;
        dueDate = null;
    }

    public void importTimeCheck(){
        if(dueDate != null){
            dueTimer = new Timer();
            TimerTask dueExecutor = new TimerTask() {
                @Override
                public void run() {
                    setLateCheckBoxState();
                }
            };
            dueTimer.schedule(dueExecutor, dueDate.getTime());
        }
    }
    public boolean hasTimer(){
        if(dueDate != null && dueTimer == null) {
            dueDate = null;
            return true;}
        else return false;
    }
}
