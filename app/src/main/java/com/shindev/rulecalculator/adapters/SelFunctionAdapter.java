package com.shindev.rulecalculator.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shindev.rulecalculator.R;
import com.shindev.rulecalculator.common.FunctionItem;
import com.shindev.rulecalculator.common.Global;

import java.util.ArrayList;

public class SelFunctionAdapter  extends BaseAdapter {

    private Context mContext;
    private ArrayList<FunctionItem> mDatas;

    public SelFunctionAdapter (Context context, ArrayList<FunctionItem> datas) {
        this.mContext = context;
        this.mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
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
    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(mContext).inflate(R.layout.item_sel_function, null);

        TextView txt_name = view.findViewById(R.id.lbl_item_name);
        txt_name.setText(mDatas.get(position).name);

        TextView txt_date = view.findViewById(R.id.lbl_item_date);
        txt_date.setText(mDatas.get(position).regdate);

        LinearLayout llt_check = view.findViewById(R.id.llt_list_item_check);
        if (!mDatas.get(position).isSelected) {
            llt_check.setVisibility(View.INVISIBLE);
        }

        LinearLayout llt_param = view.findViewById(R.id.llt_item_param);
        LinearLayout llt_spec = view.findViewById(R.id.llt_item_spec);
        if (Global.gFuncParams != mDatas.get(position).getCnt_para()) {
            Global.gFuncParams = mDatas.get(position).getCnt_para();
            TextView txt_param = view.findViewById(R.id.txt_item_param);
            txt_param.setText(String.format(mContext.getString(R.string.func_item_param), Global.gFuncParams));
            llt_spec.setVisibility(View.GONE);
        } else {
            llt_param.setVisibility(View.GONE);
        }

        if (position == mDatas.size() - 1) {
            Global.gFuncParams = 0;
        }

        return view;
    }
}
