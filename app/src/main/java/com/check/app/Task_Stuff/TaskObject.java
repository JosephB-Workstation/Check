package com.check.app.Task_Stuff;

import android.widget.Button;

import com.check.app.R;

public class TaskObject {
    private String taskName;
    private String taskDescription;
    private int checkboxState;
    private int checkboxStateSource;
    Button checkbox;
    public TaskObject(String taskName, String taskDescription){
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.checkboxState = 0;
        this.checkboxStateSource = R.drawable.notchecked;
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
        if(checkboxState == 0){
            checkboxState = 1;
            checkboxStateSource = R.drawable.ischecked;
        }
        else if(checkboxState == 1){
            checkboxState = 0;
            checkboxStateSource = R.drawable.notchecked;
        }
    }
    public void setTaskName(String newName){
        taskName = newName;
    }
    public void setTaskDescription(String newDescription){
        taskDescription = newDescription;
    }
}
