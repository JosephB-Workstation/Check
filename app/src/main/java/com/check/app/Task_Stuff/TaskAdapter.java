package com.check.app.Task_Stuff;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.check.app.R;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskListViewHolder> {
    private ArrayList<TaskObject> taskList;
    public TaskAdapter(ArrayList<TaskObject> taskList){
        this.taskList = taskList; //updates tasks.
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
        TextView vhTask;
        Button vhCheckbox;
        ImageView vhCheckBoxState;
        LinearLayout layout;
        TaskListViewHolder(View view){
            super(view);
            vhTask = view.findViewById(R.id.taskName);
            vhCheckbox = view.findViewById(R.id.checkButton);
            vhCheckBoxState = view.findViewById(R.id.checkImage);
            layout = view.findViewById(R.id.listLayout);
        }
    }
}
