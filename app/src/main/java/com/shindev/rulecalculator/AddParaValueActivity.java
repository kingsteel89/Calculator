package com.shindev.rulecalculator;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.shindev.rulecalculator.adapters.AddAdapter;
import com.shindev.rulecalculator.common.Global;

public class AddParaValueActivity extends AppCompatActivity {

    private AddAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_para_value);

        initUIView();
    }

    private void initUIView() {
        Global.gParamValues.clear();

        ListView lst_value = findViewById(R.id.lst_add_para);
        mAdapter = new AddAdapter(this, Global.gSelParams, Global.gParamValues);
        lst_value.setAdapter(mAdapter);
    }

    public void onClickBackIcon(View view) {
        Global.gParamValues.clear();
        Global.showOtherActivity(this, CalcActivity.class, 1);
    }

    public void onClickLltAddValue(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_custom, null);
        dialogBuilder.setView(dialogView);

        EditText editText = dialogView.findViewById(R.id.alert_txt_content);

        dialogBuilder.setTitle(R.string.alert_custom_title)
                .setMessage(R.string.alert_custom_detail)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Continue with delete operation
                    String str_values = editText.getText().toString();
                    String[] str_value = str_values.split(",");
                    if (str_value.length == Global.gSelParams.size()) {
                        Global.gParamValues.add(str_value);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, R.string.alert_custom_failed, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    public void onClickSaveBtn(View view) {
        if (Global.gParamValues.size() == 0) {
            Toast.makeText(this, R.string.add_para_save_wrong, Toast.LENGTH_SHORT).show();
            return;
        }
        Global.showOtherActivity(this, CalcActivity.class, 1);
    }

    public void onEventValueDelete(int index) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.calc_alert_delete_title)
                .setMessage(R.string.calc_alert_delete_detail)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Continue with delete operation
                    Global.gParamValues.remove(index);
                    mAdapter.notifyDataSetChanged();
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
