package com.shindev.rulecalculator;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shindev.rulecalculator.common.AppConstant;
import com.shindev.rulecalculator.common.CreateParaItem;
import com.shindev.rulecalculator.common.FunctionItem;
import com.shindev.rulecalculator.common.Global;
import com.shindev.rulecalculator.common.ParamInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.shindev.rulecalculator.common.Global.gAlgorithm;

public class FormulaActivity extends AppCompatActivity implements View.OnClickListener {

    TextView lbl_formula;
    LinearLayout llt_params, llt_next, llt_key;

    ArrayList<Button> ary_btn_params = new ArrayList<>();
    //btn tag 7
    Button btn_p1, btn_p2, btn_p3, btn_p4, btn_p5, btn_p6, btn_p7;
    //number tag 10
    Button btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9;
    //operate tag 4
    Button btn_plus, btn_minu, btn_multi, btn_divide;
    //logical tag 3
    Button btn_if, btn_less, btn_more;
    //bundle tag 4
    Button btn_01_open, btn_02_open, btn_01_close, btn_02_close;
    //other tag 7
    Button btn_back, btn_clear, btn_enter, btn_space, btn_result, btn_equal, btn_point;

    boolean isResult = false;
    boolean isPoint = false;
    boolean isEqual = false;
    boolean isSpace = false;
    boolean isOperate = false;
    boolean isShowLogical = false;
    boolean isEndLogical = false;
    boolean isBundle = false;
    boolean isEndBundle = false;
    boolean isIf = false;

    private enum FormulaStatu {
        Begin,
        Body,
        Ended,
        Logical
    }

    private enum KeyBoardStatu {
        None,
        Param,
        Space,
        Point,
        Operate,
        If_Operate,
        Number
    }

    FormulaStatu calcStatu = FormulaStatu.Begin;
    KeyBoardStatu keyStatu = KeyBoardStatu.None;
    KeyBoardStatu beforeKeyStatu = KeyBoardStatu.None;

    private String str_formula = "";

    @Override
    public void onClick(View v) {
        Button btn = (Button) v;
        String str_tag = (String) btn.getTag();
        String str_value = btn.getText().toString();

        switch (str_tag) {
            case "space":
                str_formula = str_formula + " ";
                lbl_formula.setText(str_formula);

                isSpace = false;
                isPoint = false;
                keyStatu = KeyBoardStatu.Space;

                initKeyBoard();
                break;
            case "equal":
                str_formula = str_formula + str_value;
                if (calcStatu == FormulaStatu.Logical) {
                    beforeKeyStatu = KeyBoardStatu.If_Operate;
                    str_formula = str_formula + str_value;
                    isShowLogical = true;
                    isOperate = false;
                } else {
                    isEqual = false;
                    calcStatu = FormulaStatu.Body;
                }

                lbl_formula.setText(str_formula);

                isSpace = true;
                initKeyBoard();
                break;
            case "param":
                str_formula = str_formula + "$" + str_value;
                lbl_formula.setText(str_formula);

                keyStatu = KeyBoardStatu.Param;
                initKeyBoard();
                break;
            case "point":
                str_formula = str_formula + str_value;
                lbl_formula.setText(str_formula);

                keyStatu = KeyBoardStatu.Point;
                initKeyBoard();
                break;
            case "result":
                str_formula = str_formula + "$" + str_value;
                lbl_formula.setText(str_formula);

                keyStatu = KeyBoardStatu.Param;
                isResult = true;
                initKeyBoard();
                break;
            case "number":
                str_formula = str_formula + str_value;
                lbl_formula.setText(str_formula);

                keyStatu = KeyBoardStatu.Number;
                initKeyBoard();
                break;
            case "operate":
                str_formula = str_formula + str_value;
                lbl_formula.setText(str_formula);

                isOperate = false;
                keyStatu = KeyBoardStatu.Operate;
                initKeyBoard();
                break;
            case "enter":
                if (calcStatu == FormulaStatu.Logical || isEndBundle) {
                    str_formula = str_formula + " \n";
                } else {
                    str_formula = str_formula + "; \n";
                }

                lbl_formula.setText(str_formula);

                isOperate = false;
                isSpace = false;
                isPoint = false;
                isEqual = false;
                isEndBundle = false;

                keyStatu = KeyBoardStatu.None;
                calcStatu = FormulaStatu.Begin;

                if (isResult) {
                    llt_next.setVisibility(View.VISIBLE);
                    llt_key.setVisibility(View.GONE);
                    calcStatu = FormulaStatu.Ended;
                }

                initKeyBoard();
                break;
            case "bundle":
                str_formula = str_formula + str_value;
                lbl_formula.setText(str_formula);

                if (btn.equals(btn_01_open)) {
                    isIf = false;
                }

                if (btn.equals(btn_02_open)) {
                    isBundle = true;
                }

                if (btn.equals(btn_02_close)) {
                    isEndBundle = true;
                    isBundle = false;
                }

                if (btn.equals(btn_01_close)) {
                    isEndLogical = true;
                    if (calcStatu == FormulaStatu.Logical) {
                        isOperate = false;
                    }
                }

                isSpace = true;
                initKeyBoard();
                break;
            case "logical":
                str_formula = str_formula + str_value;
                lbl_formula.setText(str_formula);

                if (btn.equals(btn_if)) {
                    calcStatu = FormulaStatu.Logical;
                    isShowLogical = false;
                    isEndLogical = false;
                    isBundle = false;
                    isEndBundle = false;
                    isIf = true;
                } else {
                    beforeKeyStatu = KeyBoardStatu.If_Operate;
                    isShowLogical = true;
                    isOperate = false;
                }

                isSpace = true;
                initKeyBoard();
                break;
            case "back":
                onClickBackKeyboard();
                break;
        }

    }

    private void onClickBackKeyboard() {
        String[] ary_formula = str_formula.split("\n");
        if (ary_formula.length == 0) {
            return;
        }
        String str_new = ary_formula[0];
        for (int i = 1; i < ary_formula.length - 1; i++) {
            str_new = str_new + "\n" + ary_formula[i];
        }
        str_formula = str_new;

        String str_last = ary_formula[ary_formula.length - 1];
        if (str_last.contains("$Re")) {
            isResult = false;
        }

        str_formula = str_formula + "\n";
        lbl_formula.setText(str_formula);

        isOperate = false;
        isSpace = false;
        isPoint = false;
        isEqual = false;

        keyStatu = KeyBoardStatu.None;
        calcStatu = FormulaStatu.Begin;

        initKeyBoard();
    }

    private void initKeyBoard() {
        for (int i = 0; i < ary_btn_params.size(); i++) {
            Button btn = ary_btn_params.get(i);
            btn.setVisibility(View.INVISIBLE);
        }

        if (isSpace) {
            btn_space.setVisibility(View.VISIBLE);
            return;
        }

        if (isEqual) {
            btn_equal.setVisibility(View.VISIBLE);
            return;
        }

        if (isOperate) {
            for (int i = 0; i < ary_btn_params.size(); i++) {
                Button btn = ary_btn_params.get(i);
                String str_tag = (String) btn.getTag();
                if (str_tag.equals("operate")) {
                    btn.setVisibility(View.VISIBLE);
                }
            }
            btn_enter.setVisibility(View.VISIBLE);
            btn_01_close.setVisibility(View.VISIBLE);
            return;
        }

        switch (calcStatu) {
            case Logical:
                onKeyClickLogical();
                break;
            case Begin:
                onKeyClickBegin();
                break;
            case Body:
                onKeyClickBody();
                break;
            case Ended:
                return;
        }

        if (str_formula.length() > 0) {
            btn_back.setVisibility(View.VISIBLE);
            btn_clear.setVisibility(View.VISIBLE);
        }
    }

    private void onKeyClickLogical() {
        switch (keyStatu) {
            case None:
                for (int i = 0; i < ary_btn_params.size(); i++) {
                    Button btn = ary_btn_params.get(i);
                    String str_tag = (String) btn.getTag();
                    if (str_tag.equals("param")) {
                        btn.setVisibility(View.VISIBLE);
                    }
                    if (str_tag.equals("number")) {
                        btn.setVisibility(View.VISIBLE);
                    }
                }
                btn_01_open.setVisibility(View.VISIBLE);
                break;
            case Param:
                beforeKeyStatu = keyStatu;
                isSpace = true;
                initKeyBoard();
                break;
            case Number:
                beforeKeyStatu = keyStatu;
                for (int i = 0; i < ary_btn_params.size(); i++) {
                    Button btn = ary_btn_params.get(i);
                    String str_tag = (String) btn.getTag();
                    if (str_tag.equals("number")) {
                        btn.setVisibility(View.VISIBLE);
                    }
                }
                btn_space.setVisibility(View.VISIBLE);
                if (!isPoint) {
                    btn_point.setVisibility(View.VISIBLE);
                }
                break;
            case Space:
                isSpace = false;
                if (isIf) {
                    btn_01_open.setVisibility(View.VISIBLE);
                } else if (isBundle || isEndBundle) {
                    btn_enter.setVisibility(View.VISIBLE);
                } else if (isEndLogical) {
                    btn_02_open.setVisibility(View.VISIBLE);
                } else if (beforeKeyStatu == KeyBoardStatu.Number || beforeKeyStatu == KeyBoardStatu.Param) {
                    isOperate = true;
                    initKeyBoard();
                    if (!isShowLogical) {
                        btn_equal.setVisibility(View.VISIBLE);
                        btn_less.setVisibility(View.VISIBLE);
                        btn_more.setVisibility(View.VISIBLE);
                    }
                } else {
                    keyStatu = KeyBoardStatu.None;
                    initKeyBoard();
                }
                break;
            case Operate:
                beforeKeyStatu = KeyBoardStatu.None;
                isSpace = true;

                initKeyBoard();
                break;
            case Point:
                isPoint = true;
                keyStatu = KeyBoardStatu.Number;

                initKeyBoard();
                break;
        }
    }

    private void onKeyClickBody() {
        switch (keyStatu) {
            case None:
                for (int i = 0; i < ary_btn_params.size(); i++) {
                    Button btn = ary_btn_params.get(i);
                    String str_tag = (String) btn.getTag();
                    if (str_tag.equals("param")) {
                        btn.setVisibility(View.VISIBLE);
                    }
                    if (str_tag.equals("number")) {
                        btn.setVisibility(View.VISIBLE);
                    }
                }
                btn_01_open.setVisibility(View.VISIBLE);
                break;
            case Param:
                beforeKeyStatu = keyStatu;
                isSpace = true;
                initKeyBoard();
                break;
            case Number:
                beforeKeyStatu = keyStatu;
                for (int i = 0; i < ary_btn_params.size(); i++) {
                    Button btn = ary_btn_params.get(i);
                    String str_tag = (String) btn.getTag();
                    if (str_tag.equals("number")) {
                        btn.setVisibility(View.VISIBLE);
                    }
                }
                btn_space.setVisibility(View.VISIBLE);
                if (!isPoint) {
                    btn_point.setVisibility(View.VISIBLE);
                }
                break;
            case Space:
                isSpace = false;
                if (beforeKeyStatu == KeyBoardStatu.Number || beforeKeyStatu == KeyBoardStatu.Param) {
                    isOperate = true;
                    initKeyBoard();
                } else {
                    keyStatu = KeyBoardStatu.None;
                    initKeyBoard();
                }
                break;
            case Point:
                isPoint = true;
                keyStatu = KeyBoardStatu.Number;

                initKeyBoard();
                break;
            case Operate:
                beforeKeyStatu = KeyBoardStatu.None;
                isSpace = true;
                initKeyBoard();
                break;
        }
    }

    private void onKeyClickBegin() {
        switch (keyStatu) {
            case None:
                for (int i = 0; i < ary_btn_params.size(); i++) {
                    Button btn = ary_btn_params.get(i);
                    String str_tag = (String) btn.getTag();
                    if (str_tag.equals("param")) {
                        btn.setVisibility(View.VISIBLE);
                    }
                }
                btn_result.setVisibility(View.VISIBLE);
                btn_if.setVisibility(View.VISIBLE);
                if (isBundle) {
                    btn_02_close.setVisibility(View.VISIBLE);
                }
                break;
            case Param:
                isSpace = true;
                initKeyBoard();
                break;
            case Space:
                if (isEndBundle) {
                    btn_enter.setVisibility(View.VISIBLE);
                } else {
                    isEqual = true;
                    initKeyBoard();
                }
                break;
        }
        beforeKeyStatu = keyStatu;
    }

    public void onClickBtnTest (View view) {
        gAlgorithm = lbl_formula.getText().toString();
        Global.showOtherActivity(this, TestAlgorithmActivity.class, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formula);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.txt_black, this.getTheme()));
        } else {
            getWindow().setStatusBarColor(getColor(R.color.txt_black));
        }

        initUIView();
    }

    private void initUIView() {
        lbl_formula = findViewById(R.id.lbl_formula_main);
        llt_key = findViewById(R.id.llt_formula_key);
        llt_next = findViewById(R.id.llt_formula_next);

        llt_params = findViewById(R.id.llt_formula_params);

        for (int i = 0; i < Global.gCreateParams.size(); i++) {
            CreateParaItem item = Global.gCreateParams.get(i);
            //set the properties for button
            Button btnTag = new Button(this);

            btnTag.setText(item.name);
            btnTag.setTextColor(getColor(R.color.txt_white));
            btnTag.setTextSize((float) 15.0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            if (i == Global.gCreateParams.size() - 1) {
                params.setMargins(10, 0, 10, 0);
            } else {
                params.setMargins(10, 0, 0, 0);
            }
            btnTag.setLayoutParams(params);
            btnTag.setTag("param");
            btnTag.setOnClickListener(this);

            ary_btn_params.add(btnTag);
            //add button to the layout
            llt_params.addView(btnTag);
        }
        // param tag 7
        btn_p1 = findViewById(R.id.btn_formula_p1);
        btn_p2 = findViewById(R.id.btn_formula_p2);
        btn_p3 = findViewById(R.id.btn_formula_p3);
        btn_p4 = findViewById(R.id.btn_formula_p4);
        btn_p5 = findViewById(R.id.btn_formula_p5);
        btn_p6 = findViewById(R.id.btn_formula_p6);
        btn_p7 = findViewById(R.id.btn_formula_p7);
        ary_btn_params.add(btn_p1);
        ary_btn_params.add(btn_p2);
        ary_btn_params.add(btn_p3);
        ary_btn_params.add(btn_p4);
        ary_btn_params.add(btn_p5);
        ary_btn_params.add(btn_p6);
        ary_btn_params.add(btn_p7);

        //number tag 10
        btn_0 = findViewById(R.id.btn_formula_zero);
        btn_1 = findViewById(R.id.btn_formula_one);
        btn_2 = findViewById(R.id.btn_formula_two);
        btn_3 = findViewById(R.id.btn_formula_three);
        btn_4 = findViewById(R.id.btn_formula_four);
        btn_5 = findViewById(R.id.btn_formula_five);
        btn_6 = findViewById(R.id.btn_formula_six);
        btn_7 = findViewById(R.id.btn_formula_seven);
        btn_8 = findViewById(R.id.btn_formula_eight);
        btn_9 = findViewById(R.id.btn_formula_nine);
        ary_btn_params.add(btn_0);
        ary_btn_params.add(btn_1);
        ary_btn_params.add(btn_2);
        ary_btn_params.add(btn_3);
        ary_btn_params.add(btn_4);
        ary_btn_params.add(btn_5);
        ary_btn_params.add(btn_6);
        ary_btn_params.add(btn_7);
        ary_btn_params.add(btn_8);
        ary_btn_params.add(btn_9);

        //operate tag 4
        btn_plus = findViewById(R.id.btn_formula_plus);
        btn_minu = findViewById(R.id.btn_formula_minu);
        btn_multi = findViewById(R.id.btn_formula_multi);
        btn_divide = findViewById(R.id.btn_formula_divide);
        ary_btn_params.add(btn_plus);
        ary_btn_params.add(btn_minu);
        ary_btn_params.add(btn_multi);
        ary_btn_params.add(btn_divide);

        //logical tag 3
        btn_if= findViewById(R.id.btn_formula_if);
        btn_less = findViewById(R.id.btn_formula_less);
        btn_more = findViewById(R.id.btn_formula_more);
        ary_btn_params.add(btn_if);
        ary_btn_params.add(btn_less);
        ary_btn_params.add(btn_more);

        //bundle tag 4
        btn_01_open = findViewById(R.id.btn_formula_b01o);
        btn_01_close = findViewById(R.id.btn_formula_b01c);
        btn_02_open = findViewById(R.id.btn_formula_b02o);
        btn_02_close = findViewById(R.id.btn_formula_b02c);
        ary_btn_params.add(btn_01_open);
        ary_btn_params.add(btn_01_close);
        ary_btn_params.add(btn_02_open);
        ary_btn_params.add(btn_02_close);

        //other tag 7
        btn_back = findViewById(R.id.btn_formula_back);
        btn_clear = findViewById(R.id.btn_formula_clear);
        btn_enter = findViewById(R.id.btn_formula_enter);
        btn_space = findViewById(R.id.btn_formula_space);
        btn_result = findViewById(R.id.btn_formula_result);
        btn_equal = findViewById(R.id.btn_formula_equal);
        btn_point = findViewById(R.id.btn_formula_point);
        ary_btn_params.add(btn_back);
        ary_btn_params.add(btn_clear);
        ary_btn_params.add(btn_enter);
        ary_btn_params.add(btn_space);
        ary_btn_params.add(btn_result);
        ary_btn_params.add(btn_equal);
        ary_btn_params.add(btn_point);

        initKeyBoard();
    }

    public void onClickLltAdd (View view) {
        if (lbl_formula.getText().toString().length() > 0 ) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (ActivityCompat.checkSelfPermission(Global.gContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                return;
            }

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(data.getData())));
                String line;
                String str_data = "";

                while ((line = br.readLine()) != null) {
                    if (line.equals("\n")) {
                        continue;
                    }
                    str_data = str_data + line + "\n";
                }
                br.close();

                lbl_formula.setText(str_data);
                llt_next.setVisibility(View.VISIBLE);
                llt_key.setVisibility(View.GONE);
                calcStatu = FormulaStatu.Ended;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onClickBackIcon(View view) {
        Global.showOtherActivity(this, ParameterActivity.class, 1);
    }
}
