package com.check.app.List_Stuff;

import java.util.HashMap;

public class ListObject {
    private HashMap<String, Object> listData;
    private String listName, listCategory;
    private int listSize;
    private double listBackgroundId;
    private String listID;

    public ListObject(HashMap<String, Object> _data){
        listData = _data;
        listName = (String) listData.get("name");
        listSize = (listData.size() - 1);
        listBackgroundId = (Double) listData.get("background");
        if(listData.containsKey("category")) {
            listCategory = (String) listData.get("category");
        }else{
            listCategory = "None";
        }
        if (listData.containsKey("ID")){
        listID = (String) listData.get("ID");}
        else{
            listID = "none";
        }
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


}


