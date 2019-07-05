package com.shindev.rulecalculator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shindev.rulecalculator.common.AppConstant;
import com.shindev.rulecalculator.common.CreateParaItem;
import com.shindev.rulecalculator.common.Global;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TestAlgorithmActivity extends AppCompatActivity {
    private boolean isChecked = false;
    private TextView lbl_algorithm, lbl_result;

    private Button btn_next;
    private String str_formula;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.txt_black, this.getTheme()));
        } else {
            getWindow().setStatusBarColor(getColor(R.color.txt_black));
        }

        initUIView();
    }

    private void initUIView() {
        btn_next = findViewById(R.id.btn_test_next);
        btn_next.setOnClickListener(v -> {
            if (!isChecked) {
                onShowWaringAlert();
                return;
            }
            Global.showOtherActivity(TestAlgorithmActivity.this, SaveAlgorithmActivity.class, 0);
        });
        lbl_algorithm = findViewById(R.id.lbl_test_algorithm);
        lbl_algorithm.setText(Global.gAlgorithm);

        lbl_result = findViewById(R.id.lbl_test_result);
    }

    public void onClickBackIcon(View view) {
        Global.showOtherActivity(this, FormulaActivity.class, 1);
    }

    private void onShowWaringAlert() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.setpara_alert_waring)
                .setMessage(R.string.alert_test_detail)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    // Continue with delete operation
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void onEventTestAlgorithm (final String title) {
        dialog = ProgressDialog.show(this, getString(R.string.progress_title), getString(R.string.progress_detail));

        String str_url = AppConstant.TESTFORMULA;

        final Map<String, String> params = new HashMap<>();
        params.put("id", Global.gUserInfo.id);

        String str_body = getFormulaBody(title);
        params.put("body", str_body);

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
                        Global.showOtherActivity(TestAlgorithmActivity.this, LoginActivity.class, 1);
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
                                    onTestNewFunction(title);
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

    private void onTestNewFunction(String title) {
        dialog = ProgressDialog.show(this, getString(R.string.progress_title), getString(R.string.progress_detail));
        String str_url = AppConstant.SERVER_URL + title + "/test";
        final Map<String, String> params = new HashMap<>();
        for (int i = 0; i < Global.gCreateParams.size(); i++) {
            String key = Global.gCreateParams.get(i).name;
            Random r = new Random();
            int i1 = r.nextInt(150 - 100) + 100;
            String value = String.valueOf(i1);
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
                        Global.showOtherActivity(TestAlgorithmActivity.this, LoginActivity.class, 1);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        try {
                            JSONObject obj = new JSONObject(response);
                            int ret = obj.getInt("ret");
                            String str_msg;
                            if (ret == 10000) {
                                str_msg = obj.getString("msg");
                                isChecked = true;
                            } else {
                                str_msg = obj.getString("result");
                            }
                            lbl_result.setText(str_msg);
                        } catch (JSONException e) {
                            onShowErrorAlgorithm(response);
                        }
                    }
                });
    }

    private void onShowErrorAlgorithm (String error) {
        ArrayList<Integer> ary_lines = new ArrayList<>();
        String[] str_result = error.split("\n");
        String str_show = "";
        for (int i = 0; i < str_result.length; i++) {
            String str_value = str_result[i];
            if (str_value.contains("Line Number:")) {
                str_value = str_value.replace("<p>Line Number: ", "");
                str_value = str_value.replace("</p>", "");
                str_show = str_show + String.format(getString(R.string.test_error_linenumber), Integer.valueOf(str_value) - 14 - Global.gCreateParams.size() * 2) + "\n";
                ary_lines.add(Integer.valueOf(str_value) - 14 - Global.gCreateParams.size() * 2);
            }
        }
        lbl_result.setText(str_show);

        String[] str_algos = Global.gAlgorithm.split("\n");
        for (int i = 0; i < str_algos.length; i++) {
            String str = str_algos[i];
            if (i == 0) {
                if (ary_lines.contains(i + 1)) {
                    str = "<p><font color=#ff0000>" + str + "</font></p>";
                } else {
                    str = "<p>" + str + "</font></p>";
                }
                str_show = str;
            } else {
                if (ary_lines.contains(i + 1)) {
                    str = "<p><font color=#ff0000>" + str + "</font></p>";
                } else {
                    str = "<p>" + str + "</font></p>";
                }
                str_show = str_show + "<br>" + str;
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            lbl_algorithm.setText(Html.fromHtml(str_show, Html.FROM_HTML_MODE_COMPACT));
        } else {
            lbl_algorithm.setText(Html.fromHtml(str_show));
        }
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

    public void onClickBtnTest(View view) {
        onEventTestAlgorithm("test");
    }
}
