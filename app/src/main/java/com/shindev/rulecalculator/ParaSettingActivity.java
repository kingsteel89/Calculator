package com.shindev.rulecalculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shindev.rulecalculator.common.CreateParaItem;
import com.shindev.rulecalculator.common.Global;

import static com.shindev.rulecalculator.common.Global.gSelIndex;

public class ParaSettingActivity extends AppCompatActivity {

    TextView lbl_title;
    EditText txt_name, txt_desc;
    LinearLayout llt_delete;
    Button btn_set;

    boolean isAdd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpara);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.txt_black, this.getTheme()));
        } else {
            getWindow().setStatusBarColor(getColor(R.color.txt_black));
        }

        initUIView();
    }

    private void initUIView() {
        if (Global.gSelParaItem == null) {
            isAdd = true;
        }

        lbl_title = findViewById(R.id.lbl_setpara_title);
        llt_delete = findViewById(R.id.llt_setpara_delete);
        llt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDeleteAlert();
            }
        });
        txt_name = findViewById(R.id.txt_setpara_name);
        txt_desc = findViewById(R.id.txt_setpara_desc);
        btn_set = findViewById(R.id.btn_setpara_set);
        btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateParaItem item = new CreateParaItem();
                item.id = String.valueOf(Global.gCreateParams.size());
                item.name = txt_name.getText().toString();
                if (!isCheckedValue()) {
                    onShowCheckedAlert();
                    return;
                }
                if (!Global.isCheckSpelling(item.name)) {
                    onShowAavailableAlert();
                    return;
                }
                String str_desc = txt_desc.getText().toString();
                if (str_desc.length() == 0) {
                    str_desc = getString(R.string.item_create_description);
                }
                item.description = str_desc;
                if (isAdd) {
                    Global.gCreateParams.add(item);
                } else {
                    Global.gCreateParams.set(gSelIndex, item);
                }
                onClickBackIcon(null);
            }
        });

        if (isAdd) {
            lbl_title.setText(R.string.setpara_add);
            llt_delete.setVisibility(View.INVISIBLE);
            btn_set.setText(R.string.setpara_code_add);
        } else {
            lbl_title.setText(R.string.setpara_edit);
            llt_delete.setVisibility(View.VISIBLE);
            btn_set.setText(R.string.setpara_code_edit);
            txt_name.setText(Global.gSelParaItem.name);
            txt_desc.setText(Global.gSelParaItem.description);
        }
    }

    private void onShowAavailableAlert() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.alert_waring_title)
                .setMessage(R.string.alert_para_able_detail)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void onShowCheckedAlert() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.alert_waring_title)
                .setMessage(R.string.setpara_alert_wrong)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private boolean isCheckedValue() {
        String name = txt_name.getText().toString();
        if (name.length() == 0) {
            return false;
        }
        for (int i = 0; i < Global.gCreateParams.size(); i++) {
            CreateParaItem item = Global.gCreateParams.get(i);
            if (item.name.equals(name)) {
                return false;
            }
        }
        return true;
    }

    private void onClickDeleteAlert() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.setpara_alert_delete)
                .setMessage(R.string.setpara_alert_deletedetail)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        Global.gCreateParams.remove(gSelIndex);
                        onClickBackIcon(null);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void onClickBackIcon(View view) {
        Global.showOtherActivity(this, ParameterActivity.class, 1);
    }

}
