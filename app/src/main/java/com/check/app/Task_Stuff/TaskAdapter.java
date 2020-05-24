package com.check.app.Task_Stuff;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.check.app.List_Stuff.List_Activity;
import com.check.app.R;

import java.util.ArrayList;
import java.util.Calendar;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskListViewHolder> {
    private ArrayList<TaskObject> taskList;
    private Context context;
    private FragmentManager fm; // needed to make dialogs
    public TaskAdapter( Context _context, ArrayList<TaskObject> taskList, FragmentManager _fm){ //Constructor from List_Activity, now granted power to make dialogs.
        this.taskList = taskList; //updates tasks.
        this.context = _context;
        this.fm = _fm;
    }
    @NonNull
    @Override
    public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp); //parameters here stop the recycler view from going crazy. and tell it what it's working with.

        TaskListViewHolder taskseer = new TaskListViewHolder(layoutView); // assigns parameters above to the view holder

        return taskseer;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListViewHolder holder, final int position) {
        holder.vhTask.setText(taskList.get(position).getTaskName()); //sets the name-text for each task in the list
        holder.vhCheckBoxState.setImageResource(taskList.get(position).getCheckboxStateSource()); // checks if each task is checked or not, displays image accordingly.
        holder.vhCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // button handler for the checkboxes, checks or unchecks stuff.
                taskList.get(position).setCheckBoxState();
                notifyDataSetChanged();
            }
        });
        holder.vhEditButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){ // button appended to the names of each task, allowing for editing. The fun stuff
                String taskName = taskList.get(position).getTaskName(); //gets task name for package
                String taskDescription = taskList.get(position).getTaskDescription(); // gets task description for package
                int arraypos = position; // gets array position for package
                if(taskList.get(position).getTimerState()){ // if there is a due date
                    Calendar dueDate = taskList.get(position).getCalendar(); //grab it's calendar
                    openDialogFragment(taskName, taskDescription, arraypos, dueDate); //send it to opendialogfragment
                }else
                openDialogFragment(taskName, taskDescription, arraypos); // sends off to a new function
            }
        });
    }

    private void openDialogFragment(String _taskName, String _taskDescription, int _arraypos, Calendar calendar){
        Bundle deliverTask = new Bundle(); //creates a new bundle, stores all the necessary info for editing, sends to Edit_Task_Dialog
        deliverTask.putString("taskName", _taskName);
        deliverTask.putString("taskDescription", _taskDescription);
        deliverTask.putInt("position", _arraypos);
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

    @Override
    public int getItemCount() {
        if(taskList != null){ //grabs item count if it's not empty, or returns 0 if it is.
            return taskList.size();
        }
        else
        return 0;
    }

    public class TaskListViewHolder extends RecyclerView.ViewHolder{
        TextView vhTask; //vh is for view holder.
        Button vhCheckbox;
        Button vhEditButton;
        ImageView vhCheckBoxState;
        LinearLayout taskItem;
        TaskListViewHolder(View view){
            super(view);
            vhTask = view.findViewById(R.id.taskName);
            vhCheckbox = view.findViewById(R.id.checkButton);
            vhCheckBoxState = view.findViewById(R.id.checkImage);
            taskItem = view.findViewById(R.id.layout);
            vhEditButton = view.findViewById(R.id.editTask);
        }
    }
}
