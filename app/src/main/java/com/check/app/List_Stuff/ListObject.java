package com.check.app.List_Stuff;

import java.util.HashMap;

public class ListObject {
    HashMap<String, Object> listData;
    String listName;
    int listSize;

    public ListObject(HashMap<String, Object> _data){
        listData = _data;
        listName = (String) listData.get("name");
        listSize = (listData.size() - 1);
    }

    public String getListName(){
        return listName;
    }
    public int getListSize(){
        return listSize;
    }

}


