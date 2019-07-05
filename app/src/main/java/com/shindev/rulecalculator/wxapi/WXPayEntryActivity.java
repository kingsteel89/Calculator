package com.shindev.rulecalculator.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.shindev.rulecalculator.CalcActivity;
import com.shindev.rulecalculator.R;
import com.shindev.rulecalculator.ResultActivity;
import com.shindev.rulecalculator.common.AppConstant;
import com.shindev.rulecalculator.common.Global;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	private static final String TAG = "WXPayEntryActivity";

    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        
    	api = WXAPIFactory.createWXAPI(this, AppConstant.APP_ID_WX, true);
		api.registerApp(AppConstant.APP_ID_WX);
		try {
			boolean result =  api.handleIntent(getIntent(), this);
			if(!result){
				finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errStr);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			if (resp.errCode == 0) {
				Map<String, String> params = new HashMap<>();
				params.put("id_user", Global.gUserInfo.id);
				params.put("payed", Global.gPayed);

				if (Global.gReportName.length() > 0) {
					params.put("name", Global.gReportName);
					params.put("description", Global.gReportDescription);
					params.put("content", Global.gReportContent);
				}

				if (Global.gPayed.equals("100")) {
					String str_function = Global.gSelFuncItems.get(0).id;
					for (int i = 1; i< Global.gSelFuncItems.size(); i++) {
						str_function = str_function + "," + Global.gSelFuncItems.get(i).id;
					}
					params.put("name", Global.gReportName);
					params.put("id_function", str_function);
				}

				OkHttpUtils.get().url(AppConstant.SAVEPAYMENT)
						.params(params)
						.build()
						.execute(new StringCallback() {
							@Override
							public void onError(okhttp3.Call call, Exception e, int id) {
								Toast.makeText(getApplicationContext(), R.string.alert_error_internet_detail, Toast.LENGTH_SHORT).show();
								finish();
							}

							@Override
							public void onResponse(String response, int id) {
								if (Global.gPayed.equals("49")) {
								    CalcActivity.calcActivity.onNextActivtiyEvent();
									return;
								}
								try {
									JSONObject obj = new JSONObject(response);
									JSONObject obj_result = obj.getJSONObject("result");
									Global.gUserInfo.initialWithJson(obj_result);
								} catch (JSONException e) {
									Toast.makeText(getApplicationContext(), R.string.alert_server_error_detail, Toast.LENGTH_SHORT).show();
								}
								Global.gReportName = "";
								Global.gReportDescription = "";
								Global.gReportContent = "";
								finish();
							}
						});
			}
		}
	}
}