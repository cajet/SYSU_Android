package com.example.cajet.lab4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by cajet on 2016/10/16.
 */

public class StaticActivity extends Activity{

    private ListView fruit_list;
    private ArrayList<HashMap<String, Object>> list_item;
    private SimpleAdapter simpleAdapter;
    private ArrayList<Fruit> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.static_layout);
        init();
        setListener();
    }

    private void init() {
        fruit_list= (ListView) findViewById(R.id.fruit_list);
        dataList= new ArrayList<Fruit>();
        list_item= new ArrayList<HashMap<String, Object>>();
        dataList.add(new Fruit(R.mipmap.apple, "Apple"));
        dataList.add(new Fruit(R.mipmap.banana, "Banana"));
        dataList.add(new Fruit(R.mipmap.cherry, "Cherry"));
        dataList.add(new Fruit(R.mipmap.coco, "Coco"));
        dataList.add(new Fruit(R.mipmap.kiwi, "Kiwi"));
        dataList.add(new Fruit(R.mipmap.orange, "Orange"));
        dataList.add(new Fruit(R.mipmap.pear, "Pear"));
        dataList.add(new Fruit(R.mipmap.strawberry, "Strawberry"));
        dataList.add(new Fruit(R.mipmap.watermelon, "Watermelon"));

        Iterator<Fruit> it= dataList.iterator();
        while (it.hasNext()) {
            Fruit temp= it.next();
            HashMap map= new HashMap();
            map.put("image_id", temp.getFruitImageId());
            map.put("name", temp.getFruitName());
            list_item.add(map);
        }
        simpleAdapter = new SimpleAdapter(
                this,
                list_item,
                R.layout.static_list_item,
                new String[] { "image_id", "name" },
                new int[] { R.id.fruit_image, R.id.fruit_name });

        fruit_list.setAdapter(simpleAdapter);
    }

    private void setListener() {
        fruit_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap map= (HashMap) parent.getItemAtPosition(position);
                Intent intent= new Intent("com.example.cajet.lab4.staticreceiver");
                intent.putExtra("fruit", map);
                sendBroadcast(intent);
            }
        });
    }

}
