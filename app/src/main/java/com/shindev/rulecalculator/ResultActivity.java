package com.shindev.rulecalculator;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shindev.rulecalculator.adapters.ResultAdapter;
import com.shindev.rulecalculator.common.AppConstant;
import com.shindev.rulecalculator.common.FunctionItem;
import com.shindev.rulecalculator.common.Global;
import com.shindev.rulecalculator.common.ParamInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    ListView lst_result;

    ArrayList<String[]> ary_results = new ArrayList<>();
    String[] list_results;
    ResultAdapter mAdapter;

    private ProgressDialog dialog;

    int cnt_func = 0;

    String str_data = "";

//    final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, APP_ID_WX, true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.txt_black, this.getTheme()));
        } else {
            getWindow().setStatusBarColor(getColor(R.color.txt_black));
        }

        cnt_func = Global.gSelFuncItems.size();
        dialog = ProgressDialog.show(ResultActivity.this, getString(R.string.progress_title), getString(R.string.progress_detail));
        getResultArray(0);

        initUIView();
    }

    private void getResultArray(final int index) {
        FunctionItem item = Global.gSelFuncItems.get(index);
        String str_url = AppConstant.SERVER_URL+ Global.gUserInfo.classname + "/" + item.title + "/";

        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < Global.gSelParams.size(); i++) {
            String key = Global.gSelParams.get(i).name;
            String value = Global.gParamValues.get(0)[i];
            for (int j = 1; j < Global.gParamValues.size(); j++) {
                value = value + "," + Global.gParamValues.get(j)[i];
            }
            params.put(key, value);
        }

        OkHttpUtils.get().url(str_url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        Toast.makeText(getApplicationContext(), R.string.alert_error_internet_detail, Toast.LENGTH_SHORT).show();
                        Global.showOtherActivity(ResultActivity.this, LoginActivity.class, 1);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            list_results = new String[Global.gParamValues.size()];
                            JSONObject obj = new JSONObject(response);
                            JSONArray result = obj.getJSONArray("result");
                            for (int i = 0; i < result.length(); i++) {
                                String str_result = result.getString(i);
                                list_results[i] = str_result;
                            }
                            ary_results.add(list_results);
                            int new_index = index + 1;
                            if (new_index == cnt_func) {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                                onShowResultEvent();
                            } else {
                                getResultArray(new_index);
                            }
                        } catch (JSONException e) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), R.string.alert_server_error_detail, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void onShowResultEvent() {
        lst_result = findViewById(R.id.lst_result);
        mAdapter = new ResultAdapter(this, Global.gParamValues, ary_results);
        lst_result.setAdapter(mAdapter);
    }

    private void initUIView() {
        TextView lbl_title = findViewById(R.id.lbl_result_title);
        lbl_title.setText(R.string.result_title_activity);
    }

    public void onClickBackIcon(View view) {
        Global.gFrgIndex = 5;
        Global.showOtherActivity(this, MainActivity.class, 1);
    }

    public void onClickSaveBtn(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.result_alert_title);
        alertDialog.setMessage(R.string.result_alert_detail);

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(10, 0, 10, 0);
        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        alertDialog.setView(input);

        alertDialog.setPositiveButton(R.string.formula_ok, (dialog, which) -> {
                    String str_value = input.getText().toString();
                    if (str_value.length() == 0) {
                        return;
                    }
                    if (ActivityCompat.checkSelfPermission(Global.gContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        ActivityCompat.requestPermissions(ResultActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                        return;
                    }
                    onSaveFileWithName(str_value);
                });

        alertDialog.setNegativeButton(R.string.formula_cancel, (dialog, which) -> dialog.cancel());

        alertDialog.show();
    }

    private void onSaveFileWithName(String filename) {
        String data = "";
        ArrayList<ParamInfo> params = Global.gSelParams;

        // Add function name
        for (int i = 0; i < Global.gSelFuncItems.size(); i++) {
            FunctionItem item = Global.gSelFuncItems.get(i);
            data = data + item.name + "(" + item.title + ") -> " + String.format("%02d", i + 1) + " \n";
        }
        data = data + " \n \n";

        // Add paramerters
        data = data + getString(R.string.result_para_title) + " \n \n";

        String str_param_title = "";
        for (int i = 0; i < params.size(); i++) {
            ParamInfo paramInfo = params.get(i);
            str_param_title = str_param_title + "   " + paramInfo.name + "(" + paramInfo.description + ")";
        }
        data = data + str_param_title + " \n";

        for (int i = 0; i < Global.gParamValues.size(); i++) {
            String str_param_value = "";
            String[] values = Global.gParamValues.get(i);
            for (int j = 0; j < values.length; j++) {
                str_param_value = str_param_value + "   " + values[j];
            }
            data = data + str_param_value + " \n";
        }
        data = data + " \n";

        // Add result
        data = data + getString(R.string.result_title) + " \n \n";
        String str_result = "";
        for (int i = 0; i < ary_results.size(); i++) {
            str_result = str_result + "   " + String.format("%02d", i + 1);
        }
        str_result = str_result+ " \n \n";
        for (int i = 0; i < ary_results.get(0).length; i++) {
            String str_col = "";
            for (int j = 0; j < ary_results.size(); j++) {
                str_col = str_col + "   " + ary_results.get(j)[i];
            }
            str_result = str_result + str_col + " \n";
        }
        data = data + str_result + " \n";
        data = data + " \n";

        // Add date and maker
        Date date = new Date();
        SimpleDateFormat postFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str_date = postFormater.format(date);
        data = data + getString(R.string.result_date) + "   " + str_date + " \n";
        data = data + getString(R.string.result_maker) + "   " + Global.gUserInfo.wxUserInfo.getNickname() + " \n";

        Global.writeToFile(data, filename, this);

        str_data = data;
    }

    public void onClickBackBtn(View view) {
        Global.showOtherActivity(this, MainActivity.class, 1);
    }

    public void onClickLltResultShare(View view) {
        String shareText = str_data;
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain").setText(shareText).getIntent();
        startActivity(Intent.createChooser(shareIntent, "Share"));
    }
}
