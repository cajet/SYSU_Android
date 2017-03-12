package com.example.cajet.lab9;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.helpers.XMLReaderFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends Activity {

    private TextView air_quality, humidity, show_city, tem_now, tem_today, update_time, wind;
    private EditText city;
    private ListView lv;
    private Button search;
    private static final int UPDATE_CONTENT = 0;
    private static final String url = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather";
    private Handler handler= new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE_CONTENT:
                    //处理子线程得到的ArrayList<String>数据
                    ArrayList<String> str = (ArrayList<String>)message.obj;
                    int i = 0;
                    String _temp= "";
                    while (i< str.size()) {
                        _temp+= str.get(i);
                        i++;
                    }
                    if (_temp.equals("查询结果为空。http://www.webxml.com.cn/")) {
                        Toast.makeText(MainActivity.this, "当前城市不存在，请重新输入", Toast.LENGTH_LONG).show();
                    } else if (_temp.equals("发现错误：免费用户不能使用高速访问。http://www.webxml.com.cn/")) {
                        Toast.makeText(MainActivity.this, "您的点击速度过快，二次查询间隔<600ms", Toast.LENGTH_LONG).show();
                    } else if (_temp.equals("发现错误：免费用户24小时内访问超过规定数量。http://www.webxml.com.cn/")) {
                        Toast.makeText(MainActivity.this, "免费用户24小时内访问超过规定数量50次", Toast.LENGTH_LONG).show();
                    } else {
                        setView(str); // 把数据更新到UI界面
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setListener();
    }

    private void init() {
        city= (EditText) findViewById(R.id.city);
        search= (Button) findViewById(R.id.search);
        show_city= (TextView) findViewById(R.id.show_city);
        update_time= (TextView) findViewById(R.id.update_time);
        tem_now= (TextView) findViewById(R.id.temperature_now);
        tem_today= (TextView) findViewById(R.id.temperature_today);
        humidity= (TextView) findViewById(R.id.humidity);
        air_quality= (TextView) findViewById(R.id.air_quality);
        wind= (TextView) findViewById(R.id.wind);
        lv= (ListView) findViewById(R.id.list_view);
    }


    private void setListener() {
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvaliable()) {
                    Toast.makeText(MainActivity.this, "当前没有可用网络！", Toast.LENGTH_SHORT).show();
                } else {
                    sendRequest();
                }
            }
        });
    }

    private boolean isNetworkAvaliable() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    private void sendRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection= null;
                try {
                    Log.i("key", "Begin the connection");
                    connection= (HttpURLConnection) ((new URL(url.toString()).openConnection()));
                    connection.setRequestMethod("POST");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    String request= URLEncoder.encode(city.getText().toString(), "utf-8");
                    out.writeBytes("theCityCode=" + request + "&theUserID=");
                    InputStream in= connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response= new StringBuilder();
                    String temp;
                    while ((temp= reader.readLine())!= null) {
                        response.append(temp);
                    }
                    //解析XML,并把结果传给Message，用于UI界面更新
                    Message message= new Message();
                    message.what= UPDATE_CONTENT;
                    message.obj= parseXMLWithPull(response.toString());
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection!= null)
                        connection.disconnect();
                }
            }
        }).start();
    }

    private ArrayList<String> parseXMLWithPull(String response) {
        ArrayList<String> list = new ArrayList<>();
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new StringReader(response));
            int eventType= parser.getEventType();
            while (eventType!= XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("string".equals(parser.getName())) {
                            String str= parser.nextText();
                            list.add(str);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventType= parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //把ArrayList数据放入对应布局中
    private void setView(ArrayList<String> str) {
        show_city.setText(city.getText().toString());
        String[] temp= str.get(3).split(" ");
        update_time.setText(temp[1]+" 更新");
        temp= str.get(4).split("；");
        tem_now.setText(temp[0].substring(10, temp[0].length()));
        wind.setText(temp[1].substring(6, temp[1].length()));
        humidity.setText("湿度："+temp[2].substring(3, temp[2].length()));
        temp= str.get(5).split("。");
        air_quality.setText("空气质量："+temp[1].substring(5, temp[1].length()));
        temp= str.get(8).split("/");
        tem_today.setText(temp[0]+"/"+temp[1]);
        temp= str.get(6).split("。");

        String[] zhishu= {"紫外线指数", "感冒指数", "穿衣指数", "洗车指数", "运动指数", "空气污染指数"};
        String[] text= {temp[0].substring(6, temp[0].length()),
                        temp[1].substring(5, temp[1].length()),
                        temp[2].substring(5, temp[2].length()),
                        temp[3].substring(5, temp[3].length()),
                        temp[4].substring(5, temp[4].length()),
                        temp[5].substring(7, temp[5].length())};
        ArrayList list= new ArrayList();
        for (int i= 0; i<= 5; i++) {
            HashMap map= new HashMap();
            map.put("para", zhishu[i]);
            map.put("text", text[i]);
            list.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter (this, list, R.layout.list_item, new String[] {"para","text"}, new int[] {R.id.parameter, R.id.description});
        lv.setAdapter(adapter);
    }

}
