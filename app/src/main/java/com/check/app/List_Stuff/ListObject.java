package com.check.app.List_Stuff;

import java.util.HashMap;

public class ListObject {
    private HashMap<String, Object> listData;
    private String listName;
    private int listSize;
    private double listBackgroundId;

    public ListObject(HashMap<String, Object> _data){
        listData = _data;
        listName = (String) listData.get("name");
        listSize = (listData.size() - 1);
        listBackgroundId = (Double) listData.get("background");
    }

    public String getListName(){
        return listName;
    }
    public int getListSize(){
        return listSize;
    }
    public double getListBackgroundId() {return listBackgroundId;}

}


