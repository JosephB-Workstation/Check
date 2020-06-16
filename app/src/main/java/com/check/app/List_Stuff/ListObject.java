package com.check.app.List_Stuff;

import java.util.Calendar;
import java.util.HashMap;

public class ListObject {
    private HashMap<String, Object> listData;
    private String listName, listCategory;
    private int listSize;
    private double listBackgroundId;
    private String listID;
    private Calendar lastEdit;
    private boolean newerOnline;

    ListObject(String name, String category, double listBackgroundId, String listID, Calendar lastEdit){
        listName = name;
        listCategory = category;
        this.listBackgroundId = listBackgroundId;
        this.listID = listID;
        this.lastEdit = lastEdit;
        newerOnline = false;
    }


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

    public void isNewerOnline(){newerOnline = true; }


}


