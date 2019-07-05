package com.shindev.rulecalculator.common;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.shindev.rulecalculator.R;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class Global {
    static public Context gContext;

    static public boolean testMode = false;
    static public boolean gFlgNewLogin = false;

    static public int gFrgIndex = 5;
    static public int gFuncParams = 0;
    static public int gSelIndex = -1;

    static public String gPayed = "";
    static public String gReportName = "";
    static public String gReportDescription = "";
    static public String gReportContent = "";
    static public String gAlgorithm = "";

    static public CreateParaItem gSelParaItem = new CreateParaItem();

    static public ProjectItem gSelProject = new ProjectItem();

    static public UserInfo gUserInfo;

    static public ArrayList<FunctionItem> gSelFuncItems = new ArrayList<>();
    static public ArrayList<FunctionItem> gShowFuncItems = new ArrayList<>();
    static public ArrayList<CreateParaItem> gCreateParams = new ArrayList<>();
    static public ArrayList<String[]> gParamValues = new ArrayList<>();
    static public ArrayList<ParamInfo> gSelParams = new ArrayList<>();

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showOtherActivity (Activity activity, Class<?> cls, int direction) {
        Intent myIntent = new Intent(activity, cls);
        ActivityOptions options;
        switch (direction) {
            case 0:
                options = ActivityOptions.makeCustomAnimation(activity, R.anim.slide_in_right, R.anim.slide_out_left);
                activity.startActivity(myIntent, options.toBundle());
                break;
            case 1:
                options = ActivityOptions.makeCustomAnimation(activity, R.anim.slide_in_left, R.anim.slide_out_right);
                activity.startActivity(myIntent, options.toBundle());
                break;
            default:
                activity.startActivity(myIntent);
                break;
        }
        activity.finish();
    }

    public static void writeToFile(String data, String filename, Context context) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), context.getString(R.string.app_name));
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return;
            }
        }
        if(!file.exists()){
            file.mkdir();
        }

        try{
            File gpxfile = new File(file, filename + ".txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            writer.flush();
            writer.close();

            Toast.makeText(context, gpxfile.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isCheckSpelling (String str) {
        if (str.length() == 0) {
            return false;
        }
        String str_first_able = "abcdefghijklmnopqrstuvwxyz";
        String str_first = Character.toString(str.charAt(0));
        if (!str_first_able.contains(str_first)) {
            return false;
        }
        String str_able = "abcdefghijklmnopqrstuvwxyz_1234567890";
        for (int i = 1; i < str.length(); i++) {
            String letter = Character.toString(str.charAt(i));
            if (!str_able.contains(letter)) {
                return false;
            }
        }
        return true;
    }
}
