package com.shindev.rulecalculator;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shindev.rulecalculator.common.AppConstant;
import com.shindev.rulecalculator.common.FunctionItem;
import com.shindev.rulecalculator.common.Global;
import com.shindev.rulecalculator.common.UserInfo;
import com.shindev.rulecalculator.common.WXUserInfo;
import com.shindev.rulecalculator.fragments.CalcFragment;
import com.shindev.rulecalculator.fragments.FunctionListFragment;
import com.shindev.rulecalculator.fragments.GuideFunctionFragment;
import com.shindev.rulecalculator.fragments.GuideProjectFragment;
import com.shindev.rulecalculator.fragments.HistoryFragment;
import com.shindev.rulecalculator.fragments.ProfileFragment;
import com.shindev.rulecalculator.fragments.ProjectFragment;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private boolean isSearch = false;
    public LinearLayout llt_menu, llt_add, llt_search;
    public TextView lbl_title;
    public EditText txt_search;
    public ImageView img_add;

    private DrawerLayout draw_main;
    private ActionBarDrawerToggle tog_menu;
    private NavigationView nav_menu;
    private ImageView img_avatar;
    private TextView lbl_name, lbl_location, lbl_payed, lbl_funcs, lbl_rfuncs;

    public Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.txt_black, this.getTheme()));
        } else {
            getWindow().setStatusBarColor(getColor(R.color.txt_black));
        }

        Global.gContext = this;

        if (Global.gFlgNewLogin) {
            onEventNewLogin();
        } else {
            initUIView();
        }
    }

    private void onEventNewLogin() {
        WXUserInfo userInfo = WXUserInfo.getUserInfo();

        Global.gUserInfo = new UserInfo();
        Global.gUserInfo.wxUserInfo = userInfo;

        String str_url = AppConstant.NEWLOGIN;

        Map<String, String> params = new HashMap<>();
        params.put("openid", userInfo.getOpenid());
        params.put("nickname", userInfo.getNickname());
        params.put("headurl", userInfo.getHeadimgurl());

        OkHttpUtils.get().url(str_url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Toast.makeText(getApplicationContext(), R.string.alert_error_internet_detail, Toast.LENGTH_SHORT).show();
                        Global.showOtherActivity(MainActivity.this, LoginActivity.class, 1);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            String result = obj.getString("result");
                            JSONObject obj_result = new JSONObject(result);
                            Global.gUserInfo.initialWithJson(obj_result);

                            initUIView();
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), R.string.alert_server_error_detail, Toast.LENGTH_SHORT).show();
                            Global.showOtherActivity(MainActivity.this, LoginActivity.class, 1);
                        }
                    }
                });
    }

    private void initUIView() {
        Global.gParamValues.clear();

        String str_class = Global.gUserInfo.classname;
        if (str_class.length() == 0) {
            Global.gFrgIndex = 4;
        }

        isSearch = false;

        draw_main = findViewById(R.id.drawer_main);
        tog_menu = new ActionBarDrawerToggle(this, draw_main, R.string.nav_open, R.string.nav_close);
        draw_main.addDrawerListener(tog_menu);
        tog_menu.syncState();

        nav_menu = findViewById(R.id.nav_main);
        nav_menu.setNavigationItemSelectedListener(item -> {
            draw_main.closeDrawer(GravityCompat.START);
            int id = item.getItemId();
            switch(id)
            {
                case R.id.nav_item_one:
                    if (Global.gFrgIndex != 0) {
                        Global.gFrgIndex = 0;
                        loadFragmentByIndex();
                    }
                    break;
                case R.id.nav_item_two:
                    if (Global.gFrgIndex != 1) {
                        Global.gFrgIndex = 1;
                        loadFragmentByIndex();
                    }
                    break;
                case R.id.nav_item_three:
                    if (Global.gFrgIndex != 2) {
                        Global.gFrgIndex = 2;
                        Global.gCreateParams.clear();
                        loadFragmentByIndex();
                    }
                    break;
                case R.id.nav_item_four:
                    if (Global.gFrgIndex != 3) {
                        Global.gFrgIndex = 3;
                        loadFragmentByIndex();
                    }
                    break;
                case R.id.nav_item_five:
                    if (Global.gFrgIndex != 4) {
                        Global.gFrgIndex = 4;
                        loadFragmentByIndex();
                    }
                    break;
                case R.id.nav_item_six:
                    WXUserInfo.WxUserInitialize();
                    Global.showOtherActivity(MainActivity.this, LoginActivity.class, 1);
                    return true;
                case R.id.nav_item_seven:
                    if (Global.gFrgIndex != 5) {
                        Global.gFrgIndex = 5;
                        loadFragmentByIndex();
                    }
                    break;
                case R.id.nav_item_eight:
                    if (Global.gFrgIndex != 6) {
                        Global.gFrgIndex = 6;
                        loadFragmentByIndex();
                    }
                    break;
                default:
                    break;
            }
            return true;
        });

        View headerView = nav_menu.getHeaderView(0);

        WXUserInfo info = Global.gUserInfo.wxUserInfo;

        img_avatar = headerView.findViewById(R.id.img_nav_header);
        Glide.with(MainActivity.this).load(info.getHeadimgurl()).into(img_avatar);
        lbl_name = headerView.findViewById(R.id.lbl_nav_name);
        lbl_name.setText(info.getNickname());
        lbl_location = headerView.findViewById(R.id.lbl_nav_location);
        lbl_location.setText(info.getCity() + " " + info.getProvince() + ", " + info.getCountry());
        lbl_payed = headerView.findViewById(R.id.lbl_nav_payed);
        String str_payed;
        if (Global.gUserInfo.payed > 50000) {
            str_payed = String.format("%.1f", (float) Global.gUserInfo.payed / 100000.0f) + "K";
        } else if (Global.gUserInfo.payed > 10000) {
            str_payed = String.valueOf(Global.gUserInfo.payed / 100);
        } else {
            str_payed = String.format("%.1f", (float) Global.gUserInfo.payed / 100.0f);
        }
        lbl_payed.setText(str_payed);
        lbl_funcs = headerView.findViewById(R.id.lbl_nav_calc);
        lbl_funcs.setText(String.valueOf(Global.gUserInfo.cnt_func));
        lbl_rfuncs = headerView.findViewById(R.id.lbl_nav_rcalc);
        lbl_rfuncs.setText(String.valueOf(Global.gUserInfo.cnt_restfunc));

        llt_menu = findViewById(R.id.llt_main_menu);
        llt_add = findViewById(R.id.llt_main_add);
        llt_search = findViewById(R.id.llt_main_search);
        lbl_title = findViewById(R.id.lbl_main_title);
        txt_search = findViewById(R.id.txt_main_search);
        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Global.gFrgIndex == 5) {
                    ProjectFragment changeFragment = (ProjectFragment) fragment;
                    changeFragment.onEventSearchEditing(s.toString());
                } else {
                    FunctionListFragment changeFragment = (FunctionListFragment) fragment;
                    changeFragment.onEventSearchEditing(s.toString());
                }
            }
        });
        img_add = findViewById(R.id.img_main_add);
        initSearchView();

        loadFragmentByIndex();
    }

    public void loadFragmentByIndex() {
        img_add.setImageResource(R.drawable.ico_add);
        switch (Global.gFrgIndex) {
            case 0:
                fragment = new FunctionListFragment();
                break;
            case 1:
                fragment = new CalcFragment();
                break;
            case 2:
                fragment = new GuideFunctionFragment();
                break;
            case 3:
                fragment = new HistoryFragment();
                break;
            case 4:
                fragment = new ProfileFragment();
                break;
            case 5:
                fragment = new ProjectFragment();
                break;
            case 6:
                fragment = new GuideProjectFragment();
                break;
            default:
                break;
        }

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frg_main_body, fragment);
        fragmentTransaction.commit();
    }

    private void initSearchView() {
        if (!isSearch) {
            llt_menu.setVisibility(View.VISIBLE);
            lbl_title.setVisibility(View.VISIBLE);
            txt_search.setVisibility(View.GONE);
            llt_add.setVisibility(View.GONE);
            if (Global.gFrgIndex == 5) {
//                llt_add.setVisibility(View.GONE);
            } else {
                img_add.setImageResource(R.drawable.ico_add);
            }
        } else {
            llt_add.setVisibility(View.VISIBLE);
            llt_menu.setVisibility(View.GONE);
            lbl_title.setVisibility(View.GONE);
            txt_search.setVisibility(View.VISIBLE);
            img_add.setImageResource(R.drawable.ico_close);
        }
    }

    public void onClickMenuIcon(View view) {
        draw_main.openDrawer(GravityCompat.START);
    }

    public void onClickSearchIcon(View view) {
        isSearch = true;
        initSearchView();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        txt_search.requestFocus();
    }

    public void onClickAddIcon(View view) {
        if (isSearch) {
            txt_search.setText("");
            isSearch = false;
            initSearchView();
            Global.hideKeyboard(this);
        } else {
            Global.gParamValues.clear();
            Global.gSelFuncItems.clear();
            for (int i = 0; i < Global.gShowFuncItems.size(); i++) {
                FunctionItem item = Global.gShowFuncItems.get(i);
                if (item.isSelected) {
                    Global.gSelFuncItems.add(item);
                }
            }
            if (Global.gSelFuncItems.size() > 0) {
                // Create project
                Global.showOtherActivity(MainActivity.this, CreateProjectActivity.class, 0);
            } else {
                Toast.makeText(this, R.string.main_empty_select, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
