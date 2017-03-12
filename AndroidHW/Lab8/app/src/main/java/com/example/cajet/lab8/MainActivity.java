package com.example.cajet.lab8;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity {

    private Button btn_add;
    private ListView lv;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setListener();
    }

    private void init() {
        btn_add= (Button) findViewById(R.id.btn_add);
        lv= (ListView) findViewById(R.id.list);
        updateList();
    }

    private void updateList() {
        MyDB mydb= new MyDB(MainActivity.this);
        Cursor cursor = mydb.getAll();
        if ((cursor != null) && (cursor.getCount() >= 0)) {
            adapter= new SimpleCursorAdapter(this,
                    R.layout.lv_item,
                    cursor,
                    new String[] {"name", "birth", "gift"},
                    new int[] {R.id.nameText, R.id.birthText, R.id.giftText});
            lv.setAdapter(adapter);
        }
        mydb.close();
    }


    private void setListener() {
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(intent, 0);  /*0代表请求跳转到添加界面*/
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);  //自定义AlertDialog.Builder
                View layout = inflater.inflate(R.layout.dialog_activity, null);
                final EditText ed_birth= (EditText) layout.findViewById(R.id.ed_birth); //变量初始化
                final EditText ed_gift= (EditText) layout.findViewById(R.id.ed_gift);
                final TextView name= (TextView) layout.findViewById(R.id.txt_name);
                final TextView phone= (TextView) layout.findViewById(R.id.phone_number);

                Cursor cursor= (Cursor) parent.getItemAtPosition(position);  //获取点击的item数据
                name.setText(cursor.getString(cursor.getColumnIndex("name")));
                ed_birth.setText(cursor.getString(cursor.getColumnIndex("birth")));
                ed_gift.setText(cursor.getString(cursor.getColumnIndex("gift")));
                phone.setText(getPhone(cursor.getString(cursor.getColumnIndex("name"))));

                AlertDialog.Builder builder=  new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("o(^▽^)o")
                        .setView(layout)
                        .setNegativeButton("放弃修改", null)
                        .setPositiveButton("保存修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyDB mydb= new MyDB(MainActivity.this);
                                mydb.update(name.getText().toString(), ed_birth.getText().toString(), ed_gift.getText().toString());
                                updateList();
                            }
                        })
                        .show();

            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor= (Cursor) parent.getItemAtPosition(position);
                final String temp = cursor.getString(cursor.getColumnIndex("name"));  //获取点击的item的name

                AlertDialog.Builder builder=  new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("是否删除？")
                        .setNegativeButton("否", null)
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyDB mydb= new MyDB(MainActivity.this);
                                mydb.delete(temp);
                                updateList();
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == 1) { //发生新的添加
                updateList();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getPhone(String _name) {
        Cursor Cursor1 = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        String str1 = "";
        while (Cursor1.moveToNext()) {
            String str2 = Cursor1.getString(Cursor1.getColumnIndex("_id"));
            if (Cursor1.getString(Cursor1.getColumnIndex("display_name")).equals(_name)) {
                str1 = "";
                if (Integer.parseInt(Cursor1.getString(Cursor1.getColumnIndex("has_phone_number"))) > 0) {
                    Cursor Cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, "contact_id = " + str2, null, null);
                    while (Cursor2.moveToNext()) {
                        str1 = str1 + Cursor2.getString(Cursor2.getColumnIndex("data1")) + " ";
                    }
                    Cursor2.close();
                }
            }
        }
        Cursor1.close();
        if (str1.equals("")) str1= "无";
        return str1;
    }
}
