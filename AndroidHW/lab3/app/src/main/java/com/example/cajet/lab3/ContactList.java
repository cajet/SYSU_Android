package com.example.cajet.lab3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static android.R.id.message;

/**
 * Created by cajet on 2016/10/9.
 */

public class ContactList extends Activity{

    private ListView contactsList;
    private ArrayList<HashMap<String, Object>> list_item;
    private SimpleAdapter simpleAdapter;
    private ArrayList<Contactor> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.contact_list);
        init();
        setListener();
    }

    public void init() {
        contactsList= (ListView) findViewById(R.id.contacts_list);
        dataList= new ArrayList<Contactor>();   //一定要记得先new啊！
        list_item= new ArrayList<HashMap<String, Object>>();
        dataList.add(new Contactor("Aaron", "17715523654", "手机", "江苏苏州电信", 0xFFBB4C3B));
        dataList.add(new Contactor("Elvis", "18825653224", "手机", "广东揭阳移动", 0xFFc48d30));
        dataList.add(new Contactor("David", "15052116654", "手机", "江苏无锡移动", 0xFF4469b0));
        dataList.add(new Contactor("Edwin", "18854211875", "手机", "山东青岛移动", 0xFF20A17B));
        dataList.add(new Contactor("Frank", "13955188541", "手机", "安徽合肥移动", 0xFFBB4C3B));
        dataList.add(new Contactor("Joshua", "13621574410", "手机", "江苏苏州移动", 0xFFc48d30));
        dataList.add(new Contactor("Ivan", "15684122771", "手机", "山东烟台联通", 0xFF4469b0));
        dataList.add(new Contactor("Mark", "17765213579", "手机", "广东珠海电信", 0xFF20A17B));
        dataList.add(new Contactor("Joseph", "13315466578", "手机", "河北石家庄电信", 0xFFBB4C3B));
        dataList.add(new Contactor("Phoebe", "17895466428", "手机", "山东东营移动", 0xFFc48d30));
        Iterator<Contactor> it= dataList.iterator();
        while (it.hasNext()) {
            Contactor temp= it.next();
            HashMap map= new HashMap();
            map.put("firstWord", temp.getFirstWord());
            map.put("name", temp.getName());
            map.put("mobileNumber", temp.getMobilenumber());
            map.put("type", temp.getType());
            map.put("location", temp.getLocation());
            map.put("bgcolor", temp.getBGcolor());
            list_item.add(map);
        }
        simpleAdapter = new SimpleAdapter(
                this,
                list_item,
                R.layout.contact_list_item,
                new String[] { "firstWord", "name" },
                new int[] { R.id.first_word, R.id.item_name });

        contactsList.setAdapter(simpleAdapter);
    }

    private void setListener() {
        //设置点击事件
        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap map= (HashMap) parent.getItemAtPosition(position);
                Intent intent= new Intent(ContactList.this, ContactDetail.class);
                intent.putExtra("onclick_contactor", map);
                startActivity(intent);
            }
        });
        //设置长按事件
        contactsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                HashMap map= (HashMap) parent.getItemAtPosition(position);
                String name= map.get("name").toString();
                new AlertDialog.Builder(ContactList.this)
                        .setTitle("删除联系人")
                        .setMessage("确定删除联系人" + name + "吗？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list_item.remove(position);      //注意是list_item，不是dataList啊！
                                simpleAdapter.notifyDataSetChanged();
                            }
                        }).show();
                return true;
            }
        });
    }

}
