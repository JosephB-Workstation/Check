package com.check.app.List_Stuff;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.check.app.R;
import com.check.app.Task_Stuff.TaskAdapter;
import com.check.app.Task_Stuff.TaskObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TListAdapter extends RecyclerView.Adapter<TListAdapter.ListViewHolder> {
    private ArrayList<ListObject> listOfLists;
    public TListAdapter(ArrayList<ListObject> listOfLists, FragmentManager supportFragmentManager) {
        this.listOfLists = listOfLists;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp); //parameters here stop the recycler view from going crazy. and tell it what it's working with.

        TListAdapter.ListViewHolder listseer = new TListAdapter.ListViewHolder(layoutView); // assigns parameters above to the view holder

        return listseer;
    }


    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, final int position) {
        holder.vhEntryButton.setText(listOfLists.get(position).getListName());
        holder.vhEntryButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), List_Activity.class);
                intent.putExtra("listName", listOfLists.get(position).getListName());
                intent.putExtra("mode", 0);
                intent.putExtra("size", listOfLists.get(position).getListSize());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(listOfLists != null){
            return listOfLists.size();
        }
        else return 0;
    }

    public class ListViewHolder extends RecyclerView.ViewHolder{
        TextView vhListName;
        Button vhEntryButton;
        ListViewHolder(View view){
            super(view);
            vhListName = view.findViewById(R.id.listName);
            vhEntryButton = view.findViewById(R.id.listButton);
        }
    }
}