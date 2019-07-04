package com.shindev.rulecalculator;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shindev.rulecalculator.adapters.FunctionAdapter;
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

public class ProjectDetailActivity extends AppCompatActivity {
    private TextView txt_para;

    private ListView lst_function;
    private FunctionAdapter mAdapter;
    private ArrayList<FunctionItem> mDatas = new ArrayList<>();
    private ArrayList<ParamInfo> mParams = new ArrayList<>();

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projectdetail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.txt_black, this.getTheme()));
        } else {
            getWindow().setStatusBarColor(getColor(R.color.txt_black));
        }

        initUIView();
    }

    private void initUIView() {
        TextView txt_title = findViewById(R.id.lbl_project_detail_title);
        txt_title.setText(Global.gSelProject.name);

        lst_function = findViewById(R.id.lst_function_detail);
        txt_para= findViewById(R.id.lbl_project_detail_params);

        initDatas();
    }

    private void initDatas () {
        dialog = ProgressDialog.show(this, getString(R.string.progress_title), getString(R.string.progress_detail));
        OkHttpUtils.get().url(AppConstant.PROJECTDETAIL)
                .addParams("id_project", Global.gSelProject.id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        Toast.makeText(getApplicationContext(), R.string.alert_error_internet_detail, Toast.LENGTH_SHORT).show();
                        Global.showOtherActivity(ProjectDetailActivity.this, LoginActivity.class, 1);
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
                                    JSONArray functions = result.getJSONArray("function");
                                    JSONArray params = result.getJSONArray("param");

                                    mDatas.clear();
                                    mParams.clear();

                                    if (functions.length() > 0) {
                                        for (int i = 0; i < functions.length(); i++) {
                                            JSONObject json = functions.getJSONObject(i);

                                            FunctionItem item = new FunctionItem();
                                            item.initialWithJson(json);

                                            mDatas.add(item);
                                        }

                                        for (int i = 0; i < params.length(); i++) {
                                            JSONObject json = params.getJSONObject(i);

                                            ParamInfo item = new ParamInfo();
                                            item.initialWithJson(json);

                                            mParams.add(item);
                                        }

                                    }
                                    initListView();
                                    break;
                                case 10001:
                                    Toast.makeText(getApplicationContext(), R.string.list_empty_function, Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    String msg = obj.getString("msg");
                                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                    break;
                            }


                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), R.string.alert_server_error_detail, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initListView() {
        mAdapter = new FunctionAdapter(this, mDatas);
        lst_function.setAdapter(mAdapter);

        String str_para = "";
        for (int i = 0; i < mParams.size(); i++) {
            ParamInfo info = mParams.get(i);
            if (i == mParams.size() - 1) {
                str_para = str_para + String.valueOf(i + 1) + ". " + info.name + " ( " + info.description + " )";
            } else {
                str_para = str_para + String.valueOf(i + 1) + ". " + info.name + " ( " + info.description + " )\n";
            }
        }
        txt_para.setText(str_para);
    }

    public void onClickBackIcon(View view) {
        Global.showOtherActivity(this, MainActivity.class, 1);
    }

    public void onClickLltCalculate(View view) {
        Global.gSelFuncItems.clear();
        Global.gSelFuncItems = (ArrayList<FunctionItem>) mDatas.clone();
        Global.showOtherActivity(this, CalcActivity.class, 0);
    }
}
