package com.shindev.rulecalculator.adapters;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shindev.rulecalculator.CalcActivity;
import com.shindev.rulecalculator.R;
import com.shindev.rulecalculator.common.ParamInfo;

import java.util.ArrayList;

public class ValueAdapter  extends BaseAdapter {

    private CalcActivity mActivity;
    private ArrayList<String[]> mDatas;
    private ArrayList<ParamInfo> mParams;

    public ValueAdapter (CalcActivity activity, ArrayList<ParamInfo> params, ArrayList<String[]> datas) {
        this.mActivity = activity;
        this.mDatas = datas;
        this.mParams = params;
    }

    @Override
    public int getCount() {
        return (mDatas.size() + 1);
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(mActivity).inflate(R.layout.item_value, null);

        LinearLayout llt_main = view.findViewById(R.id.llt_value_list);

        if (position == 0) {
            for (int i = 0; i < mParams.size(); i++) {
                ParamInfo param = mParams.get(i);

                TextView textView = new TextView(mActivity);
                textView.setText(param.name);
                textView.setTextColor(mActivity.getColor(R.color.txt_black));
                textView.setTextSize((float) 15.0);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        150, LinearLayout.LayoutParams.MATCH_PARENT
                );
                textView.setGravity(Gravity.CENTER);

                if (i == mParams.size() - 1) {
                    params.setMargins(10, 0, 10, 0);
                } else {
                    params.setMargins(10, 0, 0, 0);
                }
                textView.setLayoutParams(params);

                llt_main.addView(textView);
            }
        } else {
            for (int i = 0; i < mDatas.get(position - 1).length; i++) {
                String value = mDatas.get(position - 1)[i];

                TextView textView = new TextView(mActivity);
                textView.setText(value);
                textView.setTextColor(mActivity.getColor(R.color.txt_black));
                textView.setTextSize((float) 15.0);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        150, LinearLayout.LayoutParams.MATCH_PARENT
                );
                textView.setGravity(Gravity.CENTER);

                if (i == mParams.size() - 1) {
                    params.setMargins(10, 0, 10, 0);
                } else {
                    params.setMargins(10, 0, 0, 0);
                }
                textView.setLayoutParams(params);

                llt_main.addView(textView);
            }

            llt_main.setOnClickListener(v -> mActivity.onEventValueDelete(position - 1));
        }

        return view;
    }
}
