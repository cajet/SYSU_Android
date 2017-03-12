package com.example.cajet.lab3;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by cajet on 2016/10/9.
 */

public class ContactDetail extends Activity{

    private ListView inf_list;
    private String array[] = {"编辑联系人", "分享联系人", "加入黑名单", "删除联系人"};
    private ArrayAdapter<String> arrayAdapter;
    private TextView name, mobileNumber, type, location;
    private RelativeLayout bg;
    private ImageView back, star;
    private boolean star_flag= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.contact_detail);
        init();
        setListener();
    }

    private void init() {
        name= (TextView) findViewById(R.id.name);
        mobileNumber= (TextView) findViewById(R.id.mobileNumber);
        type= (TextView) findViewById(R.id.type);
        location= (TextView) findViewById(R.id.location);
        bg= (RelativeLayout) findViewById(R.id.top);
        inf_list= (ListView) findViewById(R.id.inf_list);
        back= (ImageView) findViewById(R.id.back);
        star= (ImageView) findViewById(R.id.star);

        arrayAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array);
        inf_list.setAdapter(arrayAdapter);

        //处理传递过来的数据
        Intent intent= getIntent(); //取出传递过来的数据
        HashMap map= (HashMap) intent.getSerializableExtra("onclick_contactor");
        name.setText(map.get("name").toString());
        mobileNumber.setText(map.get("mobileNumber").toString());
        type.setText(map.get("type").toString());
        location.setText(map.get("location").toString());
        bg.setBackgroundColor((Integer) map.get("bgcolor"));
    }

    private void setListener() {
        //返回跳转
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent= new Intent(ContactDetail.this, ContactList.class);
                startActivity(intent);*/
                finish();
            }
        });
        //设置星星图标切换
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (star_flag == false) {
                    star.setImageResource(R.mipmap.full_star);
                    star_flag= true;
                } else {
                    star.setImageResource(R.mipmap.empty_star);
                    star_flag= false;
                }
            }
        });
    }
}
