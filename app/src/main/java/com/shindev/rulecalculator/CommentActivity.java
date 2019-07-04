package com.shindev.rulecalculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.shindev.rulecalculator.common.AppConstant;
import com.shindev.rulecalculator.common.Global;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class CommentActivity extends AppCompatActivity {

    EditText txt_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.txt_black, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.txt_black));
        }
        initUIVIew();
    }

    private void initUIVIew() {
        txt_detail = findViewById(R.id.txt_comment_detail);
    }

    public void onClickBackIcon(View view) {
        Global.showOtherActivity(this, MainActivity.class, 1);
    }

    public void onClickBtnSubmit(View view) {
        String str_comment = txt_detail.getText().toString();
        if (str_comment.length() == 0) {
            onShowCheckedAlert();
            return;
        }
        OkHttpUtils.get().url(AppConstant.ADDCOMMENT)
                .addParams("id_user", Global.gUserInfo.id)
                .addParams("comment", str_comment)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Toast.makeText(getApplicationContext(), R.string.alert_error_internet_detail, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            String result = obj.getString("result");
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                            onClickBackIcon(null);
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), R.string.alert_server_error_detail, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void onShowCheckedAlert() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.alert_waring_title)
                .setMessage(R.string.alert_empty_detail)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
