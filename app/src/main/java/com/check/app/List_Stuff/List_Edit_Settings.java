package com.check.app.List_Stuff;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import com.check.app.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class List_Edit_Settings extends DialogFragment  implements List_Color_Settings.ColorSettingsListListener{
    private double colorId;
    private String category, name, listID, mediaID, mediaURI;
    private Button backgroundPicker, backgroundIPicker;
    private EditText categorySelector, namePicker;
    private ListEditListener listEditListener;
    private Boolean darkMode, imageBackground;
    private String imageURI;
    private int PICK_IMAGE_INTENT = 1;

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Dark mode button handling
        //state init
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            darkMode = true;
        } else darkMode = false;

        imageBackground = false;

        //builder of the builder
        MaterialAlertDialogBuilder dialogBuilder = null;
        if(darkMode){
            dialogBuilder = new MaterialAlertDialogBuilder(getActivity(), R.style.DarkDialogCustom);}
        else {dialogBuilder = new MaterialAlertDialogBuilder(getActivity(), R.style.LightDialogCustom);}

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_list_settings, null);//bureaucracy of setting up a dialog
        backgroundPicker =  view.findViewById(R.id.backgroundEditor);
        backgroundIPicker = view.findViewById(R.id.backgroundImage);
        categorySelector = view.findViewById(R.id.categoryEditor);
        namePicker = view.findViewById(R.id.nameEditor);




        if(getArguments().containsKey("colorId")){//grabs background color data
            colorId = getArguments().getDouble("colorId");
        } else {colorId = 0;}

        if(getArguments().containsKey("category")){// grabs category
            category = getArguments().getString("category");
        }else{category = "None";}

        if(getArguments().containsKey("name")){// grabs name
            name = getArguments().getString("name");
        }else{name = "?";}

        if(getArguments().containsKey("listId")){// grabs name
            listID = getArguments().getString("listId");
        }else{listID = "?";}

        categorySelector.setText(category);
        namePicker.setText(name);
        backgroundPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List_Color_Settings color_settings = new List_Color_Settings();
                color_settings.show(getActivity().getSupportFragmentManager(), "Change color"); //change color button
            }
        });

        backgroundIPicker.setOnClickListener(new View.OnClickListener() {//image button
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture(s)"), PICK_IMAGE_INTENT);
            }
        });

        dialogBuilder.setView(view)
                .setTitle("Edit List Settings")
                .setPositiveButton("Save settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!categorySelector.getText().toString().trim().isEmpty()){
                            category = categorySelector.getText().toString();
                        }else{category = "None";}
                        name = namePicker.getText().toString();

                        if(mediaID == null){
                            mediaID = "None";
                        }
                        if(mediaURI == null){
                            mediaURI = "None";
                        }

                        listEditListener.attachListSettings(category, name, mediaID, mediaURI, colorId, imageBackground);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return dialogBuilder.create();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listEditListener = (List_Edit_Settings.ListEditListener)context;//instantiate listener
    }

    public interface ListEditListener{
        void attachListSettings(String category, String name, String mediaID, String mediaURI, double colorId, boolean imagebackgroundupdate);
    }

    @Override
    public void attachColorSettings(int colorId) {
        this.colorId = (double)colorId;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_INTENT){
            if(resultCode== Activity.RESULT_OK){
                if(data != null){
                    Toast toast = Toast.makeText(getActivity(), "Image uploading, please wait before confirming dialog!", Toast.LENGTH_LONG);
                    toast.show();
                    imageURI = data.getData().toString();
                    final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("list").child(listID);
                    UploadTask uploadTask = filepath.putFile(Uri.parse(imageURI));
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mediaURI = uri.toString();
                                    colorId = -1;
                                    imageBackground = true;
                                    Toast toast = Toast.makeText(getActivity(), "Image uploaded! Will update when you confirm the dialog box!", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                        }
                    });
                }
            }
        }
    }

}

