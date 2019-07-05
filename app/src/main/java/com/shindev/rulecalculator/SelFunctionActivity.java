package com.shindev.rulecalculator;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.shindev.rulecalculator.adapters.SelFunctionAdapter;
import com.shindev.rulecalculator.common.AppConstant;
import com.shindev.rulecalculator.common.FunctionItem;
import com.shindev.rulecalculator.common.Global;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SelFunctionActivity extends AppCompatActivity {
    private ArrayList<FunctionItem> mFunctionDatas = new ArrayList<>();

    private ListView lst_function;
    private SelFunctionAdapter mAdapterFunction;
    private ProgressDialog dialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sel_function);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.txt_black, this.getTheme()));
        } else {
            getWindow().setStatusBarColor(getColor(R.color.txt_black));
        }

        initDatas();

        lst_function = findViewById(R.id.lst_sel_functions);
        lst_function.setOnItemClickListener((parent, view, position, id) -> {
            FunctionItem item = mFunctionDatas.get(position);
            item.isSelected = !item.isSelected;
            mAdapterFunction.notifyDataSetChanged();
        });
    }

    private void initDatas() {
        dialog = ProgressDialog.show(this, getString(R.string.progress_title), getString(R.string.progress_detail));

        OkHttpUtils.get().url(AppConstant.GETFUNCS)
                .addParams("id_user", Global.gUserInfo.id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        if (null != dialog) {
                            dialog.dismiss();
                        }
                        Toast.makeText(SelFunctionActivity.this, R.string.alert_error_internet_detail, Toast.LENGTH_SHORT).show();
                        Global.showOtherActivity(SelFunctionActivity.this, LoginActivity.class, 1);
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
                                    JSONArray result = obj.getJSONArray("result");
                                    JSONArray params = obj.getJSONArray("param");

                                    mFunctionDatas.clear();

                                    if (result.length() > 0) {
                                        for (int i = 0; i < result.length(); i++) {
                                            JSONObject json = result.getJSONObject(i);

                                            FunctionItem item = new FunctionItem();
                                            item.initialWithJson(json);
                                            item.cnt_para = params.getInt(i);

                                            mFunctionDatas.add(item);
                                        }

                                        initListView();
                                    }
                                    break;
                                case 10001:
                                    Toast.makeText(SelFunctionActivity.this, R.string.list_empty_function, Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    String msg = obj.getString("msg");
                                    Toast.makeText(SelFunctionActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    break;
                            }


                        } catch (JSONException e) {
                            Toast.makeText(SelFunctionActivity.this, R.string.alert_server_error_detail, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initListView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(mFunctionDatas, Comparator.comparing(FunctionItem::getName));
            Collections.sort(mFunctionDatas, Comparator.comparing(FunctionItem::getCnt_para));
        }

        mAdapterFunction = new SelFunctionAdapter(this, mFunctionDatas);
        lst_function.setAdapter(mAdapterFunction);
    }

    public void onClickBackIcon(View view) {
        Global.showOtherActivity(this, MainActivity.class, 1);
    }

    public void onClickSelFunctionNextBtn(View view) {
        Global.gSelFuncItems.clear();
        for (int i = 0; i < mFunctionDatas.size(); i++) {
            FunctionItem item = mFunctionDatas.get(i);
            if (item.isSelected) {
                Global.gSelFuncItems.add(item);
            }
        }
        if (Global.gSelFuncItems.size() > 0) {
            Global.showOtherActivity(this, CreateProjectActivity.class, 0);
        } else {
            Toast.makeText(this, R.string.main_empty_select, Toast.LENGTH_SHORT).show();
        }
    }
}
