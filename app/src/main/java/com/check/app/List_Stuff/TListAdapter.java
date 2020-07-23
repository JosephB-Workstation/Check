package com.check.app.List_Stuff;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp); //parameters here stop the recycler view from going crazy. and tell it what it's working with.

        TListAdapter.ListViewHolder listseer = new TListAdapter.ListViewHolder(layoutView); // assigns parameters above to the view holder

        return listseer;
    }


    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, final int position) {
                holder.vhListName.setText(listOfLists.get(position).getListName());
                holder.vhTags.setText(listOfLists.get(position).getTagString());
                holder.vhTime.setText(listOfLists.get(position).getTimeString());
                holder.vhCardButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) { // code for opening lists to the list activity.
                Boolean onlineUp = listOfLists.get(position).getNewerOnline();
                Intent intent = new Intent(v.getContext(), List_Activity.class);
                intent.putExtra("listName", listOfLists.get(position).getListName());
                intent.putExtra("mode", 0);
                intent.putExtra("listBackground", listOfLists.get(position).getListBackgroundId());
                intent.putExtra("listCategory", listOfLists.get(position).getListCategory());
                intent.putExtra("listID", listOfLists.get(position).getListID());
                intent.putExtra("lastUpdate", onlineUp);
                intent.putExtra("mediaURI", listOfLists.get(position).getMediaURI());
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

    public void filterList(ArrayList<ListObject> filteredList){ // code to filter the lists via searches and categories.
        listOfLists = filteredList;
        notifyDataSetChanged();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder{
        TextView vhListName, vhTags, vhTime;
        CardView vhCardButton;
        ListViewHolder(View view){
            super(view);
            vhListName = view.findViewById(R.id.listNameMark);
            vhTags = view.findViewById(R.id.listTagMark);
            vhTime = view.findViewById(R.id.listTimeMark);
            vhCardButton = view.findViewById(R.id.listCard);
        }
    }
}
