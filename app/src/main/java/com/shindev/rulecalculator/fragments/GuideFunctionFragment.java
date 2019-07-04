package com.shindev.rulecalculator.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.shindev.rulecalculator.MainActivity;
import com.shindev.rulecalculator.ParameterActivity;
import com.shindev.rulecalculator.R;
import com.shindev.rulecalculator.common.Global;

public class GuideFunctionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_guide_function, container, false);
        initFragmentUI(mainView);
        return mainView;
    }

    private void initFragmentUI (View view) {
        MainActivity activity = (MainActivity) getActivity();
        activity.lbl_title.setText(R.string.menu_create_function);
        activity.llt_add.setVisibility(View.INVISIBLE);
        activity.llt_search.setVisibility(View.GONE);

        Button btn_next = view.findViewById(R.id.btn_gfunc_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.showOtherActivity(getActivity(), ParameterActivity.class, 0);
            }
        });
    }
}
