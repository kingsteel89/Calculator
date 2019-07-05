package com.shindev.rulecalculator.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shindev.rulecalculator.CommentActivity;
import com.shindev.rulecalculator.GuideActivity;
import com.shindev.rulecalculator.HelpActivity;
import com.shindev.rulecalculator.LoginActivity;
import com.shindev.rulecalculator.MainActivity;
import com.shindev.rulecalculator.R;
import com.shindev.rulecalculator.SupportActivity;
import com.shindev.rulecalculator.WechatPayActivity;
import com.shindev.rulecalculator.common.AppConstant;
import com.shindev.rulecalculator.common.Global;
import com.shindev.rulecalculator.common.WXUserInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {

    private LinearLayout llt_payed, llt_comment, llt_support, llt_logout, llt_report, llt_guide;
    private ImageView img_avatar;
    private TextView lbl_name, lbl_location, lbl_payed, lbl_func, lbl_rfunc, lbl_classname, lbl_update;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.fragment_profile, container, false);

        String str_classname = Global.gUserInfo.classname;
        if (str_classname.length() == 0) {
            onShowAlertClassName(mainView);
        } else {
            initFragmentUI(mainView);
        }

        return mainView;
    }

    private void onShowAlertClassName(final View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.profile_alert_detail);

        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(10, 0, 10, 0);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton(R.string.formula_ok,
                (dialog, which) -> {
                    String str_value = input.getText().toString();
                    if (!Global.isCheckSpelling(str_value) || str_value.length() == 0) {
                        onShowAavailableAlert(view);
                        return;
                    }
                    onRegisterClassName(view, str_value);
                });

        alertDialog.show();
    }

    private void onShowAavailableAlert(final View view) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.alert_waring_title)
                .setMessage(R.string.alert_para_able_detail)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    // Continue with delete operation
                    onShowAlertClassName(view);
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void onRegisterClassName(final View view, String name) {
        OkHttpUtils.get().url(AppConstant.UPDATE_USER)
                .addParams("id", Global.gUserInfo.id)
                .addParams("classname", name)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Toast.makeText(getActivity(), R.string.alert_error_internet_detail, Toast.LENGTH_SHORT).show();
                        Global.showOtherActivity(getActivity(), LoginActivity.class, 1);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Global.gSelParams.clear();
                        try {
                            JSONObject obj = new JSONObject(response);
                            int ret = obj.getInt("ret");
                            switch (ret) {
                                case 10000:
                                    Global.gUserInfo.classname = obj.getString("result");
                                    initFragmentUI(view);
                                    break;
                                case 10001:
                                    onShowAlertClassName(view);
                                    break;
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), R.string.alert_server_error_detail, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initFragmentUI(View mainView) {
        MainActivity activity = (MainActivity) getActivity();
        activity.lbl_title.setText(R.string.menu_profile);
        activity.llt_add.setVisibility(View.GONE);
        activity.llt_search.setVisibility(View.INVISIBLE);

        llt_payed = mainView.findViewById(R.id.llt_profile_pay);
        llt_payed.setOnClickListener(v -> Global.showOtherActivity(getActivity(), WechatPayActivity.class, 0));

        llt_comment = mainView.findViewById(R.id.llt_profile_comment);
        llt_comment.setOnClickListener(v -> Global.showOtherActivity(getActivity(), CommentActivity.class, 0));

        llt_report = mainView.findViewById(R.id.llt_profile_report);
        llt_report.setOnClickListener(v -> Global.showOtherActivity(getActivity(), HelpActivity.class, 0));

        llt_guide = mainView.findViewById(R.id.llt_profile_guide);
        llt_guide.setOnClickListener(v -> Global.showOtherActivity(getActivity(), GuideActivity.class, 0));

        llt_support = mainView.findViewById(R.id.llt_profile_support);
        llt_support.setOnClickListener(v -> Global.showOtherActivity(getActivity(), SupportActivity.class, 0));

        llt_logout = mainView.findViewById(R.id.llt_profile_logout);
        llt_logout.setOnClickListener(v -> {
            WXUserInfo.WxUserInitialize();
            Global.showOtherActivity(getActivity(), LoginActivity.class, 1);

        });

        WXUserInfo info = Global.gUserInfo.wxUserInfo;

        img_avatar = mainView.findViewById(R.id.img_profile_avatar);
        Glide.with(getActivity()).load(info.getHeadimgurl()).into(img_avatar);
//        Toast.makeText(getActivity(), info.getHeadimgurl(), Toast.LENGTH_SHORT).show();
        lbl_name = mainView.findViewById(R.id.lbl_profile_name);
        lbl_name.setText(info.getNickname());
        lbl_location = mainView.findViewById(R.id.lbl_profile_location);
        lbl_location.setText(info.getCity() + " " + info.getProvince() + ", " + info.getCountry());
        lbl_payed = mainView.findViewById(R.id.lbl_profile_payed);
        String str_payed;
        if (Global.gUserInfo.payed > 50000) {
            str_payed = String.format("%.1f", (float) Global.gUserInfo.payed / 100000.0f) + "K";
        } else if (Global.gUserInfo.payed > 10000) {
            str_payed = String.valueOf(Global.gUserInfo.payed / 100);
        } else {
            str_payed = String.format("%.1f", (float) Global.gUserInfo.payed / 100.0f);
        }
        lbl_payed.setText(str_payed);
        lbl_func = mainView.findViewById(R.id.lbl_profile_func);
        lbl_func.setText(String.valueOf(Global.gUserInfo.cnt_func));
        lbl_rfunc = mainView.findViewById(R.id.lbl_profile_rfunc);
        lbl_rfunc.setText(String.valueOf(Global.gUserInfo.cnt_restfunc));
        lbl_update = mainView.findViewById(R.id.lbl_profile_regdate);
        lbl_update.setText(String.valueOf(Global.gUserInfo.regDate));
        lbl_classname = mainView.findViewById(R.id.lbl_profile_classname);
        lbl_classname.setText(String.valueOf(Global.gUserInfo.classname));
    }

}
