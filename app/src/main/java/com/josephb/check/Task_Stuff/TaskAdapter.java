package com.josephb.check.Task_Stuff;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.josephb.check.R;

import java.util.ArrayList;
import java.util.Calendar;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskListViewHolder> {
    private ArrayList<TaskObject> taskList;
    private Context context;
    private FragmentManager fm; // needed to make dialogs
    public TaskAdapter( ArrayList<TaskObject> taskList, FragmentManager _fm){ //Constructor from List_Activity, now granted power to make dialogs.
        this.taskList = taskList; //updates tasks.
        this.fm = _fm;
    }
    @NonNull
    @Override
    public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_taskcard, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp); //parameters here stop the recycler view from going crazy. and tell it what it's working with.

        TaskListViewHolder taskseer = new TaskListViewHolder(layoutView); // assigns parameters above to the view holder

        return taskseer;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListViewHolder holder, final int position) {
        holder.vhTask.setText(taskList.get(position).getTaskName()); //sets the name-text for each task in the list
        holder.vhDesc.setText(taskList.get(position).displayDescription());
        int state = taskList.get(position).getTimerState();
        if(state == 0) holder.vhTime.setText("No timer set!"); // if there is no timer function, then there is no need to show a time that doesn't exist
        else if(state == 1) { // grabs the due date, and formats it into text
            Calendar taskTime = taskList.get(position).getCalendar();
            int year = taskTime.get(taskTime.YEAR) ;
            int month = taskTime.get(taskTime.MONTH);
            int day = taskTime.get(taskTime.DAY_OF_MONTH);
            int hour = taskTime.get(taskTime.HOUR_OF_DAY);
            int minute = taskTime.get(taskTime.MINUTE);
            StringBuilder dayView = new StringBuilder().append(month +1).append("/").append(day).append("/").append(year).append(" ");
            StringBuilder timeView = TimeBuilder(hour, minute);
            holder.vhTime.setText("Due on: " + dayView.toString() + "at " + timeView.toString());
            ;}
        else if (state == 2) {
            Calendar taskTime = taskList.get(position).getCalendar();
            int year = taskTime.get(taskTime.YEAR) ;
            int month = taskTime.get(taskTime.MONTH);
            int day = taskTime.get(taskTime.DAY_OF_MONTH);
            int hour = taskTime.get(taskTime.HOUR_OF_DAY);
            int minute = taskTime.get(taskTime.MINUTE);
            StringBuilder dayView = new StringBuilder().append(month +1).append("/").append(day).append("/").append(year).append(" ");
            StringBuilder timeView = TimeBuilder(hour, minute);

            int recurCode = taskList.get(position).getRecursionCode();
            String recursionString = null;
            switch(recurCode){
                case 0:
                    recursionString = "Annually";
                    break;
                case 1:
                    recursionString = "Daily";
                    break;
                case 2:
                    recursionString = "Weekly";
                    break;
                case 3:
                    recursionString = "Bi-Weekly";
                    break;
                case 4:
                    recursionString = "Monthly";
                    break;
                case 5:
                    recursionString = "Quarterly";
                    break;
                default:
                    recursionString = "NO RECURSION CODE ERROR WITHIN TASKADAPTER!";
                    break;
            }
            holder.vhTime.setText("Resets on: " + dayView.toString() + "at " + timeView.toString() + ". " + recursionString);

        }
        holder.vhCheckBoxState.setImageResource(taskList.get(position).getCheckboxStateSource()); // checks if each task is checked or not, displays image accordingly.
        holder.vhTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // button handler for the checkboxes, checks or unchecks stuff.
                taskList.get(position).setCheckBoxState();
                notifyDataSetChanged();
            }
        });
        holder.vhTaskButton.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){ // button appended to the names of each task, allowing for editing. The fun stuff
                String taskName = taskList.get(position).getTaskName(); //gets task name for package
                String taskDescription = taskList.get(position).getTaskDescription(); // gets task description for package
                int arraypos = position; // gets array position for package
                if(taskList.get(position).getTimerState() == 1){ // if there is a due date
                    Calendar dueDate = taskList.get(position).getCalendar(); //grab it's calendar
                    int timeCode = 1;
                    openDialogFragment(taskName, taskDescription, arraypos, dueDate); //send it to opendialogfragment
                }else if(taskList.get(position).getTimerState() == 2){
                    Calendar dueDate = taskList.get(position).getCalendar(); //grab it's calendar
                    int recursionCode = taskList.get(position).getRecursionCode();
                    int timeCode = 2;
                    openDialogFragment(taskName, taskDescription, arraypos, dueDate, recursionCode); //send it to opendialogfragment

                }
                else openDialogFragment(taskName, taskDescription, arraypos); // sends off to a new function
                return false;
            }
        });
    }

    private void openDialogFragment(String _taskName, String _taskDescription, int _arraypos, Calendar calendar){
        Bundle deliverTask = new Bundle(); //creates a new bundle, stores all the necessary info for editing, sends to Edit_Task_Dialog
        deliverTask.putString("taskName", _taskName);
        deliverTask.putString("taskDescription", _taskDescription);
        deliverTask.putInt("position", _arraypos);
        deliverTask.putInt("timecode", 1);
        deliverTask.putSerializable("calendar", calendar);
        Edit_Task_Dialog editTask = new Edit_Task_Dialog();
        editTask.setArguments(deliverTask);
        editTask.show(fm, "editTask");
    }

    private void openDialogFragment(String _taskName, String _taskDescription, int _arraypos){
        Bundle deliverTask = new Bundle(); //creates a new bundle, stores all the necessary info for editing, sends to Edit_Task_Dialog
        deliverTask.putString("taskName", _taskName);
        deliverTask.putString("taskDescription", _taskDescription);
        deliverTask.putInt("position", _arraypos);
        Edit_Task_Dialog editTask = new Edit_Task_Dialog();
        editTask.setArguments(deliverTask);
        editTask.show(fm, "editTask");
    }

    private void openDialogFragment(String _taskName, String _taskDescription, int _arraypos, Calendar calendar, int recursionCode){
        Bundle deliverTask = new Bundle(); //creates a new bundle, stores all the necessary info for editing, sends to Edit_Task_Dialog
        deliverTask.putString("taskName", _taskName);
        deliverTask.putString("taskDescription", _taskDescription);
        deliverTask.putInt("position", _arraypos);
        deliverTask.putInt("recursion", recursionCode);
        deliverTask.putInt("timecode", 2);
        deliverTask.putSerializable("calendar", calendar);
        Edit_Task_Dialog editTask = new Edit_Task_Dialog();
        editTask.setArguments(deliverTask);
        editTask.show(fm, "editTask");
    }

    @Override
    public int getItemCount() {
        if(taskList != null){ //grabs item count if it's not empty, or returns 0 if it is.
            return taskList.size();
        }
        else
        return 0;
    }

    public class TaskListViewHolder extends RecyclerView.ViewHolder{
        TextView vhTask, vhTime, vhDesc; //vh is for view holder.
        CardView vhTaskButton;
        Button vhEditButton;
        ImageView vhCheckBoxState;
        LinearLayout taskItem;
        TaskListViewHolder(View view){
            super(view);
              vhTask = view.findViewById(R.id.taskNameCard);
              vhTime = view.findViewById(R.id.taskTimerCard);
              vhTaskButton = view.findViewById(R.id.taskCard);
              vhDesc = view.findViewById(R.id.taskDescCard);
              vhCheckBoxState = view.findViewById(R.id.taskStatusCard);
//            taskItem = view.findViewById(R.id.layout);
//            vhEditButton = view.findViewById(R.id.editTask);
//            vhTask = view.findViewById(R.id.taskName);
//            vhTime = view.findViewById(R.id.taskTime);
//            vhCheckbox = view.findViewById(R.id.checkButton);
//            vhCheckBoxState = view.findViewById(R.id.checkImage);
//            taskItem = view.findViewById(R.id.layout);
//            vhEditButton = view.findViewById(R.id.editTask);
        }
    }

    private StringBuilder TimeBuilder(int hour, int minute){
        StringBuilder timeBuilder = new StringBuilder();
        if(hour < 10) timeBuilder.append("0");
        timeBuilder.append(hour)
                .append(":");
        if(minute < 10) timeBuilder.append("0");
        timeBuilder.append(minute);
        return timeBuilder;
    }
}
