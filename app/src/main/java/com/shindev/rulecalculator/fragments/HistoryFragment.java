package com.shindev.rulecalculator.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.shindev.rulecalculator.MainActivity;
import com.shindev.rulecalculator.R;
import com.shindev.rulecalculator.adapters.HistoryAdapter;
import com.shindev.rulecalculator.common.AppConstant;
import com.shindev.rulecalculator.common.Global;
import com.shindev.rulecalculator.common.HistoryItem;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private ListView lst_function;
    private HistoryAdapter mAdapter;
    private ArrayList<HistoryItem> mHistoryDatas = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_history, container, false);
        initFragmentUI(mainView);
        return mainView;
    }

    private void initFragmentUI(View mainView) {
        MainActivity activity = (MainActivity) getActivity();
        activity.lbl_title.setText(R.string.menu_history);
        activity.llt_add.setVisibility(View.GONE);
        activity.llt_search.setVisibility(View.INVISIBLE);

        OkHttpUtils.get().url(AppConstant.GETHISTORY)
                .addParams("id_user", Global.gUserInfo.id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Toast.makeText(getActivity(), R.string.alert_error_internet_detail, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mHistoryDatas.clear();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray result = obj.getJSONArray("result");

                            for (int i = 0; i < result.length(); i++) {
                                HistoryItem item = new HistoryItem();

                                JSONObject object = result.getJSONObject(i);
                                item.initialWithJson(object);
                                mHistoryDatas.add(item);
                            }

                            mAdapter = new HistoryAdapter(Global.gContext, mHistoryDatas);
                            lst_function.setAdapter(mAdapter);
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), R.string.alert_server_error_detail, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });

        lst_function = mainView.findViewById(R.id.lst_history_item);
        lst_function.setOnItemClickListener((parent, view, position, id) -> {
            //
        });
    }

}
