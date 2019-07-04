package com.shindev.rulecalculator.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shindev.rulecalculator.R;
import com.shindev.rulecalculator.common.HistoryItem;

import java.util.ArrayList;

public class HistoryAdapter  extends BaseAdapter {

    private Context mContext;
    private ArrayList<HistoryItem> mDatas;

    public HistoryAdapter (Context context, ArrayList<HistoryItem> datas) {
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

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(mContext).inflate(R.layout.item_history, null);

        TextView lbl_name = view.findViewById(R.id.lbl_his_name);
        TextView lbl_date = view.findViewById(R.id.lbl_his_date);
        TextView lbl_data = view.findViewById(R.id.lbl_his_data);
        ImageView img_avatar = view.findViewById(R.id.img_history_avatar);

        lbl_name.setText(mDatas.get(position).name);
        String str_data = mDatas.get(position).comment;
        if (str_data.length() > 0) {
            lbl_data.setText(mDatas.get(position).comment);
        } else {
            String str_payed = String.format(mContext.getString(R.string.history_adpater_payed), String.valueOf(Float.valueOf(mDatas.get(position).payed) / 100.0f));
            lbl_data.setText( str_payed );
            lbl_data.setTextColor(mContext.getColor(R.color.colorAccent));
        }
        lbl_date.setText(mDatas.get(position).regDate);
        Glide.with(mContext).load(mDatas.get(position).avatarUrl).into(img_avatar);

        return view;
    }
}
