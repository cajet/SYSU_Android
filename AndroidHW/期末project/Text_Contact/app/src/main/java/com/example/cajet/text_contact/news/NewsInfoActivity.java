package com.example.cajet.text_contact.news;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cajet.text_contact.R;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class NewsInfoActivity extends Activity {

    private SwipeRefreshLayout mRefresh;
    private WebView mWeb;
    private String link;
    private NewsItemBiz newsItemBiz;
    private TextView mTag;

    public static final String FRAME = "<html ><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head>" +
            "<div class=\"wrapper\"><h3>%s</h3>" +
            "<div class=\"info\"><span>%s</span></div><br />" +
            "<div class=\"text\">%s</div>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_info);
        initView();
        link = getIntent().getStringExtra("link");
        newsItemBiz = new NewsItemBiz();
        mRefresh.post(new Runnable() {
            @Override
            public void run() {
                mRefresh.setRefreshing(true);
            }
        });
        new LoadDataTask().execute();
    }

    private void initView() {
        mRefresh = (SwipeRefreshLayout) findViewById(R.id.id_newsInfo_refresh);
        mWeb = (WebView) findViewById(R.id.id_newsInfo_webView);
        mTag = (TextView) findViewById(R.id.id_loadFailed);
        ImageView mBack = (ImageView) findViewById(R.id.id_imb_back);
        WebSettings mWebSettings = mWeb.getSettings();
        mWebSettings.setSupportZoom(true);
        mWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mRefresh.setRefreshing(false);
            }
        });
        mRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimary,
                R.color.colorPrimary, R.color.colorPrimary);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new LoadDataTask().execute();
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    class LoadDataTask extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... params) {
            String html = null;
            try {
                //GET请求
                StringBuffer sb = new StringBuffer();
                try {
                    URL url = new URL(link);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setDoInput(true);
                    if (conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();
                        InputStreamReader reader = new InputStreamReader(is, "UTF-8");
                        char[] ch = new char[1024];
                        int length;
                        while ((length = reader.read(ch)) != -1) {
                            sb.append(new String(ch, 0, length));

                        }
                        is.close();
                        reader.close();
                    }else{
                        throw new Exception("网络链接失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                html = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return html;
        }

        @Override
        protected void onPostExecute(String s) {
            if(!TextUtils.isEmpty(s)){
                mTag.setVisibility(View.GONE);
                NewsDetail mNews = newsItemBiz.getNewsDetail(s);
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(formatHtml(FRAME, mNews.getTitle(), mNews.getInfo(), mNews.getTexts()));
                mWeb.loadData(stringBuffer.toString(), "text/html; charset=UTF-8", null);
            }else{
                mTag.setVisibility(View.VISIBLE);
            }
            mRefresh.setRefreshing(false);
        }
    }

    /**
     * 格式化html
     *
     * @param frame 框架
     * @param title 标题
     * @param info 作者时间
     * @param texts 内容
     * @return formatHtml
     */
    private String formatHtml(String frame, String title, String info, String texts) {
        return String.format(frame, title, info, texts);

    }

    public static void actionStart(Context context, String url){
        Intent intent = new Intent(context,NewsInfoActivity.class);
        intent.putExtra("link",url);
        context.startActivity(intent);
    }
}
