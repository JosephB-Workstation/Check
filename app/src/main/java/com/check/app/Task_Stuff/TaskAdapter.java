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

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskListViewHolder> {
    private ArrayList<TaskObject> taskList;
    private Context context;
    private FragmentManager fm;
    public TaskAdapter( Context _context, ArrayList<TaskObject> taskList, FragmentManager _fm){
        this.taskList = taskList; //updates tasks.
        this.context = _context;
        this.fm = _fm;
    }
    @NonNull
    @Override
    public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        TaskListViewHolder taskseer = new TaskListViewHolder(layoutView); // assigns parameters above to the view holder

        return taskseer;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListViewHolder holder, final int position) {
        holder.vhTask.setText(taskList.get(position).getTaskName());
        holder.vhCheckBoxState.setImageResource(taskList.get(position).getCheckboxStateSource());
        holder.vhCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskList.get(position).setCheckBoxState();
                notifyDataSetChanged();
            }
        });
        holder.vhEditButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String taskName = taskList.get(position).getTaskName();
                String taskDescription = taskList.get(position).getTaskDescription();
                int arraypos = position;
                openDialogFragment(taskName, taskDescription, arraypos);
            }
        });
    }

    private void openDialogFragment(String _taskName, String _taskDescription, int _arraypos){
        Bundle deliverTask = new Bundle();
        deliverTask.putString("taskName", _taskName);
        deliverTask.putString("taskDescription", _taskDescription);
        deliverTask.putInt("position", _arraypos);
        Edit_Task_Dialog editTask = new Edit_Task_Dialog();
        editTask.setArguments(deliverTask);
        editTask.show(fm, "editTask");
    }

    @Override
    public int getItemCount() {
        if(taskList != null){
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
