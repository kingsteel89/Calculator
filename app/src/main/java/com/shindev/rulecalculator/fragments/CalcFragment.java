package com.shindev.rulecalculator.fragments;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.shindev.rulecalculator.MainActivity;
import com.shindev.rulecalculator.R;
import com.shindev.rulecalculator.listener.ActionListener;
import com.shindev.rulecalculator.listener.NumberListener;
import com.shindev.rulecalculator.listener.OperationListener;

public class CalcFragment extends Fragment {

    public EditText calcText;
    public boolean solutionVisible = false;
    public double operationValueOne;
    public double operationValueTwo;
    public String operation = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_calc, container, false);
        initFragmentUI(mainView);
        return mainView;
    }

    private void initFragmentUI(View mainView) {
        MainActivity activity = (MainActivity) getActivity();
        activity.lbl_title.setText(R.string.menu_common);
        activity.llt_add.setVisibility(View.GONE);
        activity.llt_search.setVisibility(View.INVISIBLE);

        calcText = mainView.findViewById(R.id.calc_text);
        calcText.setInputType(InputType.TYPE_NULL);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/product_sans_bold.ttf");
        calcText.setTypeface(typeface);

        setupActionListeners(mainView);
        setupOperationListeners(mainView);
        setupNumberListeners(mainView);
    }


    public void setupActionListeners(View mainView){
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/product_sans_bold.ttf");
        TextView clear =  mainView.findViewById(R.id.action_clear);
        clear.setTypeface(typeface);
        if(clear != null) clear.setOnClickListener(new ActionListener(this,calcText,"C"));

        TextView changeSign =  mainView.findViewById(R.id.action_change_sign);
        changeSign.setTypeface(typeface);
        if(changeSign != null) changeSign.setOnClickListener(new ActionListener(this,calcText,"±"));

        TextView percent =  mainView.findViewById(R.id.action_percent);
        percent.setTypeface(typeface);
        if(percent != null) percent.setOnClickListener(new ActionListener(this,calcText,"%"));

        TextView decimal =  mainView.findViewById(R.id.decimal);
        decimal.setTypeface(typeface);
        if(decimal != null) decimal.setOnClickListener(new ActionListener(this,calcText,"."));

//        TextView squareRoot =  mainView.findViewById(R.id.action_root);
//        if(squareRoot != null) squareRoot.setOnClickListener(new ActionListener(this,calcText,"√"));
//
//        TextView squared =  mainView.findViewById(R.id.action_squared);
//        if(squared != null) squared.setOnClickListener(new ActionListener(this,calcText,"X²"));
//
//        TextView cubed =  mainView.findViewById(R.id.action_cubed);
//        if(cubed != null) cubed.setOnClickListener(new ActionListener(this,calcText,"X³"));
//
//        TextView naturalLog =  mainView.findViewById(R.id.action_natural_log);
//        if(naturalLog != null) naturalLog.setOnClickListener(new ActionListener(this,calcText,"ln"));
//
//        TextView commonLog =  mainView.findViewById(R.id.action_log_base10);
//        if(commonLog != null) commonLog.setOnClickListener(new ActionListener(this,calcText,"log10"));
//
//        TextView sin =  mainView.findViewById(R.id.action_sin);
//        if(sin != null) sin.setOnClickListener(new ActionListener(this,calcText,"sin(x)"));
//
//        TextView cos =  mainView.findViewById(R.id.action_cos);
//        if(cos != null) cos.setOnClickListener(new ActionListener(this,calcText,"cos(x)"));
//
//        TextView tan =  mainView.findViewById(R.id.action_tan);
//        if(tan != null) tan.setOnClickListener(new ActionListener(this,calcText,"tan(x)"));
//
//        TextView pi =  mainView.findViewById(R.id.action_pi);
//        if(pi != null) pi.setOnClickListener(new ActionListener(this,calcText,"π"));
//
//        TextView e =  mainView.findViewById(R.id.action_e);
//        if(e != null) e.setOnClickListener(new ActionListener(this,calcText,"e"));

    }

    public void setupOperationListeners(View mainView){
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/product_sans_bold.ttf");
        TextView divide =  mainView.findViewById(R.id.divide);
        divide.setTypeface(typeface);
        if(divide != null) divide.setOnClickListener(new OperationListener(this,calcText,"÷"));

        TextView multiply =  mainView.findViewById(R.id.multiply);
        multiply.setTypeface(typeface);
        if(multiply != null) multiply.setOnClickListener(new OperationListener(this,calcText,"×"));

        TextView subtract =  mainView.findViewById(R.id.subtract);
        subtract.setTypeface(typeface);
        if(subtract != null) subtract.setOnClickListener(new OperationListener(this,calcText,"–"));

        TextView add =  mainView.findViewById(R.id.add);
        add.setTypeface(typeface);
        if(add != null) add.setOnClickListener(new OperationListener(this,calcText,"+"));

        TextView equals =  mainView.findViewById(R.id.equals);
        equals.setTypeface(typeface);
        if(equals != null) equals.setOnClickListener(new OperationListener(this,calcText,"="));
    }

    public void setupNumberListeners(View mainView){
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/product_sans_bold.ttf");
        TextView num_1  =  mainView.findViewById(R.id.one);
        num_1.setTypeface(typeface);
        if(num_1 != null) num_1.setOnClickListener(new NumberListener(this,calcText,1));

        TextView num_2  =  mainView.findViewById(R.id.two);
        num_2.setTypeface(typeface);
        if(num_2 != null) num_2.setOnClickListener(new NumberListener(this,calcText,2));

        TextView num_3  =  mainView.findViewById(R.id.three);
        num_3.setTypeface(typeface);
        if(num_3 != null) num_3.setOnClickListener(new NumberListener(this,calcText,3));

        TextView num_4  =  mainView.findViewById(R.id.four);
        num_4.setTypeface(typeface);
        if(num_4 != null) num_4.setOnClickListener(new NumberListener(this,calcText,4));

        TextView num_5  =  mainView.findViewById(R.id.five);
        num_5.setTypeface(typeface);
        if(num_5 != null) num_5.setOnClickListener(new NumberListener(this,calcText,5));

        TextView num_6  =  mainView.findViewById(R.id.six);
        num_6.setTypeface(typeface);
        if(num_6 != null) num_6.setOnClickListener(new NumberListener(this,calcText,6));

        TextView num_7  =  mainView.findViewById(R.id.seven);
        num_7.setTypeface(typeface);
        if(num_7 != null) num_7.setOnClickListener(new NumberListener(this,calcText,7));

        TextView num_8  =  mainView.findViewById(R.id.eight);
        num_8.setTypeface(typeface);
        if(num_8 != null) num_8.setOnClickListener(new NumberListener(this,calcText,8));

        TextView num_9  =  mainView.findViewById(R.id.nine);
        num_9.setTypeface(typeface);
        if(num_9 != null) num_9.setOnClickListener(new NumberListener(this,calcText,9));

        TextView num_0  =  mainView.findViewById(R.id.zero);
        num_0.setTypeface(typeface);
        if(num_0 != null) num_0.setOnClickListener(new NumberListener(this,calcText,0));

    }


}
