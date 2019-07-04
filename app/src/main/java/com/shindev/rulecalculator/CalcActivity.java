package com.shindev.rulecalculator;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shindev.rulecalculator.adapters.ValueAdapter;
import com.shindev.rulecalculator.common.AppConstant;
import com.shindev.rulecalculator.common.FunctionItem;
import com.shindev.rulecalculator.common.Global;
import com.shindev.rulecalculator.common.ParamInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CalcActivity extends AppCompatActivity {

    TextView lbl_title;
    ListView lst_value;
    ValueAdapter mAdapter;

    String[] values;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.txt_black, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.txt_black));
        }

        initDatas();
    }

    private void initDatas() {
        dialog = ProgressDialog.show(CalcActivity.this, getString(R.string.progress_title), getString(R.string.progress_detail));

        String str_ids = Global.gSelFuncItems.get(0).id;
        for (int i = 0; i < Global.gSelFuncItems.size(); i++) {
            FunctionItem item = Global.gSelFuncItems.get(i);
            str_ids = str_ids + "," + item.id;
        }
        OkHttpUtils.get().url(AppConstant.GETPARAMS)
                .addParams("id_func", str_ids)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        Toast.makeText(getApplicationContext(), R.string.alert_error_internet_detail, Toast.LENGTH_SHORT).show();
                        Global.showOtherActivity(CalcActivity.this, LoginActivity.class, 1);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        Global.gSelParams.clear();
                        try {
                            JSONObject obj = new JSONObject(response);
                            int ret = obj.getInt("ret");
                            switch (ret) {
                                case 10000:
                                    JSONArray result = obj.getJSONArray("result");
                                    for (int i = 0; i < result.length(); i++) {
                                        JSONObject object = result.getJSONObject(i);

                                        ParamInfo param = new ParamInfo();
                                        param.initialWithJson(object);

                                        Global.gSelParams.add(param);
                                    }

                                    initViewUI();
                                    break;
                                case 10001:
                                    String msg = obj.getString("msg");
                                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                    Global.showOtherActivity(CalcActivity.this, MainActivity.class, 1);
                                    break;
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), R.string.alert_server_error_detail, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initViewUI() {
        lbl_title = findViewById(R.id.lbl_calc_title);
        lbl_title.setText(R.string.calc_title);

        lst_value = findViewById(R.id.lst_calc_para);
        mAdapter = new ValueAdapter(this, Global.gSelParams, Global.gParamValues);
        lst_value.setAdapter(mAdapter);
    }

    public void onClickBackIcon(View view) {
        Global.showOtherActivity(this, MainActivity.class, 1);
    }

    public void onClickLltAdd(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (ActivityCompat.checkSelfPermission(Global.gContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                return;
            }

            Global.gParamValues.clear();

            File file = new File(data.getData().getPath());
            String path = file.getAbsolutePath();
            String newPath = "/storage/emulated/0/" + path.split(":")[1];
            try {
                BufferedReader br = new BufferedReader(new FileReader(newPath));
                String line;

                while ((line = br.readLine()) != null) {
                    if (line.equals("\n")) {
                        continue;
                    }
                    String[] ary_values = line.split(",");
                    Global.gParamValues.add(ary_values);
                }
                br.close();

                mAdapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onEventValueDelete (final int index) {
        new AlertDialog.Builder(CalcActivity.this)
                .setTitle(R.string.calc_alert_delete_title)
                .setMessage(R.string.calc_alert_delete_detail)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        Global.gParamValues.remove(index);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void onClickCalcBtn(View view) {
        Global.showOtherActivity(this, ResultActivity.class, 0);
    }
}
