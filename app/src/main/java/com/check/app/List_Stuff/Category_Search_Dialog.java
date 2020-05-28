package com.check.app.List_Stuff;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.check.app.Create_List_Dialog;
import com.check.app.R;

public class Category_Search_Dialog extends DialogFragment {
    private Spinner categorySpinner;
    private SearchCategoryListener searchCategoryListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity()); //custom layout
        LayoutInflater inflater = requireActivity().getLayoutInflater(); // inflater for custom layout
        View view = inflater.inflate(R.layout.dialog_category_sort, null); // view for the custom inflater, and setting the layout file.
        dialogBuilder.setView(view)
                .setTitle("Select a category to filter in")
                .setPositiveButton("Set filter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        categorySpinner = view.findViewById(R.id.categorySpinner);
        return dialogBuilder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        searchCategoryListener = (Category_Search_Dialog.SearchCategoryListener)context; // instantiates listener
    }
    public interface SearchCategoryListener{
        void searchCategory(String _listName); // interface to attach to the main page that uses this.
    }
}
