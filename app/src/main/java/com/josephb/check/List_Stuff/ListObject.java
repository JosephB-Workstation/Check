package com.josephb.check.List_Stuff;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.HashMap;

public class ListObject {
    private HashMap<String, Object> listData;
    private String listName, listCategory, mediaURI;
    private int listSize;
    private double listBackgroundId;
    private String listID, listOwnerId;
    private Calendar lastEdit;
    private boolean newerOnline;

    ListObject(String name, String category, double listBackgroundId, String listID, Calendar lastEdit, String MediaURI){
        listName = name;
        listCategory = category;
        this.listBackgroundId = listBackgroundId;
        this.listID = listID;
        this.lastEdit = lastEdit;
        newerOnline = false;
        listOwnerId = FirebaseAuth.getInstance().getUid();
        this.mediaURI = MediaURI;
    }


    public String getListOwnerId() {return listOwnerId;}

    public String getListName(){
        return listName;
    }

    public int getListSize(){
        return listSize;
    }

    public double getListBackgroundId() {return listBackgroundId;}

    public String getListCategory(){return listCategory;}

    public String getListID(){if (listID != null) return listID; else return "none"; }

    public boolean getNewerOnline(){return newerOnline;}

    public Calendar getLastEdit() {return lastEdit;}

    public String getMediaURI(){return mediaURI;}

    public void isNewerOnline(){newerOnline = true; }

    public void setListName(String name){listName = name;}

    public void setListCategory(String category){listCategory = category;}

    public void setListBackgroundId(Double value){listBackgroundId = value;}


    //string funcs

    public String getTagString(){
        String tagString;
        if(listCategory != null){
            tagString = ("Tag: " + listCategory); }
        else{
            tagString = "No tag!";
        }
        return tagString;
    }

    public String getTimeString(){
        String timeString = null;
        if(lastEdit != null){

            int year = lastEdit.get(lastEdit.YEAR) ;
            int month = lastEdit.get(lastEdit.MONTH);
            int day = lastEdit.get(lastEdit.DAY_OF_MONTH);
            int hour = lastEdit.get(lastEdit.HOUR_OF_DAY);
            int minute = lastEdit.get(lastEdit.MINUTE);
            StringBuilder dayView = new StringBuilder().append(month +1).append("/").append(day).append("/").append(year).append(" ");
            StringBuilder timeView = TimeBuilder(hour, minute);
            timeString = dayView.toString() + timeView.toString();
        } else timeString = "Never opened!";
        return timeString;
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


