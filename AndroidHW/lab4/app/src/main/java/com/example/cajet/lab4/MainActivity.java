package com.example.cajet.lab4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button static_btn, dynamic_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        static_btn= (Button) findViewById(R.id.static_reg);
        dynamic_btn= (Button) findViewById(R.id.dynamic_reg);
        static_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1= new Intent(MainActivity.this, StaticActivity.class);
                startActivity(intent1);
            }
        });
        dynamic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2= new Intent(MainActivity.this, DynamicActivity.class);
                startActivity(intent2);
            }
        });
    }
}
