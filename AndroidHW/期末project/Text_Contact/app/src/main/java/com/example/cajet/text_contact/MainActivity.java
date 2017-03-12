package com.example.cajet.text_contact;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cajet.text_contact.note.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity implements View.OnClickListener {
    ListView lv_userList;
    ContactListAdapter contactListAdapter;
    RelativeLayout searchLinearLayout;
    LinearLayout mainLinearLayout;
    EditText et_search;
    Button exit_search;
    ImageButton m;
    TextView[] menuItem = new TextView[5];
    ArrayList<Map<String, Object>> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        try {
            mainLinearLayout = (LinearLayout)findViewById(R.id.list_ll);
            lv_userList = (ListView) findViewById(R.id.lv_userList);
            searchLinearLayout = (RelativeLayout) findViewById(R.id.ll_search);
            et_search = (EditText) findViewById(R.id.et_search);
            exit_search = (Button) findViewById(R.id.exit_search);
            exit_search.setOnClickListener(this);
            contactListAdapter = new ContactListAdapter(MainActivity.this, list);
            lv_userList.setAdapter(contactListAdapter);
            lv_userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    setGone();
                    if (m.getTag().equals("on")) {
                        m.clearAnimation();
                    }
                    HashMap map = (HashMap) parent.getItemAtPosition(position);
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra("userMap", map);
                    startActivityForResult(intent, 3); //3代表请求转向DetailActivity页面
                }
            });
            lv_userList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    setGone();
                    if (m.getTag().equals("on")) {
                        m.clearAnimation();
                    }
                    final HashMap map = (HashMap) parent.getItemAtPosition(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("(●'◡'●)").setMessage("是否删除？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    User user = new User();
                                    user._id = Integer.valueOf(map.get("_id").toString());
                                    DBHelper helper = new DBHelper(MainActivity.this);
                                    helper.delete(user);
                                    loadUserList();
                                }
                            }).setNegativeButton("取消", null).show();
                    return true;
                }
            });
            loadButtonMenu();
            loadUserList();
            SysApplication.getInstance().addActivity(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadButtonMenu() {
        m = (ImageButton) findViewById(R.id.menu);
        for (int i = 0; i < 5; i++) {
            int identify = getResources().getIdentifier("menu" + (i + 1), "id", getPackageName());
            menuItem[i] = (TextView) findViewById(identify);
            Drawable drawable = menuItem[i].getCompoundDrawables()[1];
            drawable.setBounds(0, 0, 40, 40);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
            menuItem[i].setCompoundDrawables(null, drawable, null, null);
            menuItem[i].setOnClickListener(this);
        }
        setGone();
        m.setOnClickListener(this);
    }
    private static AnimationSet getTranslateAnimate(float fromX, float toX, float fromY, float toY, float fromAlpha, float toAlpha, int speed) {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, fromX,
                TranslateAnimation.RELATIVE_TO_SELF, toX,
                TranslateAnimation.RELATIVE_TO_SELF, fromY,
                TranslateAnimation.RELATIVE_TO_SELF, toY);
        AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        if (speed == 0)
            animationSet.setInterpolator(new DecelerateInterpolator());
        else if (speed == 2)
            animationSet.setInterpolator(new AccelerateInterpolator());
        else
            animationSet.setInterpolator(new LinearInterpolator());

        animationSet.setDuration(300);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        return animationSet;
    }
    private static AnimationSet getScaleAnimation(float from, float to) {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(from, to, from, to, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        RotateAnimation rotateAnimation = new RotateAnimation(0, 45, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        if (from > to) {
            rotateAnimation = new RotateAnimation(45, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        }
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(rotateAnimation);
        animationSet.setInterpolator(new AccelerateInterpolator());
        animationSet.setDuration(200);
        animationSet.setFillAfter(true);
        return animationSet;
    }
    Handler handler = new Handler();

    private void loadSearchLinearLayout() {
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String condition = et_search.getText().toString().trim();
                DBHelper helper = new DBHelper(MainActivity.this);
                ArrayList<Map<String, Object>> subList = helper.getSearchUserList(condition);
                list.clear();
                list.addAll(subList);
                contactListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == 1) {//添加用户成功，对MainActivity进行刷新
                DBHelper helper= new DBHelper(MainActivity.this);
                ArrayList<Map<String, Object>> subList = helper.getUserList();
                list.clear();
                list.addAll(subList);
                contactListAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == 3) {  //在detail页面更改，返回来后更新list view
            loadUserList();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadUserList() {
        DBHelper helper = new DBHelper(MainActivity.this);
        ArrayList<Map<String, Object>> subList = helper.getUserList();
        list.clear();
        list.addAll(subList);
        contactListAdapter.notifyDataSetChanged();
    }
    private void setGone() {
        m.setTag("off");
        for (int i = 0; i < 5; i++) {
            menuItem[i].setVisibility(View.GONE);
        }
        searchLinearLayout.setVisibility(View.GONE);
    }
    final float angle = (float) Math.PI / 4;
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.menu:
                if (m.getTag().equals("off")) {
                    m.setTag("on");
                    m.startAnimation(getScaleAnimation(1, 1.4f));
                    for (int i = 0; i < 5; i++) {
                        menuItem[i].setVisibility(View.VISIBLE);
                        float fromX = (float) (2 * Math.sin(i * angle));
                        float fromY = (float) (2 * Math.cos(i * angle));
                        menuItem[i].startAnimation(getTranslateAnimate(fromX, 0, fromY, 0, 0, 1, 0));
                    }
                } else {
                    m.setTag("off");
                    m.startAnimation(getScaleAnimation(1.4f, 1));
                    for (int i = 0; i < 5; i++) {
                        float toX = (float) (2 * Math.sin(i * angle));
                        float toY = (float) (2 * Math.cos(i * angle));
                        menuItem[i].startAnimation(getTranslateAnimate(0, toX, 0, toY, 1, 0, 2));
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setGone();
                        }
                    }, 300);
                }
                break;
            case R.id.menu1:
                setGone();
                m.clearAnimation();
                intent = new Intent(MainActivity.this, DetailActivity.class);
                /*0代表请求跳转到添加界面*/
                startActivityForResult(intent, 0);
                searchLinearLayout.setVisibility(View.GONE);
                et_search.setText("");
                break;
            case R.id.menu2:
                setGone();
//                m.clearAnimation();
                m.startAnimation(getScaleAnimation(1.4f, 1));
                /*1代表请求查找*/
                loadSearchLinearLayout();
                searchLinearLayout.setVisibility(View.VISIBLE);
                et_search.requestFocus();
                et_search.selectAll();
                break;
            case R.id.exit_search:
                searchLinearLayout.setVisibility(View.GONE);
                loadUserList();
                break;
            case R.id.menu3:
                setGone();
                m.clearAnimation();
                Intent intent_= new Intent(MainActivity.this, com.example.cajet.text_contact.news.MainActivity.class);
                startActivity(intent_);
                break;
            case R.id.menu4:
                /*3代表备忘录*/
                setGone();
                m.clearAnimation();
                intent = new Intent(MainActivity.this, Main.class);
                startActivity(intent);
                break;
            case R.id.menu5:
                /*4代表退出程序*/
                SysApplication.getInstance().exit();
                break;
        }
    }
}
