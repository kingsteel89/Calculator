package com.shindev.rulecalculator;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.shindev.rulecalculator.common.AppConstant;
import com.shindev.rulecalculator.common.FunctionItem;
import com.shindev.rulecalculator.common.Global;
import com.shindev.rulecalculator.common.ParamInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FunctionDetailActivity extends AppCompatActivity {
    private ProgressDialog dialog;

    private FunctionItem functionItem = new FunctionItem();
    private ArrayList<ParamInfo> paramInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.txt_black, this.getTheme()));
        } else {
            getWindow().setStatusBarColor(getColor(R.color.txt_black));
        }

        initDatas();
    }

    private void initDatas() {
        dialog = ProgressDialog.show(this, getString(R.string.progress_title), getString(R.string.progress_detail));

        OkHttpUtils.get().url(AppConstant.FUNCTIONDETAIL)
                .addParams("id_function", String.valueOf(Global.gSelIndex))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        Toast.makeText(FunctionDetailActivity.this, R.string.alert_error_internet_detail, Toast.LENGTH_SHORT).show();
                        Global.showOtherActivity(FunctionDetailActivity.this, LoginActivity.class, 1);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        try {
                            JSONObject obj = new JSONObject(response);

                            int ret = obj.getInt("ret");
                            switch (ret) {
                                case 10000:
                                    JSONObject result = obj.getJSONObject("result");
                                    JSONObject function = result.getJSONObject("function");
                                    functionItem.initialWithJson(function);

                                    paramInfos.clear();
                                    JSONArray params = result.getJSONArray("params");
                                    for (int i = 0; i < params.length(); i++) {
                                        JSONObject json = params.getJSONObject(i);

                                        ParamInfo item = new ParamInfo();
                                        item.initialWithJson(json);

                                        paramInfos.add(item);
                                    }

                                    initListView();
                                    break;
                                case 10001:
                                    Toast.makeText(FunctionDetailActivity.this, R.string.list_empty_function, Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    String msg = obj.getString("msg");
                                    Toast.makeText(FunctionDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    break;
                            }


                        } catch (JSONException e) {
                            Toast.makeText(FunctionDetailActivity.this, R.string.alert_server_error_detail, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initListView() {
        TextView title = findViewById(R.id.txt_fdetail_title);
        title.setText(functionItem.name);

        TextView txt_params = findViewById(R.id.txt_fdetail_para);
        String str_params = "";
        for (ParamInfo paramInfo: paramInfos) {
            str_params = str_params + paramInfo.name + " - " + paramInfo.description + " : " + paramInfo.regdate + "\n";
        }
        txt_params.setText(str_params);

        TextView txt_algorithm = findViewById(R.id.txt_fdetail_algo);
        txt_algorithm.setText(functionItem.content);
    }

    public void onClickBackIcon(View view) {
        Global.gFrgIndex = 0;
        Global.showOtherActivity(this, MainActivity.class, 1);
    }

    public void onClickBtnEdit(View view) {
        //
    }

    public void onClickLltDelete(View view) {
        //
    }
}
