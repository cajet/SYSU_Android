package com.example.cajet.text_contact.note;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cajet.text_contact.R;

public class Main extends Activity{
    Button addButton, searchButton;
    ListView myListView;
    MyDataBaseAdapter myDataBaseAdapter = null;
    String oldTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_activity_main);
        myListView = new ListView(this);
        myListView = (ListView)findViewById(R.id.listView);
        addButton = (Button)findViewById(R.id.add);
        searchButton = (Button)findViewById(R.id.search);
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Main.this, EditPlan.class), 1);
            }
        });
        searchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main.this, SearchPlan.class));
            }
        });

        myDataBaseAdapter = new MyDataBaseAdapter(this);
        myDataBaseAdapter.open();
        updateAdapter();

        //长按记录
        myListView.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView dateText = (TextView)view.findViewById(R.id.date);
                AlertDialog.Builder builder= new AlertDialog.Builder(Main.this);
                builder.setTitle("(●'◡'●)").setMessage(" 是否删除？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myDataBaseAdapter.deleteData(dateText.getText().toString());
                                Toast.makeText(Main.this, "删除成功", Toast.LENGTH_SHORT).show();
                                updateAdapter();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                return true;
            }
        });
        //点击记录
        myListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView themeText = (TextView)view.findViewById(R.id.theme);
                TextView textText = (TextView)view.findViewById(R.id.text);
                TextView timeText = (TextView)view.findViewById(R.id.date);
                TextView deadLineText = (TextView)view.findViewById(R.id.deadTime);
                TextView stateText = (TextView)view.findViewById(R.id.state);
                oldTime = timeText.getText().toString();
                Intent intent = new Intent(Main.this, EditPlan.class);
                Bundle bundle = new Bundle();
                bundle.putString("theme", themeText.getText().toString());
                bundle.putString("text", textText.getText().toString());
                bundle.putString("deadline", deadLineText.getText().toString());
                bundle.putString("state", stateText.getText().toString());
                intent.putExtras(bundle);
                startActivityForResult(intent, 2);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String theme = data.getExtras().getString("theme");
            String text = data.getExtras().getString("text");
            String time = data.getExtras().getString("time");
            String deadTime = data.getExtras().getString("deadline");
            String state = data.getExtras().getString("state");
            if (requestCode == 1) {
                myDataBaseAdapter.insertData(theme, text, time, deadTime, state);
                Toast.makeText(Main.this, "添加成功", Toast.LENGTH_SHORT).show();
            }
            if (requestCode == 2) {
                myDataBaseAdapter.updateData(oldTime, theme, text, time, deadTime, state);
                Toast.makeText(Main.this, "修改成功", Toast.LENGTH_SHORT).show();
            }
            updateAdapter();
        }
    }

    protected void onResume() {
        super.onResume();
        updateAdapter();
    }

    public void updateAdapter() {
        Cursor cur = myDataBaseAdapter.fetchAllData();
        if (cur != null) {
            SimpleCursorAdapter myListAdapter = new MyAdapter (this,
                    R.layout.note_list_item,
                    cur,
                    new String[] {MyDataBaseAdapter.KEY_THEME,
                            MyDataBaseAdapter.KEY_TEXT, MyDataBaseAdapter.KEY_TIME,
                            MyDataBaseAdapter.KEY_STATE, MyDataBaseAdapter.KEY_DEADLINE},
                    new int[] {R.id.theme, R.id.text, R.id.date, R.id.state, R.id.deadTime},
                    0);
            myListView.setAdapter(myListAdapter);
        }
    }

    public class MyAdapter extends SimpleCursorAdapter{

        MyAdapter(Context context, int layout, Cursor c, String[] from,
                         int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            final TextView timeText = (TextView)view.findViewById(R.id.date);
            final TextView stateText = (TextView)view.findViewById(R.id.state);
            final ImageButton imagebutton = (ImageButton) view.findViewById(R.id.imageButton);
            //设置图片的点击效果
            Cursor cur = myDataBaseAdapter.fetchDataForState(timeText.getText().toString());
            if (cur != null) {
                int idState = cur.getColumnIndex(MyDataBaseAdapter.KEY_STATE);
                if (cur.getString(idState).equals("已完成")) {
                    imagebutton.setImageResource(R.mipmap.check);
                } else {
                    imagebutton.setImageResource(R.mipmap.todo);
                }
            }
            imagebutton.setOnClickListener(new ImageButton.OnClickListener(){
                public void onClick(View v) {
                    Cursor cur = myDataBaseAdapter.fetchDataForState(timeText.getText().toString());
                    if (cur != null) {
                        int idState = cur.getColumnIndex(MyDataBaseAdapter.KEY_STATE);
                        if (cur.getString(idState).equals("已完成")) {
                            myDataBaseAdapter.updateData(timeText.getText().toString(), "未完成");
                            stateText.setText("未完成");
                            imagebutton.setImageResource(R.mipmap.todo);
                        } else {
                            myDataBaseAdapter.updateData(timeText.getText().toString(), "已完成");
                            stateText.setText("已完成");
                            imagebutton.setImageResource(R.mipmap.check);
                        }
                    }
                }
            });
            return view;
        }
    }
}
