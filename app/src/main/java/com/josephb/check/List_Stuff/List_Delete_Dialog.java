package com.josephb.check.List_Stuff;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import com.josephb.check.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class List_Delete_Dialog  extends DialogFragment {
    private String name;
    private ListDeleteListener listDeleteListener;
    private Boolean darkMode;

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){

        //Dark mode handling
        //state init
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            darkMode = true;
        } else darkMode = false;

        //builder of the builder
        MaterialAlertDialogBuilder dialogBuilder = null;
        if(darkMode){
            dialogBuilder = new MaterialAlertDialogBuilder(getActivity(), R.style.DarkDialogCustom);}
        else {dialogBuilder = new MaterialAlertDialogBuilder(getActivity(), R.style.LightDialogCustom);}

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete_list, null);

        if(getArguments().containsKey("name")){
            name = "Deleting: " + getArguments().getString("name");
        }else name = "??name??";

        dialogBuilder.setView(view)
                .setTitle(name)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Yes, I'm sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listDeleteListener.deleteList();
                    }
                });
        return dialogBuilder.create();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listDeleteListener = (List_Delete_Dialog.ListDeleteListener)context;//instantiate listener
    }
    public interface ListDeleteListener{
        void deleteList();
    }
}
