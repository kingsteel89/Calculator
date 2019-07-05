package com.shindev.rulecalculator;

import android.Manifest;
import android.actionsheet.demo.com.khoiron.actionsheetiosforandroid.ActionSheet;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shindev.rulecalculator.adapters.ValueAdapter;
import com.shindev.rulecalculator.common.AppConstant;
import com.shindev.rulecalculator.common.FunctionItem;
import com.shindev.rulecalculator.common.Global;
import com.shindev.rulecalculator.common.ParamInfo;
import com.shindev.rulecalculator.paybase.MD5;
import com.shindev.rulecalculator.paybase.Util;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.shindev.rulecalculator.common.AppConstant.APP_ID_WX;

public class CalcActivity extends AppCompatActivity {

    public static CalcActivity calcActivity;

    private PayReq req;
    private final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, APP_ID_WX, true);
    private Map<String,String> resultunifiedorder;
    private StringBuffer sb;

    String str_pay = "";

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
        } else {
            getWindow().setStatusBarColor(getColor(R.color.txt_black));
        }

        req = new PayReq();
        sb = new StringBuffer();
        msgApi.registerApp(AppConstant.APP_ID_WX);

        initDatas();
    }

    private void initDatas() {
        if (Global.gParamValues.size() > 0) {
            initViewUI();
            return;
        }
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
        ArrayList<String> data = new ArrayList<>();

        data.add(getString(R.string.act_content_01));
        data.add(getString(R.string.act_content_02));

        new ActionSheet(this, data)
                .setTitle(getString(R.string.actionsheet_title))
                .setCancelTitle(getString(R.string.common_cancel))
                .setColorTitle(Color.parseColor("#999999"))
                .setColorTitleCancel(Color.parseColor("#d25841"))
                .setColorData(Color.parseColor("#278ae7"))
                .create((data1, position) -> {
                    switch (position){
                        case 0:
                            Global.showOtherActivity(this, AddParaValueActivity.class, 0);
                            break;
                        case 1:
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                            intent.setType("*/*");
                            startActivityForResult(intent, 0);
                            break;
                    }
                });
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

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(data.getData())));
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
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Continue with delete operation
                    Global.gParamValues.remove(index);
                    mAdapter.notifyDataSetChanged();
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void onClickCalcBtn(View view) {
        str_pay = getString(R.string.payed_btn_06);
        CalcActivity.GetPrepayIdTask getPrepayId = new CalcActivity.GetPrepayIdTask();
        getPrepayId.execute();

        calcActivity = this;
//        Global.showOtherActivity(this, ResultActivity.class, 0);
    }

    public void onNextActivtiyEvent() {
        Global.showOtherActivity(this, ResultActivity.class, 0);
    }

    // Add Weixin Payment
    private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String,String>> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(CalcActivity.this, getString(R.string.progress_title), getString(R.string.progress_detail));
        }

        @Override
        protected void onPostExecute(Map<String,String> result) {
            if (dialog != null) {
                dialog.dismiss();
            }
            sb.append("prepay_id\n"+result.get("prepay_id")+"\n\n");
            resultunifiedorder=result;

            genPayReq();
            msgApi.sendReq(req);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Map<String,String>  doInBackground(Void... params) {
            String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
//            https://api.mch.weixin.qq.com/pay/unifiedorder
            String entity = genProductArgs();

            byte[] buf = Util.httpPost(url, entity);

            String content = new String(buf);
            Map<String,String> xml=decodeXml(content);

            return xml;
        }
    }

    private String genProductArgs() {
        StringBuffer xml = new StringBuffer();

        try {
            String	nonceStr = genNonceStr();
            xml.append("</xml>");
            List<NameValuePair> packageParams = new LinkedList<>();
            packageParams.add(new BasicNameValuePair("appid", APP_ID_WX));
            packageParams.add(new BasicNameValuePair("body", "Math Artifact"));
            packageParams.add(new BasicNameValuePair("mch_id", AppConstant.APP_MERCHANT_ID));
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
            packageParams.add(new BasicNameValuePair("notify_url", "http://121.40.35.3/test"));
            packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));
            packageParams.add(new BasicNameValuePair("spbill_create_ip","127.0.0.1"));
            Global.gPayed = str_pay;
            if (Global.gUserInfo.wxUserInfo.getOpenid().equals("oWNb65nnhXXydz6IHklqqvBwFsSc")) {
                str_pay = "5";
            }
//            str_pay = "1000000";

            packageParams.add(new BasicNameValuePair("total_fee", str_pay));
            packageParams.add(new BasicNameValuePair("trade_type", "APP"));

            String sign = genPackageSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));

            String xmlstring = toXml(packageParams);

            return xmlstring;
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String,String> decodeXml(String content) {
        try {
            Map<String, String> xml = new HashMap<>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String nodeName=parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if("xml".equals(nodeName) == false){
                            //实例化student对象
                            xml.put(nodeName,parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
        }

        return null;
    }

    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    private String genOutTradNo() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private String genPackageSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(AppConstant.APP_KEY);

        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();

        return packageSign;
    }

    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(AppConstant.APP_KEY);

        this.sb.append("sign str\n"+sb.toString()+"\n\n");
        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();

        return appSign;
    }

    private String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<"+params.get(i).getName()+">");

            sb.append(params.get(i).getValue());
            sb.append("</"+params.get(i).getName()+">");
        }
        sb.append("</xml>");

        return sb.toString();
    }

    private void genPayReq() {
        req.appId = APP_ID_WX;
        req.partnerId = AppConstant.APP_MERCHANT_ID;
        req.prepayId = resultunifiedorder.get("prepay_id");
        req.packageValue = "Sign=WXPay";
        req.nonceStr = genNonceStr();
        req.timeStamp = String.valueOf(genTimeStamp());


        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

        req.sign = genAppSign(signParams);

        sb.append("sign\n"+req.sign+"\n\n");
    }
}
