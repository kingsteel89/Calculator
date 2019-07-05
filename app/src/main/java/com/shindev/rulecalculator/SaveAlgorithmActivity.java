package com.shindev.rulecalculator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shindev.rulecalculator.common.AppConstant;
import com.shindev.rulecalculator.common.CreateParaItem;
import com.shindev.rulecalculator.common.Global;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SaveAlgorithmActivity extends AppCompatActivity {
    private EditText txt_name, txt_description;

    private String str_formula, str_name, str_description;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_algorithm);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.txt_black, this.getTheme()));
        } else {
            getWindow().setStatusBarColor(getColor(R.color.txt_black));
        }

        initUIView();
    }

    private void initUIView() {
        TextView lbl_detail = findViewById(R.id.lbl_save_detail);
        lbl_detail.setText(Global.gAlgorithm);

        txt_name = findViewById(R.id.txt_save_algo_name);
        txt_description = findViewById(R.id.txt_save_algo_description);
    }

    private void onShowAavailableAlert() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.alert_waring_title)
                .setMessage(R.string.alert_para_able_detail)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    // Continue with delete operation
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void onEventTestAlgorithm () {
        dialog = ProgressDialog.show(this, getString(R.string.progress_title), getString(R.string.progress_detail));

        String str_url = AppConstant.SAVEFORMULA;

        final Map<String, String> params = new HashMap<>();
        params.put("title", str_name);
        params.put("description", str_description);
        params.put("id", Global.gUserInfo.id);

        String str_param = Global.gCreateParams.get(0).name + "," + Global.gCreateParams.get(0).description;
        for (int i = 1; i < Global.gCreateParams.size(); i++) {
            CreateParaItem item = Global.gCreateParams.get(i);
            String str_item = item.name + "," + item.description;
            str_param = str_param + ":" + str_item;
        }
        params.put("params", str_param);

        String str_body = getFormulaBody(str_name);
        params.put("body", str_body);

        params.put("algorithm", Global.gAlgorithm);

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
                        Global.showOtherActivity(SaveAlgorithmActivity.this, LoginActivity.class, 1);
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
                                    String title = obj.getString("result");

                                    Global.gUserInfo.cnt_restfunc--;
                                    Global.gUserInfo.cnt_func++;
                                    Global.gFrgIndex = 0;
                                    onClickBackIcon(null);

                                    Global.writeToFile(str_formula, title + "(" + str_description + ")", SaveAlgorithmActivity.this);

                                    Global.showOtherActivity(SaveAlgorithmActivity.this, MainActivity.class, 1);
                                    Toast.makeText(getApplicationContext(), R.string.common_success, Toast.LENGTH_SHORT).show();
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

    private String getFormulaBody(String name) {
        str_formula = Global.gAlgorithm;
        // Create Formula Header
        String body = String.format("function %s () {", name);
        body = body + "\n";

        // Add Formula Parameters
        for (int i = 0; i < Global.gCreateParams.size(); i++) {
            CreateParaItem item = Global.gCreateParams.get(i);
            String str_item = String.format("$%s = $this->input->get('%s');", item.name, item.name);
            body = body + str_item + "\n";
        }
        body = body + "\n";

        // Add Formula Parameters Spilt
        for (int i = 0; i < Global.gCreateParams.size(); i++) {
            CreateParaItem item = Global.gCreateParams.get(i);
            String str_item = String.format("$ary_%s = explode(',', $%s);", item.name, item.name);
            body = body + str_item + "\n";
        }
        body = body + "\n";

        // Add Formula Body
        body = body + String.format("for ($i=0; $i < sizeof($ary_%s); $i++) {", Global.gCreateParams.get(0).name) + " \n";
        for (int i = 0; i < Global.gCreateParams.size(); i++) {
            CreateParaItem item = Global.gCreateParams.get(i);
            String str_before = "$" + item.name;
            String str_after = "$ary_" + item.name + "[$i]";
            str_formula = str_formula.replace(str_before, str_after);
        }
        str_formula = str_formula.replace("$Re", "$result[$i]");
        body = body + str_formula;
        body = body + "}" + "\n";
        body = body + "\n";

        // Add Formula Output
        String output = "echo json_encode(array('ret' => 10000, 'msg' => 'Success', 'result' => $result));";
        body = body + output + "\n";
        body = body + "\n";

        body = body + "}" + "\n";
        return body;
    }

    public void onClickBackIcon(View view) {
        Global.showOtherActivity(this, TestAlgorithmActivity.class, 1);
    }

    public void onClickBtnTest(View view) {
        str_name = txt_name.getText().toString();
        if (Global.isCheckSpelling(str_name)) {
            onShowAavailableAlert();
        }

        str_description = txt_description.getText().toString();
        if (str_description.length() == 0) {
            str_description = getResources().getString(R.string.item_create_description);
        }

        onEventTestAlgorithm();

    }
}
