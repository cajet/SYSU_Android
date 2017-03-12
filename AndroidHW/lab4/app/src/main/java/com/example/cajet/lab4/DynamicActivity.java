package com.example.cajet.lab4;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by cajet on 2016/10/16.
 */

public class DynamicActivity extends Activity{

    private EditText et_word;
    private Button res_btn, send_btn;
    private String et_string;
    private DynamicReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dynamic_layout);
        init();
        setListener();
    }

    private void init() {
        res_btn= (Button) findViewById(R.id.reg_broadcast);
        send_btn= (Button) findViewById(R.id.send);
        et_word= (EditText) findViewById(R.id.et_word);
    }

    private void setListener() {

        res_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (res_btn.getText().equals("Register Broadcast")) {
                    //注册动态广播
                    IntentFilter filter= new IntentFilter("com.example.cajet.lab4.dynamicreceiver");
                    receiver= new DynamicReceiver();
                    registerReceiver(receiver, filter);
                    res_btn.setText("Unregister Broadcast");

                } else {
                    //注销动态广播
                    unregisterReceiver(receiver);
                    res_btn.setText("Register Broadcast");
                }
            }
        });

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_string= et_word.getText().toString();
                Bundle bundle= new Bundle();
                bundle.putString("send_word", et_string);
                Intent intent= new Intent("com.example.cajet.lab4.dynamicreceiver");
                intent.putExtras(bundle);
                sendBroadcast(intent);
            }
        });
    }
}
