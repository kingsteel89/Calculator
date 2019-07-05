package com.shindev.rulecalculator;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.shindev.rulecalculator.common.AppConstant;
import com.shindev.rulecalculator.common.Global;
import com.shindev.rulecalculator.common.UserInfo;
import com.shindev.rulecalculator.common.WXUserInfo;
import com.shindev.rulecalculator.wxapi.WXEntryActivity;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import static com.shindev.rulecalculator.common.AppConstant.APP_ID_WX;
import static com.shindev.rulecalculator.common.Global.gFlgNewLogin;
import static com.shindev.rulecalculator.common.Global.testMode;

public class LoginActivity extends AppCompatActivity {

    private IWXAPI api;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(false);

        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.txt_black, this.getTheme()));
        } else {
            getWindow().setStatusBarColor(getColor(R.color.txt_black));
        }

        api = WXAPIFactory.createWXAPI(this, APP_ID_WX, false);

        Global.gContext = this;
    }

    public void onResume() {
        super.onResume();
        if (WXEntryActivity.token != null) {
            Toast.makeText(this, "Token: " + WXEntryActivity.token, Toast.LENGTH_LONG).show();
            WXEntryActivity.token = null;
        }
    }

    public void onClickWechatLogin(View view) {
        Global.gFrgIndex = 5;

        WXUserInfo userInfo = new WXUserInfo();
        if (testMode) {
            userInfo.setHeadimgurl("http://thirdwx.qlogo.cn/mmopen/vi_32/ILQ1KcyETxH1VzV05xoMD1ggVRVhHib9UP3eItmibTeoIm8nVaYV5qQukU9MOibqdSaO0fynln9Uwg2oQmBrTn3ug/132");
            userInfo.setSex(0);
            userInfo.setNickname("杨现");
            userInfo.setOpenid("oWNb65nnhXXydz6IHklqqvBwFsSc");
            userInfo.setProvince("Liaoning");
            userInfo.setCity("Shenyang");
            userInfo.setCountry("CN");
        } else {
            userInfo = WXUserInfo.getUserInfo();
        }
        gFlgNewLogin = false;

        if (userInfo == null) {
            gFlgNewLogin = true;

            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "none";
            api.sendReq(req);
        } else {
            dialog = ProgressDialog.show(LoginActivity.this, getString(R.string.progress_title), getString(R.string.progress_detail));

            Global.gUserInfo = new UserInfo();
            Global.gUserInfo.wxUserInfo = userInfo;
            OkHttpUtils.get().url(AppConstant.RELOGIN)
                    .addParams("openid", userInfo.getOpenid())
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(okhttp3.Call call, Exception e, int id) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), R.string.alert_error_internet_detail, Toast.LENGTH_SHORT).show();
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
                                        JSONObject obj_result = obj.getJSONObject("result");
                                        Global.gUserInfo.initialWithJson(obj_result);
                                        onShowMainActivity();
                                        break;
                                    case 10001:
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
    }

    private void onShowMainActivity() {
        Global.showOtherActivity(this, MainActivity.class, 0);
    }

    public void onClickLltConatact(View view) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse(getString(R.string.profile_phone_number)));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 0);
            return;
        }
        startActivity(callIntent);
    }
}
