package com.shindev.rulecalculator.listener;

import android.view.View;
import android.widget.EditText;

import com.shindev.rulecalculator.fragments.CalcFragment;

public class ActionListener implements View.OnClickListener {

    private CalcFragment fragment;
    private EditText calcText;
    private String action;

    public ActionListener(CalcFragment fragment, EditText calcText, String action){
        this.fragment = fragment;
        this.calcText = calcText;
        this.action = action;
    }

    @Override
    public void onClick(View v) {

        //Calculator text is empty so handle corner case for pi and e.
        if(calcText!= null && calcText.getText().toString().isEmpty()) {
            if (action.equals("π")) {
                if(fragment.operationValueOne == 0)
                    fragment.operationValueOne = Math.PI;
                else
                    fragment.operationValueTwo = Math.PI;

                calcText.setText(Double.toString(Math.PI));
            }
            if (action.equals("e")) {
                if(fragment.operationValueOne == 0)
                    fragment.operationValueOne = Math.E;
                else
                    fragment.operationValueTwo = Math.E;

                calcText.setText(Double.toString(Math.E));
            }
        }
        //Update operation values and the edit text field depending on the action.
        if (calcText != null && !calcText.getText().toString().isEmpty()) {
            double value = Double.parseDouble(calcText.getText().toString());
            switch (action) {
                //Action - Clearing calculator text field
                case "C":
                    calcText.setText("");
                    fragment.operationValueOne = 0;
                    fragment.operationValueTwo = 0;
                    calcText.setText("");
                    break;
                //Action - Change sign of the number
                case "±":
                    if (value == fragment.operationValueOne) fragment.operationValueOne *= -1;
                    if (value == fragment.operationValueTwo) fragment.operationValueTwo *= -1;
                    if (value % 1 == 0)
                        calcText.setText(Integer.toString((int) (value * -1)));
                    else
                        calcText.setText(Double.toString(value * -1));
                    break;
                //Action - Change number to its percent
                case "%":
                    if (value == fragment.operationValueOne) fragment.operationValueOne *= 0.01;
                    if (value == fragment.operationValueTwo) fragment.operationValueTwo *= 0.01;

                    calcText.setText(Double.toString(value * 0.01));
                    break;
                //Action - Add a decimal to the calculator text field
                case ".":
                    String currCalculation = calcText.getText().toString();
                    if (currCalculation.charAt(currCalculation.length() - 1) != '.')
                        currCalculation += '.';
                    calcText.setText(currCalculation);
                    break;
                //Action - Get square root of the number
                case "√":
                    if (value == fragment.operationValueOne)
                        fragment.operationValueOne = Math.sqrt(fragment.operationValueOne);
                    if (value == fragment.operationValueTwo)
                        fragment.operationValueTwo = Math.sqrt(fragment.operationValueTwo);

                    value = Math.sqrt(value);

                    if (value % 1 == 0)
                        calcText.setText(Integer.toString((int) (value)));
                    else
                        calcText.setText(Double.toString(value));
                    break;
                //Action - Get square of the number
                case "X²":
                    if (value == fragment.operationValueOne)
                        fragment.operationValueOne *= fragment.operationValueOne;
                    if (value == fragment.operationValueTwo)
                        fragment.operationValueTwo *= fragment.operationValueTwo;

                    value *= value;

                    if (value % 1 == 0)
                        calcText.setText(Integer.toString((int) (value)));
                    else
                        calcText.setText(Double.toString(value));
                    break;
                //Action - Get cube of the number
                case "X³":
                    if (value == fragment.operationValueOne)
                        fragment.operationValueOne *= fragment.operationValueOne * fragment.operationValueOne;
                    if (value == fragment.operationValueTwo)
                        fragment.operationValueTwo *= fragment.operationValueTwo * fragment.operationValueTwo;

                    value *= value * value;

                    if (value % 1 == 0)
                        calcText.setText(Integer.toString((int) (value)));
                    else
                        calcText.setText(Double.toString(value));
                    break;
                //Action - Get natural log of the number
                case "ln":
                    if (value == fragment.operationValueOne)
                        fragment.operationValueOne = Math.log(fragment.operationValueOne);
                    if (value == fragment.operationValueTwo)
                        fragment.operationValueTwo = Math.log(fragment.operationValueTwo);

                    value = Math.log(value);

                    if (value % 1 == 0)
                        calcText.setText(Integer.toString((int) (value)));
                    else
                        calcText.setText(Double.toString(value));
                    break;
                //Action - Get common log of the number
                case "log10":
                    if (value == fragment.operationValueOne)
                        fragment.operationValueOne = Math.log10(fragment.operationValueOne);
                    if (value == fragment.operationValueTwo)
                        fragment.operationValueTwo = Math.log10(fragment.operationValueTwo);

                    value = Math.log10(value);

                    if (value % 1 == 0)
                        calcText.setText(Integer.toString((int) (value)));
                    else
                        calcText.setText(Double.toString(value));
                    break;
                //Action - Get sine of the number.
                case "sin(x)":
                    if (value == fragment.operationValueOne)
                        fragment.operationValueOne = Math.sin(fragment.operationValueOne);
                    if (value == fragment.operationValueTwo)
                        fragment.operationValueTwo = Math.sin(fragment.operationValueTwo);

                    value = Math.sin(value);

                    if (value % 1 == 0)
                        calcText.setText(Integer.toString((int) (value)));
                    else
                        calcText.setText(Double.toString(value));
                    break;
                //Action - Get cosine of the number.
                case "cos(x)":
                    if (value == fragment.operationValueOne)
                        fragment.operationValueOne = Math.cos(fragment.operationValueOne);
                    if (value == fragment.operationValueTwo)
                        fragment.operationValueTwo = Math.cos(fragment.operationValueTwo);

                    value = Math.cos(value);

                    if (value % 1 == 0)
                        calcText.setText(Integer.toString((int) (value)));
                    else
                        calcText.setText(Double.toString(value));
                    break;
                //Action - Get tangent of the number.
                case "tan(x)":
                    if (value == fragment.operationValueOne)
                        fragment.operationValueOne = Math.tan(fragment.operationValueOne);
                    if (value == fragment.operationValueTwo)
                        fragment.operationValueTwo = Math.tan(fragment.operationValueTwo);

                    value = Math.tan(value);

                    if (value % 1 == 0)
                        calcText.setText(Integer.toString((int) (value)));
                    else
                        calcText.setText(Double.toString(value));
                    break;
                //Action - Replace number with pi.
                case "π":
                    if (value == fragment.operationValueOne)
                        fragment.operationValueOne = Math.PI;
                    if (value == fragment.operationValueTwo)
                        fragment.operationValueTwo = Math.PI;

                    value = Math.PI;

                    if (value % 1 == 0)
                        calcText.setText(Integer.toString((int) (value)));
                    else
                        calcText.setText(Double.toString(value));
                    break;
                //Action - Replace number with e.
                case "e":
                    if (value == fragment.operationValueOne)
                        fragment.operationValueOne = Math.E;
                    if (value == fragment.operationValueTwo)
                        fragment.operationValueTwo = Math.E;

                    value = Math.E;

                    if (value % 1 == 0)
                        calcText.setText(Integer.toString((int) (value)));
                    else
                        calcText.setText(Double.toString(value));
                    break;
            }
        }
    }
}
