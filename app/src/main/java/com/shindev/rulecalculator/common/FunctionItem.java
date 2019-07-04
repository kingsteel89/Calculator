package com.shindev.rulecalculator.common;

import org.json.JSONException;
import org.json.JSONObject;

public class FunctionItem {
    public String id = "";
    public String title = "";
    public String name = "";
    public String regdate = "";
    public String content = "";
    public String other = "";
    public boolean isSelected = false;
    public int cnt_para = 1;

    public void initialWithJson (JSONObject object) {
        try {
            id = object.getString("id");
            title = object.getString("title");
            name = object.getString("name");
            content = object.getString("content");
            regdate = object.getString("regdate");
            other = object.getString("other");
            isSelected = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getCnt_para() {
        return cnt_para;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }
}
