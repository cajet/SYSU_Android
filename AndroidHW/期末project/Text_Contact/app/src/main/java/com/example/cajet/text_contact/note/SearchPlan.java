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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cajet.text_contact.R;


public class SearchPlan extends Activity {
    Button searchB;
    AutoCompleteTextView searchText;
    ListView searchList;
    MyDataBaseAdapter myDataBaseAdapter = null;
    ArrayAdapter<String> adapter;
    String oldTime;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_search_layout);
        findView();
        searchB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cur = myDataBaseAdapter.fetchData(searchText.getText().toString());
                if (cur.getCount() != 0) {
                    updateListAdapter();
                    Toast.makeText(SearchPlan.this, "搜索成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SearchPlan.this, "没有该记录", Toast.LENGTH_SHORT).show();
                }
            }
        });
        searchList.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                final TextView dateText = (TextView)view.findViewById(R.id.date);
                AlertDialog.Builder builder= new AlertDialog.Builder(SearchPlan.this);
                builder.setTitle("(●'◡'●)").setMessage(" 是否删除？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myDataBaseAdapter.deleteData(dateText.getText().toString());
                                Toast.makeText(SearchPlan.this, "删除成功", Toast.LENGTH_SHORT).show();
                                updateListAdapter();
                                updateTextAdapter();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                return true;
            }
        });
        searchList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                TextView themeText = (TextView)view.findViewById(R.id.theme);
                TextView textText = (TextView)view.findViewById(R.id.text);
                TextView timeText = (TextView)view.findViewById(R.id.date);
                TextView deadLineText = (TextView)view.findViewById(R.id.deadTime);
                TextView stateText = (TextView)view.findViewById(R.id.state);
                oldTime = timeText.getText().toString();
                Intent intent = new Intent(SearchPlan.this, EditPlan.class);
                Bundle bundle = new Bundle();
                bundle.putString("theme", themeText.getText().toString());
                bundle.putString("text", textText.getText().toString());
                bundle.putString("deadline", deadLineText.getText().toString());
                bundle.putString("state", stateText.getText().toString());
                intent.putExtras(bundle);
                startActivityForResult(intent, 3);
            }
        });

        myDataBaseAdapter = new MyDataBaseAdapter(this);
        myDataBaseAdapter.open();
        updateTextAdapter();

    }
    private void findView() {
        searchB=(Button)findViewById(R.id.finishSearch);
        searchText=(AutoCompleteTextView)findViewById(R.id.searchPlan);
        searchList=(ListView) findViewById(R.id.searchListView);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String theme = data.getExtras().getString("theme");
            String text = data.getExtras().getString("text");
            String time = data.getExtras().getString("time");
            String deadTime = data.getExtras().getString("deadline");
            String state = data.getExtras().getString("state");
            if (requestCode == 3) {
                myDataBaseAdapter.updateData(oldTime, theme, text, time, deadTime, state);
                Toast.makeText(SearchPlan.this, "修改成功", Toast.LENGTH_SHORT).show();
            }
            updateListAdapter();
            updateTextAdapter();
        }
    }

    private void updateTextAdapter() {
        Cursor cur = myDataBaseAdapter.fetchAllData();
        if (cur != null) {
            String nameStrings[] = new String[cur.getCount()*2];
            int count = 0;
            if (cur.moveToFirst()) {
                int idTheme = cur.getColumnIndex(MyDataBaseAdapter.KEY_THEME);
                int idText = cur.getColumnIndex(MyDataBaseAdapter.KEY_TEXT);
                do {
                    nameStrings[count++] = cur.getString(idTheme);
                    nameStrings[count++] = cur.getString(idText);
                    System.out.println(cur.getString(idTheme));

                } while (cur.moveToNext());
            }
            adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, nameStrings);
            searchText.setAdapter(adapter);
        }
    }
    private void updateListAdapter() {
        Cursor cur = myDataBaseAdapter.fetchData(searchText.getText().toString());
        if (cur != null) {
            SimpleCursorAdapter myListAdapter = new MyAdapter (this,
                    R.layout.note_list_item,
                    cur,
                    new String[] {MyDataBaseAdapter.KEY_THEME,
                            MyDataBaseAdapter.KEY_TEXT, MyDataBaseAdapter.KEY_TIME,
                            MyDataBaseAdapter.KEY_STATE, MyDataBaseAdapter.KEY_DEADLINE},
                    new int[] {R.id.theme, R.id.text, R.id.date, R.id.state, R.id.deadTime},
                    0);
            searchList.setAdapter(myListAdapter);
        }
    }

    public class MyAdapter extends SimpleCursorAdapter{

        MyAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            final TextView timeText = (TextView)view.findViewById(R.id.date);
            final TextView stateText = (TextView)view.findViewById(R.id.state);
            final ImageButton imagebutton = (ImageButton) view.findViewById(R.id.imageButton);
            imagebutton.setOnClickListener(new ImageButton.OnClickListener(){
                public void onClick(View v) {
                    Cursor cur = myDataBaseAdapter.fetchDataForState(timeText.getText().toString());
                    if (cur != null) {
                        int idState = cur.getColumnIndex(MyDataBaseAdapter.KEY_STATE);
                        if (cur.getString(idState).equals("已完成")) {
                            myDataBaseAdapter.updateData(timeText.getText().toString(), "未完成");
                            stateText.setText("未完成");
                            imagebutton.setImageResource(R.mipmap.todo);
//                            image button.setImageDrawable(getResources().getDrawable(R.mipmap.to do));
                        } else {
                            myDataBaseAdapter.updateData(timeText.getText().toString(), "已完成");
                            stateText.setText("已完成");
                            imagebutton.setImageResource(R.mipmap.check);
//                            image button.setImageDrawable(getResources().getDrawable(R.mipmap.check));
                        }
                    }
                }
            });
            return view;
        }
    }
}
