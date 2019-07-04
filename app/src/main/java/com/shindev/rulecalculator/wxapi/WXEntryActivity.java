/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package com.shindev.rulecalculator.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.shindev.rulecalculator.MainActivity;
import com.shindev.rulecalculator.common.WXAccessTokenEntity;
import com.shindev.rulecalculator.common.WXUserInfo;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import com.vise.log.ViseLog;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import static com.shindev.rulecalculator.common.AppConstant.APP_ID_WX;
import static com.shindev.rulecalculator.common.AppConstant.APP_SECRET_WX;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	public static String token;
	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		api = WXAPIFactory.createWXAPI(this, APP_ID_WX , false);
		api.registerApp(APP_ID_WX);
		try {
			boolean result =  api.handleIntent(getIntent(), this);
			if(!result){
				ViseLog.d("参数不合法，未被SDK处理，退出");
				finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		finish();
	}

	@Override
	public void onReq(BaseReq baseReq) {
		//
	}

	@Override
	public void onResp(BaseResp baseResp) {
		String mWechatInfo = JSON.toJSONString(baseResp);
		JSONObject obj = null;
		try {
			obj = new JSONObject(mWechatInfo);
			Log.d("My App", obj.toString());
		} catch (Throwable t) {
			Log.e("My App", "Could not parse malformed JSON: \"" + mWechatInfo + "\"");
		}
		String mCode = "";
		try {
			mCode = obj.getString("code");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String result = "";
		switch(baseResp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				result ="发送成功";
				OkHttpUtils.get().url("https://api.weixin.qq.com/sns/oauth2/access_token")
						.addParams("appid", APP_ID_WX)
						.addParams("secret", APP_SECRET_WX)
						.addParams("code", mCode)
						.addParams("grant_type","authorization_code")
						.build()
						.execute(new StringCallback() {
							@Override
							public void onError(okhttp3.Call call, Exception e, int id) {
								ViseLog.d("请求错误..");
							}

							@Override
							public void onResponse(String response, int id) {
								ViseLog.d("response:"+response);
								JSONObject mJsonToken = null;
								try {
									mJsonToken = new JSONObject(response);
									Log.d("My App", mJsonToken.toString());
								} catch (Throwable t) {
									Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
								}
								WXAccessTokenEntity accessTokenEntity = new WXAccessTokenEntity();
								try {
									accessTokenEntity.setAccess_token(mJsonToken.getString("access_token"));
									accessTokenEntity.setExpires_in(mJsonToken.getInt("expires_in"));
									accessTokenEntity.setRefresh_token(mJsonToken.getString("refresh_token"));
									accessTokenEntity.setOpenid(mJsonToken.getString("openid"));
									accessTokenEntity.setScope(mJsonToken.getString("scope"));
									accessTokenEntity.setUnionid(mJsonToken.getString("unionid"));
								} catch (JSONException e) {
									e.printStackTrace();
								}
								if(accessTokenEntity!=null){
									getUserInfo(accessTokenEntity);
								}else {
									ViseLog.d("获取失败");
								}
							}
						});
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				result = "发送取消";
				ViseLog.d("发送取消");
				finish();
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				result = "发送被拒绝";
				ViseLog.d("发送被拒绝");
				finish();
				break;
			case BaseResp.ErrCode.ERR_UNSUPPORT:
				result = "签名错误";
				ViseLog.d("签名错误");
				break;
			default:
				result = "发送返回";
				finish();
				break;
		}
		Toast.makeText(WXEntryActivity.this,result,Toast.LENGTH_LONG).show();
	}

	/**
	 * 获取个人信息
	 * @param accessTokenEntity
	 */
	private void getUserInfo(WXAccessTokenEntity accessTokenEntity) {
		OkHttpUtils.get()
				.url("https://api.weixin.qq.com/sns/userinfo")
				.addParams("access_token",accessTokenEntity.getAccess_token())
				.addParams("openid",accessTokenEntity.getOpenid())//openid:授权用户唯一标识
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(okhttp3.Call call, Exception e, int id) {
						ViseLog.d("获取错误..");
					}

					@Override
					public void onResponse(String response, int id) {
						ViseLog.d("userInfo:"+response);
						JSONObject mJsonWechat = null;
						try {
							mJsonWechat = new JSONObject(response);
							Log.d("My App", mJsonWechat.toString());
						} catch (Throwable t) {
							Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
						}
						WXUserInfo wxResponse = new WXUserInfo();
						try {
							wxResponse.setOpenid(mJsonWechat.getString("openid"));
							wxResponse.setNickname(mJsonWechat.getString("nickname"));
							wxResponse.setSex(mJsonWechat.getInt("sex"));
							wxResponse.setLanguage(mJsonWechat.getString("language"));
							wxResponse.setCity(mJsonWechat.getString("city"));
							wxResponse.setProvince(mJsonWechat.getString("province"));
							wxResponse.setCountry(mJsonWechat.getString("country"));
							wxResponse.setHeadimgurl(mJsonWechat.getString("headimgurl"));
							wxResponse.setUnionid(mJsonWechat.getString("unionid"));

						} catch (JSONException e) {
							e.printStackTrace();
						}
						WXUserInfo.setUserInfo(wxResponse);

						Intent intent = new Intent(WXEntryActivity.this, MainActivity.class);
						WXEntryActivity.this.startActivity(intent);

//						Global.showOtherActivity(WXEntryActivity.this, MainActivity.class, 0);

						finish();
					}
				});
	}
}
