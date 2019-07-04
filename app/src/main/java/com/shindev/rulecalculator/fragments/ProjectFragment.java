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

import com.shindev.rulecalculator.LoginActivity;
import com.shindev.rulecalculator.MainActivity;
import com.shindev.rulecalculator.ProjectDetailActivity;
import com.shindev.rulecalculator.R;
import com.shindev.rulecalculator.adapters.ProjectAdapter;
import com.shindev.rulecalculator.common.AppConstant;
import com.shindev.rulecalculator.common.FunctionItem;
import com.shindev.rulecalculator.common.Global;
import com.shindev.rulecalculator.common.ProjectItem;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProjectFragment extends Fragment {

    private ListView lst_project;
    private ProjectAdapter mAdapter;
    private ArrayList<ProjectItem> ary_project = new ArrayList<>();
    private ArrayList<ProjectItem> ary_show = new ArrayList<>();

    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_project, container, false);
        initFragmentUI(mainView);
        return mainView;
    }

    private void initFragmentUI(View view) {
        MainActivity activity = (MainActivity) getActivity();
        activity.lbl_title.setText(R.string.menu_project_list);
        activity.llt_add.setVisibility(View.GONE);
        activity.llt_search.setVisibility(View.VISIBLE);

        lst_project = view.findViewById(R.id.lst_project_item);
        lst_project.setOnItemClickListener((parent, view1, position, id) -> {
            Global.gSelProject = ary_show.get(position);
            Global.showOtherActivity(getActivity(), ProjectDetailActivity.class, 0);
        });

        dialog = ProgressDialog.show(activity, getString(R.string.progress_title), getString(R.string.progress_detail));

        initDatas();
    }

    private void initDatas() {
        OkHttpUtils.get().url(AppConstant.GETPROJECT)
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

                                    ary_project.clear();
                                    ary_show.clear();

                                    if (result.length() > 0) {
                                        for (int i = 0; i < result.length(); i++) {
                                            JSONObject json = result.getJSONObject(i);

                                            ProjectItem item = new ProjectItem();
                                            item.initialWithJson(json);

                                            ary_project.add(item);
                                        }
                                        ary_show = (ArrayList<ProjectItem>) ary_project.clone();
                                        initListView();
                                    }
                                    break;
                                case 10001:
                                    Toast.makeText(getActivity(), R.string.list_empty_project, Toast.LENGTH_SHORT).show();
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
        mAdapter = new ProjectAdapter(getContext(), ary_show);
        lst_project.setAdapter(mAdapter);
    }

    public void onEventSearchEditing(String searchKey) {
        ary_show.clear();
        for (int i = 0; i < ary_project.size(); i++ ) {
            String str_title = ary_project.get(i).name.toLowerCase();
            String str_compare = searchKey.toLowerCase();
            if (str_title.contains(str_compare)) {
                ary_show.add(ary_project.get(i));
            }
        }
        if (ary_show.size() == 0) {
            Toast.makeText(getActivity(), R.string.list_empty_project, Toast.LENGTH_SHORT).show();
        }
        mAdapter.notifyDataSetChanged();
    }
}
