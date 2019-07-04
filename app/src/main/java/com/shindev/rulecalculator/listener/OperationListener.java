package com.shindev.rulecalculator.listener;

import android.view.View;
import android.widget.EditText;

import com.shindev.rulecalculator.fragments.CalcFragment;


public class OperationListener implements View.OnClickListener  {
    EditText calcText;
    String operation;
    CalcFragment fragment;

    public OperationListener(CalcFragment fragment, EditText calcText, String operation){
        this.fragment = fragment;
        this.calcText = calcText;
        this.operation = operation;
    }

    @Override
    public void onClick(View v) {

        if(calcText != null){
            if(!operation.equals("=")) {
                String currCalculation = calcText.getText().toString();

                if(!currCalculation.isEmpty()) {
                    fragment.operationValueOne = Double.parseDouble(currCalculation);
                }
                fragment.operation = operation;
                fragment.solutionVisible = false;

               calcText.setText("");
            }
            else{
                double solution = 0;
                double valueOne = fragment.operationValueOne;
                double valueTwo = fragment.operationValueTwo;

                switch (fragment.operation){
                    case "+": solution = valueOne + valueTwo; break;
                    case "–": solution = valueOne - valueTwo; break;
                    case "×": solution = valueOne * valueTwo; break;
                    case "÷": solution = valueOne / valueTwo; break;
                }
                fragment.operationValueOne = solution;
                fragment.solutionVisible = true;
                if(solution % 1 == 0) calcText.setText(Integer.toString((int)solution));
                else calcText.setText(Double.toString(solution));
            }
        }
    }
}
