package com.check.app.Task_Stuff;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.check.app.R;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TaskObject implements Serializable {
    private String taskName;
    private String taskDescription;
    private int checkboxState;
    private int checkboxStateSource;
    private int timerState;
    private int recursionCode;
    private int year, month, day, hour, minute;
    private Calendar dueDate;
    transient private  Timer dueTimer;
    private int taskID;
    private notificationSender notificationListener;


    public TaskObject(String taskName, String taskDescription){ // self explanatory constructor when there is no timers
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.checkboxState = 0;
        this.checkboxStateSource = R.drawable.notchecked;
        this.timerState = 0;
        this.notificationListener = null;

        Random rand = new Random();
        taskID = rand.nextInt();
    }


    public TaskObject(String taskName, String taskDescription, Calendar dueDate, int timerState){ // self explanatory constructor when there is a due date
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.checkboxState = 0;
        this.checkboxStateSource = R.drawable.notchecked;
        this.dueDate = dueDate;
        this.timerState = timerState;
        this.notificationListener = null;

        Random rand = new Random();
        taskID = rand.nextInt();

        year = dueDate.get(dueDate.YEAR) ;
        month = dueDate.get(dueDate.MONTH);
        day = dueDate.get(dueDate.DAY_OF_MONTH);
        hour = dueDate.get(dueDate.HOUR_OF_DAY);
        minute = dueDate.get(dueDate.MINUTE);



        dueTimer = new Timer();
        TimerTask dueExecutor = new TimerTask() {
            @Override
            public void run() {
                setLateCheckBoxState();
            }
        };
        dueTimer.schedule(dueExecutor, dueDate.getTime());
    }


    public TaskObject(String taskName, String taskDescription, Calendar dueDate, int timerState, int recursionTimerToggle){ // self explanatory constructor when there is a recursion point.
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.checkboxState = 0;
        this.checkboxStateSource = R.drawable.notchecked;
        this.dueDate = dueDate;
        this.timerState = timerState;
        this.recursionCode = recursionTimerToggle;
        this.notificationListener = null;

        Random rand = new Random();
        taskID = rand.nextInt();

        year = dueDate.get(dueDate.YEAR) ;
        month = dueDate.get(dueDate.MONTH);
        day = dueDate.get(dueDate.DAY_OF_MONTH);
        hour = dueDate.get(dueDate.HOUR_OF_DAY);
        minute = dueDate.get(dueDate.MINUTE);

        dueTimer = new Timer();
        TimerTask dueExecutor = new TimerTask() {
            @Override
            public void run() {
                setRecursionCheckBoxState();
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

    public int getRecursionCode(){
        return recursionCode;
    }

    public int getTimerState(){
        if (timerState == 1){
            return 1;
        }
        else if(timerState == 2){return 2;}
        else return 0;
    }

    public Calendar getCalendar(){
        return dueDate;
    }

    public int getCheckboxStateSource(){return checkboxStateSource;}

    public int getTaskID(){return taskID;}

    public void setCheckBoxState(){ // on - click setting of checkboxes
        if(checkboxState == 0 || checkboxState == 2){
            checkboxState = 1;
            if(!(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)){
                checkboxStateSource = R.drawable.ischecked;
            } else checkboxStateSource = R.drawable.dischecked;


        }
        else if(checkboxState == 1){
            checkboxState = 0;
            if(!(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)){
                checkboxStateSource = R.drawable.notchecked;
            } else checkboxStateSource = R.drawable.dnotchecked;

        }
    }

    public void setLateCheckBoxState(){ //Due date timer command
        if(checkboxState == 0){ // if check box is empty, the task is late. Give it a red x
            checkboxState = 2;
            if(!(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)){
                checkboxStateSource = R.drawable.islate;
            } else checkboxStateSource = R.drawable.dislate;

        }
        dueTimer = null; //deletes timer after use
        timerState = 0;
    }

    public void updateCheckBoxSource() { // for when you swap between light or dark mode.
        if (checkboxState == 0) {
            if (!(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)) {
                checkboxStateSource = R.drawable.ischecked;
            } else checkboxStateSource = R.drawable.dischecked;
        } else if (checkboxState == 1) {
            if (!(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)) {
                checkboxStateSource = R.drawable.notchecked;
            } else checkboxStateSource = R.drawable.dnotchecked;
        } else if (checkboxState == 2) {
            if (!(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)) {
                checkboxStateSource = R.drawable.islate;
            } else checkboxStateSource = R.drawable.dislate;
        }
    }

    public void setRecursionCheckBoxState(){ //Recurring timer command
        if(checkboxState == 1){ // if item is checked off, we are unchecking it
            checkboxState = 0;
            checkboxStateSource = R.drawable.notchecked;
        }
        year = dueDate.get(dueDate.YEAR) ; //ensures that these fields have the current recursion date's time, as it wouldn't if recursion was edited in. Without this, system would crash.
        month = dueDate.get(dueDate.MONTH);
        day = dueDate.get(dueDate.DAY_OF_MONTH);
        hour = dueDate.get(dueDate.HOUR_OF_DAY);
        minute = dueDate.get(dueDate.MINUTE);

        switch (recursionCode){
            case 0: // annually
                year++;
                break;
            case 1: //daily
                day++;
                break;
            case 2: //weekly
                day += 7;
                break;
            case 3: //bi-weekly
                day += 14;
                break;
            case 4: // monthly
                month++;
                break;
            case 5: //quarterly
                month += 3;
                break;
        }

        dueDate.set(year, month, day, hour, minute); //sets new recursion date

        dueTimer = new Timer();
        TimerTask dueExecutor = new TimerTask() {
            @Override
            public void run() {
                setRecursionCheckBoxState();
            }
        };

          dueTimer.schedule(dueExecutor, dueDate.getTime());

        year = dueDate.get(dueDate.YEAR) ; // updates list with current information. A little redundant,
        month = dueDate.get(dueDate.MONTH);
        day = dueDate.get(dueDate.DAY_OF_MONTH);
        hour = dueDate.get(dueDate.HOUR_OF_DAY);
        minute = dueDate.get(dueDate.MINUTE);
    }

    public void setTaskName(String newName){
        taskName = newName;
    }

    public void setTaskDescription(String newDescription){
        taskDescription = newDescription;
    }

    public void setRecursionCode(int newcode){
        recursionCode = newcode;
    }

    public void updateDueTimer(Calendar _calendar, int _timerState){ // a long chain of conversions for timer based edits
                dueDate = _calendar; // timerstate 0 = no timer, 1 = due date, 2 = recurring.
                dueDate.set(_calendar.get(_calendar.YEAR), _calendar.get(_calendar.MONTH), _calendar.get(_calendar.DAY_OF_MONTH), _calendar.get(_calendar.HOUR_OF_DAY), _calendar.get(_calendar.MINUTE));
        if(timerState == 0) {
            if (_timerState == 1) {
                dueTimer = new Timer();
                TimerTask dueExecutor = new TimerTask() {
                    @Override
                    public void run() { setLateCheckBoxState(); }};
                dueTimer.schedule(dueExecutor, dueDate.getTime());
                timerState = _timerState;

            } else if (_timerState == 2){
                dueTimer = new Timer();
                TimerTask dueExecutor = new TimerTask() {
                    @Override
                    public void run() { setRecursionCheckBoxState(); }};
                dueTimer.schedule(dueExecutor, dueDate.getTime());
                timerState = _timerState;
            }
        }
        else if(timerState == 1 || timerState == 2){
            if(_timerState == 0){
                dueTimer.cancel();
                dueTimer = null;
                dueDate = null;
                timerState = _timerState;
            }
            else if(_timerState == 1){
                dueTimer.cancel();
                dueTimer = new Timer();
                TimerTask dueExecutor = new TimerTask() {
                    @Override
                    public void run() {
                        setLateCheckBoxState();
                    }
                };
                dueTimer.schedule(dueExecutor, dueDate.getTime());
                timerState = _timerState;
            }
            else if(_timerState == 2){
                dueTimer.cancel();
                dueTimer = new Timer();
                TimerTask dueExecutor = new TimerTask() {
                    @Override
                    public void run() { setRecursionCheckBoxState(); }
                };
                dueTimer.schedule(dueExecutor, dueDate.getTime());
                timerState = _timerState;
            }
        }
        timerState = _timerState;
    }

    public void updateDueTimer(){ // due timer canceler
        timerState = 0;
        recursionCode = 0;
        dueTimer.cancel();
        dueTimer = null;
        dueDate = null;
    }

    public void importTimerHandler(){ // cycles through each task with a due date or recursion date, ensures the information on them is up to date.
        if(timerState == 0){}
        else if (timerState == 1){
        if(dueDate != null){
            if(dueDate.compareTo(Calendar.getInstance()) == 1){
                if(notificationListener != null){
                    notificationListener.onNotificationReady(taskName, dueDate, taskID);
                }
            }
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
        else if(timerState == 2){
            if(dueDate != null){
                dueTimer = new Timer();
                TimerTask dueExecutor = new TimerTask() {
                    @Override
                    public void run() {
                        setRecursionCheckBoxState();
                    }
                };
                dueTimer.schedule(dueExecutor, dueDate.getTime());
            }
        }
        updateCheckBoxSource();
    }
    public boolean hasTimer(){
        if(timerState == 1) {
            if (dueDate != null && dueTimer == null) {
                dueDate = null;
                return true;
            } else return false;
        }
        else if (timerState == 2){
            return true;
        }
        return false;
    }

    public void setNotificationListener(notificationSender listener){
        this.notificationListener = listener;
    }

    public interface notificationSender{
         void onNotificationReady(String taskName, Calendar taskCalendar, int taskID);
    }

}
