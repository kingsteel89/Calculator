package com.shindev.rulecalculator.common;

import org.json.JSONException;
import org.json.JSONObject;

public class HistoryItem {
    public String id = "";
    public String avatarUrl = "";
    public String name = "";
    public String comment = "";
    public String payed = "";
    public String regDate = "";
    public String other = "";

    public void initialWithJson (JSONObject object) {
        try {
            id = object.getString("id");
            name = object.getString("name");
            avatarUrl = object.getString("imgurl");
            comment = object.getString("comment");
            payed = object.getString("payed");
            regDate = object.getString("regdate");
            other = object.getString("other");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
