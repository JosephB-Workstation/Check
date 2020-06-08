package com.check.app.Task_Stuff;

import com.check.app.R;

import java.io.Serializable;
import java.util.Calendar;
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

    public TaskObject(String taskName, String taskDescription){
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.checkboxState = 0;
        this.checkboxStateSource = R.drawable.notchecked;
        this.timerState = 0;
    }


    public TaskObject(String taskName, String taskDescription, Calendar dueDate, int timerState){
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.checkboxState = 0;
        this.checkboxStateSource = R.drawable.notchecked;
        this.dueDate = dueDate;
        this.timerState = timerState;


        dueTimer = new Timer();
        TimerTask dueExecutor = new TimerTask() {
            @Override
            public void run() {
                setLateCheckBoxState();
            }
        };
        dueTimer.schedule(dueExecutor, dueDate.getTime());
    }


    public TaskObject(String taskName, String taskDescription, Calendar dueDate, int timerState, int recursionTimerToggle){
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.checkboxState = 0;
        this.checkboxStateSource = R.drawable.notchecked;
        this.dueDate = dueDate;
        this.timerState = timerState;
        this.recursionCode = recursionTimerToggle;

        dueTimer = new Timer();
        TimerTask dueExecutor = new TimerTask() {
            @Override
            public void run() {
                setRecursionCheckBoxState();
            }
        };
        dueTimer.schedule(dueExecutor, dueDate.getTime());

        year = dueDate.get(dueDate.YEAR) ;
        month = dueDate.get(dueDate.MONTH);
        day = dueDate.get(dueDate.DAY_OF_MONTH);
        hour = dueDate.get(dueDate.HOUR_OF_DAY);
        minute = dueDate.get(dueDate.MINUTE);

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

    public int getcheckboxState(){return checkboxState;}
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

    public void setLateCheckBoxState(){ //Due date timer command
        if(checkboxState == 0){
            checkboxState = 2;
            checkboxStateSource = R.drawable.islate;
        }
        dueTimer = null;
        timerState = 0;
    }

    public void setRecursionCheckBoxState(){
        if(checkboxState == 1){
            checkboxState = 0;
            checkboxStateSource = R.drawable.notchecked;
        }
        year = dueDate.get(dueDate.YEAR) ;
        month = dueDate.get(dueDate.MONTH);
        day = dueDate.get(dueDate.DAY_OF_MONTH);
        hour = dueDate.get(dueDate.HOUR_OF_DAY);
        minute = dueDate.get(dueDate.MINUTE);

        switch (recursionCode){
            case 0: // this does mean that annual is label 0, daily is 1, etc.
                year++;
                break;
            case 1:
                minute++;//temporary testing value, is supposed to be day
                break;
            case 2:
                day += 7;
                break;
            case 3:
                day += 14;
                break;
            case 4:
                month++;
                break;
            case 5:
                month += 3;
                break;
        }

        dueDate.set(year, month, day, hour, minute);

        dueTimer = new Timer();
        TimerTask dueExecutor = new TimerTask() {
            @Override
            public void run() {
                setRecursionCheckBoxState();
            }
        };

          dueTimer.schedule(dueExecutor, dueDate.getTime());

        year = dueDate.get(dueDate.YEAR) ;
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


    public void updateDueTimer(Calendar _calendar, int _timerState){
                dueDate = _calendar;
                dueDate.set(_calendar.get(_calendar.YEAR), _calendar.get(_calendar.MONTH), _calendar.get(_calendar.DAY_OF_MONTH), _calendar.get(_calendar.HOUR_OF_DAY), _calendar.get(_calendar.MINUTE));
        if(timerState == 0) {
            if (_timerState == 1) {
                dueTimer = new Timer();
                TimerTask dueExecutor = new TimerTask() {
                    @Override
                    public void run() { setLateCheckBoxState(); }};
                dueTimer.schedule(dueExecutor, dueDate.getTime());

            } else if (_timerState == 2){
                dueTimer = new Timer();
                TimerTask dueExecutor = new TimerTask() {
                    @Override
                    public void run() { setRecursionCheckBoxState(); }};
                dueTimer.schedule(dueExecutor, dueDate.getTime());
            }
        }
        else if(timerState == 1 || timerState == 2){
            if(_timerState == 0){
                dueTimer.cancel();
                dueTimer = null;
                dueDate = null;
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
            }
            else if(_timerState == 2){
                dueTimer.cancel();
                dueTimer = new Timer();
                TimerTask dueExecutor = new TimerTask() {
                    @Override
                    public void run() { setRecursionCheckBoxState(); }
                };
                dueTimer.schedule(dueExecutor, dueDate.getTime());
            }
        }
        timerState = _timerState;
    }
    public void updateDueTimer(){
        timerState = 0;
        recursionCode = 0;
        dueTimer.cancel();
        dueTimer = null;
        dueDate = null;
    }

    public void importTimeCheck(){
        if(timerState == 0){}
        else if (timerState == 1){
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
}
