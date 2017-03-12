package com.example.cajet.lab7;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText conf_Password, new_Password, edit_password;
    private LinearLayout create_password, input_password;
    private Button btn_ok, btn_clear;
    private String temp;
    private boolean flag= false; //标记是否已经存储了密码
    private SharedPreferences sharedpreferences;
    private static final String PREFERENCE_NAME = "PASSWORD_PREFERENCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        sharedpreferences= getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        temp= sharedpreferences.getString("KEY_PASSWORD", "");
        if (!temp.isEmpty()) {  //已经存储了密码
            create_password.setVisibility(View.GONE);  //不可见的且不占用原来的布局空间
            input_password.setVisibility(View.VISIBLE);
            flag= true;
        }

        setListener();
    }

    private void init() {
        new_Password= (EditText) findViewById(R.id.new_password);
        conf_Password= (EditText)findViewById(R.id.confirm_password);
        edit_password= (EditText) findViewById(R.id.edit_password);
        btn_ok= (Button) findViewById(R.id.button_ok);
        btn_clear= (Button) findViewById(R.id.button_clear);
        create_password= (LinearLayout) findViewById(R.id.create_password);
        input_password= (LinearLayout) findViewById(R.id.input_password);
    }

    private void setListener() {
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    String s= edit_password.getText().toString();
                    if (s.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (s.equals(temp)) {
                            Intent intent2 = new Intent(MainActivity.this, FileEditorActivity.class);
                            startActivity(intent2);
                        } else {
                            Toast.makeText(MainActivity.this, "Invalid Password.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    String s1= new_Password.getText().toString();
                    String s2= conf_Password.getText().toString();
                    if (s1.isEmpty()) {         //若new_Password为空
                        Toast.makeText(MainActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!s1.equals(s2)) {   //若不匹配
                            Toast.makeText(MainActivity.this, "Password Mismatch.", Toast.LENGTH_SHORT).show();
                        } else {
                            SharedPreferences.Editor editor= sharedpreferences.edit();
                            editor.putString("KEY_PASSWORD", s1);
                            editor.commit();
                            Intent intent = new Intent(MainActivity.this, FileEditorActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    edit_password.setText("");
                } else {
                    conf_Password.setText("");
                    new_Password.setText("");
                }
            }
        });
    }
}
