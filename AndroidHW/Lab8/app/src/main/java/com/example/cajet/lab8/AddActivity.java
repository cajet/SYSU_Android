package com.example.cajet.lab8;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by cajet on 2016/11/15.
 */

public class AddActivity extends Activity {

    private EditText et_name, et_birth, et_gift;
    private Button add_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);
        init();
        setListener();
    }

    private void init() {
        et_name= (EditText) findViewById(R.id.et_name);
        et_birth= (EditText) findViewById(R.id.et_birth);
        et_gift= (EditText) findViewById(R.id.et_gift);
        add_btn= (Button) findViewById(R.id.addBtn);
    }

    private void setListener() {
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_name.getText().toString().equals("")) {
                    Toast.makeText(AddActivity.this, "名字为空，请完善", Toast.LENGTH_SHORT).show();
                } else {
                    MyDB mydb= new MyDB(AddActivity.this);
                    if (mydb.query(et_name.getText().toString())) {
                        Toast.makeText(AddActivity.this, "名字重复啦，请核查", Toast.LENGTH_SHORT).show();
                    } else {
                        mydb.insert(et_name.getText().toString(), et_birth.getText().toString(), et_gift.getText().toString());
                        setResult(1);
                        finish();//才能跳转回MainActivity
                    }
                }
            }
        });
    }
}
