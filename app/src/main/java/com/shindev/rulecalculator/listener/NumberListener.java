package com.shindev.rulecalculator.listener;

import android.view.View;
import android.widget.EditText;

import com.shindev.rulecalculator.fragments.CalcFragment;

public class NumberListener implements View.OnClickListener {
    private CalcFragment fragment;
    private EditText calcText;
    private long number;


    public NumberListener(CalcFragment fragment, EditText calcText, long number){
        this.fragment = fragment;
        this.calcText = calcText;
        this.number = number;
    }


    @Override
    public void onClick(View v) {
        if(calcText != null){
            //Handle case if the solution is on screen ie don't add anything to it.
            if(fragment.solutionVisible){
                fragment.solutionVisible = false;
                fragment.operationValueOne = number;
                fragment.operationValueTwo = 0;
                fragment.operation = "";

                calcText.setText(Long.toString(number));
            }
            else {
                String currCalculation = calcText.getText().toString();

                currCalculation += number;
                calcText.setText(currCalculation);

                if (!fragment.operation.isEmpty()) {
                    fragment.operationValueTwo = Double.parseDouble(currCalculation);
                }
            }

        }
    }
}
