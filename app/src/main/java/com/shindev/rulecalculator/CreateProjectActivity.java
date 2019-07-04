package com.shindev.rulecalculator;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.shindev.rulecalculator.adapters.FunctionAdapter;
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

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.shindev.rulecalculator.common.AppConstant.APP_ID_WX;

public class CreateProjectActivity extends AppCompatActivity {

    private EditText txt_project;

    PayReq req;
    final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, APP_ID_WX, true);
    Map<String,String> resultunifiedorder;
    StringBuffer sb;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createproject);

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

    private void initUIView() {
        for (int i = 0; i < Global.gSelFuncItems.size(); i++) {
            FunctionItem item = Global.gSelFuncItems.get(i);
            item.isSelected = false;
        }
        ListView lst_function = findViewById(R.id.lst_create_project);
        FunctionAdapter mAdapter = new FunctionAdapter(this, Global.gSelFuncItems);
        lst_function.setAdapter(mAdapter);

        txt_project = findViewById(R.id.txt_create_project);
    }

    private void initDatas() {
        dialog = ProgressDialog.show(CreateProjectActivity.this, getString(R.string.progress_title), getString(R.string.progress_detail));

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
                        Global.showOtherActivity(CreateProjectActivity.this, LoginActivity.class, 1);
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

                                    initUIView();
                                    break;
                                case 10001:
                                    String msg = obj.getString("msg");
                                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                    Global.showOtherActivity(CreateProjectActivity.this, MainActivity.class, 1);
                                    break;
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), R.string.alert_server_error_detail, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void onClickBackIcon(View view) {
        Global.gFrgIndex = 5;
        Global.showOtherActivity(this, MainActivity.class, 1);
    }

    public void onClickLltCreateProject(View view) {
        String str_name = txt_project.getText().toString();
        if (str_name.length() == 0) {
            Toast.makeText(this, R.string.create_toast_name, Toast.LENGTH_SHORT).show();
            return;
        }
        Global.gReportName = str_name;
        CreateProjectActivity.GetPrepayIdTask getPrepayId = new CreateProjectActivity.GetPrepayIdTask();
        getPrepayId.execute();
    }

    // Add Weixin Payment
    private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String,String>> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(CreateProjectActivity.this, getString(R.string.progress_title), getString(R.string.progress_detail));
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
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new BasicNameValuePair("appid", APP_ID_WX));
            packageParams.add(new BasicNameValuePair("body", "Math Artifact"));
            packageParams.add(new BasicNameValuePair("mch_id", AppConstant.APP_MERCHANT_ID));
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
            packageParams.add(new BasicNameValuePair("notify_url", "http://121.40.35.3/test"));
            packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));
            packageParams.add(new BasicNameValuePair("spbill_create_ip","127.0.0.1"));
            String str_pay = "100";
            Global.gPayed = str_pay;
            if (Global.gUserInfo.wxUserInfo.getOpenid().equals("oWNb65nnhXXydz6IHklqqvBwFsSc")) {
                str_pay = "5";
            }
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
            Map<String, String> xml = new HashMap<String, String>();
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
