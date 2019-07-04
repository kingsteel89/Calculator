package com.shindev.rulecalculator.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.shindev.rulecalculator.FunctionDetailActivity;
import com.shindev.rulecalculator.LoginActivity;
import com.shindev.rulecalculator.MainActivity;
import com.shindev.rulecalculator.R;
import com.shindev.rulecalculator.adapters.FunctionAdapter;
import com.shindev.rulecalculator.common.AppConstant;
import com.shindev.rulecalculator.common.FunctionItem;
import com.shindev.rulecalculator.common.Global;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FunctionListFragment extends Fragment {

    private ListView lst_function;
    private FunctionAdapter mAdapterFunction;
    private ArrayList<FunctionItem> mFunctionDatas = new ArrayList<>();

    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_functionlist, container, false);
        initFragmentUI(mainView);

        return mainView;
    }

    private void initFragmentUI(View mainView) {
        MainActivity activity = (MainActivity) getActivity();
        activity.lbl_title.setText(R.string.menu_function_list);
        activity.llt_add.setVisibility(View.GONE);
        activity.llt_search.setVisibility(View.VISIBLE);

        dialog = ProgressDialog.show(activity, getString(R.string.progress_title), getString(R.string.progress_detail));

        initDatas();

        lst_function = mainView.findViewById(R.id.lst_function_item);
        lst_function.setOnItemClickListener((parent, view, position, id) -> {
            Global.gSelIndex = Integer.parseInt(Global.gShowFuncItems.get(position).id);
            Global.showOtherActivity(getActivity(), FunctionDetailActivity.class, 0);
        });
    }

    private void initDatas() {
        OkHttpUtils.get().url(AppConstant.GETFUNCS)
                .addParams("id_user", Global.gUserInfo.id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        Toast.makeText(getActivity(), R.string.alert_error_internet_detail, Toast.LENGTH_SHORT).show();
                        Global.showOtherActivity(getActivity(), LoginActivity.class, 1);
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
                                    JSONArray result = obj.getJSONArray("result");
                                    JSONArray params = obj.getJSONArray("param");

                                    mFunctionDatas.clear();

                                    if (result.length() > 0) {
                                        for (int i = 0; i < result.length(); i++) {
                                            JSONObject json = result.getJSONObject(i);

                                            FunctionItem item = new FunctionItem();
                                            item.initialWithJson(json);
                                            item.cnt_para = params.getInt(i);

                                            mFunctionDatas.add(item);
                                        }

                                        Global.gShowFuncItems.clear();
                                        Global.gShowFuncItems = (ArrayList<FunctionItem>) mFunctionDatas.clone();

                                        initListView();
                                    }
                                    break;
                                case 10001:
                                    Toast.makeText(getActivity(), R.string.list_empty_function, Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    String msg = obj.getString("msg");
                                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                                    break;
                            }


                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), R.string.alert_server_error_detail, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initListView() {
        mAdapterFunction = new FunctionAdapter(Global.gContext, Global.gShowFuncItems);
        lst_function.setAdapter(mAdapterFunction);
    }

    public void onEventSearchEditing(String searchKey) {
        Global.gShowFuncItems.clear();
        for (int i = 0; i < mFunctionDatas.size(); i++ ) {
            String str_title = mFunctionDatas.get(i).name.toLowerCase();
            String str_compare = searchKey.toLowerCase();
            if (str_title.contains(str_compare)) {
                Global.gShowFuncItems.add(mFunctionDatas.get(i));
            }
        }
        if (Global.gShowFuncItems.size() == 0) {
            Toast.makeText(getActivity(), R.string.list_empty_function, Toast.LENGTH_SHORT).show();
        }
        mAdapterFunction.notifyDataSetChanged();
    }

}
