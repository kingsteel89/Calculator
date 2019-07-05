package com.shindev.rulecalculator;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.shindev.rulecalculator.common.AppConstant;
import com.shindev.rulecalculator.common.Global;
import com.shindev.rulecalculator.paybase.MD5;
import com.shindev.rulecalculator.paybase.Util;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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

public class HelpActivity extends AppCompatActivity {

    private PayReq req;
    private final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, APP_ID_WX, true);
    private Map<String,String> resultunifiedorder;
    private StringBuffer sb;

    String str_pay = "";

    private ArrayList<String> ary_formula = new ArrayList<>();

    private TextView lbl_formula;
    private String str_formula = "";
    private EditText txt_name, txt_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.txt_black, this.getTheme()));
        } else {
            getWindow().setStatusBarColor(getColor(R.color.txt_black));
        }

        req = new PayReq();
        sb = new StringBuffer();
        msgApi.registerApp(AppConstant.APP_ID_WX);

        initUIView();
    }

    private void initUIView() {
        lbl_formula = findViewById(R.id.lbl_help_main);
        txt_name = findViewById(R.id.txt_help_name);
        txt_description = findViewById(R.id.txt_help_description);
    }

    public void onClickBackIcon(View view) {
        Global.showOtherActivity(this, MainActivity.class, 1);
    }

    public void onClickLltAddFormula(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        startActivityForResult(intent, 0);
    }

    public void onClickLltHelp(View view) {
        String name = txt_name.getText().toString();
        String description = txt_description.getText().toString();
        if (name.length() == 0 || description.length() == 0 || str_formula.length() == 0) {
            onShowCheckedAlert();
            return;
        }
        if (!Global.isCheckSpelling(name)) {
            onShowAavailableAlert();
            return;
        }
        str_pay = getString(R.string.payed_btn_05);
        Global.gReportName = name;
        Global.gReportDescription = description;
        Global.gReportContent = str_formula;

        HelpActivity.GetPrepayIdTask getPrepayId = new HelpActivity.GetPrepayIdTask();
        getPrepayId.execute();
    }

    private void onShowCheckedAlert() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.alert_waring_title)
                .setMessage(R.string.setpara_alert_wrong)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    // Continue with delete operation
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (ActivityCompat.checkSelfPermission(Global.gContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                return;
            }

            ary_formula.clear();

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(data.getData())));
                String line;

                while ((line = br.readLine()) != null) {
                    ary_formula.add(line);
                }

                str_formula = ary_formula.get(0);
                for (int i = 1; i < ary_formula.size(); i++) {
                    str_formula = str_formula + "\n" + ary_formula.get(i);
                }
                lbl_formula.setText(str_formula);

                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Add Weixin Payment
    private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String,String>> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(HelpActivity.this, getString(R.string.progress_title), getString(R.string.progress_detail));
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
