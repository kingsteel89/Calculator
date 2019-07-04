package com.shindev.rulecalculator.common;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo {
    public WXUserInfo wxUserInfo;
    public String id;
    public String classname;
    public int cnt_func, cnt_restfunc, payed;
    public String regDate, other;

    public void initialWithJson (JSONObject object) {
        try {
            id = object.getString("id");
            classname = object.getString("classname");
            cnt_func = object.getInt("function");
            cnt_restfunc = object.getInt("restfunc");
            payed = object.getInt("payed");
            regDate = object.getString("regdate");
            other = object.getString("other");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
