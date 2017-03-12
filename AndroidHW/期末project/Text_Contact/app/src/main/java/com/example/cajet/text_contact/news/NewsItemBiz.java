package com.example.cajet.text_contact.news;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


class NewsItemBiz {

    List<NewsItem> getNewsItems(int curPage)
            throws Exception {
        List<NewsItem> newsItems = new ArrayList<>();
        String _url = "http://news.csdn.net/news/"+curPage;
        StringBuffer sb = new StringBuffer();
        //GET请求
        try {
            URL url = new URL(_url);
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
        String htmlStr = sb.toString();

        NewsItem item;
        Document doc = Jsoup.parse(htmlStr);
        Elements units = doc.getElementsByClass("unit");
        for (int i = 0; i < units.size(); i++) {
            item = new NewsItem();
            Element unit = units.get(i);
            Element h1 = unit.getElementsByTag("h1").get(0);
            Element ha = h1.child(0);
            item.setTitle(h1.text());
            item.setLink(ha.attr("href"));
            Element h4 = unit.getElementsByTag("h4").get(0);
            Element ago = h4.getElementsByClass("ago").get(0);
            item.setDate(ago.text());
            Element dl_ele = unit.getElementsByTag("dl").get(0);
            Element dt_ele = dl_ele.child(0);
            try {
                // 可能没有图片
                Element img_ele = dt_ele.child(0);
                String imgLink = img_ele.child(0).attr("src");
                item.setImgLink(imgLink);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            Element dd_ele = dl_ele.child(1);
            item.setContent(dd_ele.text());
            newsItems.add(item);
        }

        return newsItems;
    }

    NewsDetail getNewsDetail(String html) {
        NewsDetail news = new NewsDetail();
        Document doc = Jsoup.parse(html);
        // 获得文章中的第一个detail
        Element detail = doc.select("div.wrapper").first();
        //获取title
        Element title = detail.select("h1").first();
        news.setTitle(title.text());
        //info
        Element info = detail.select("div.info").first();
        news.setInfo(info.text());
        Elements elements = detail.select(".text p");
        StringBuffer buffer = new StringBuffer();
        for (Element element : elements) {
            element.select("img").attr("width", "100%").attr("style", "");
            buffer.append("<p>");
            buffer.append(element.html());
            buffer.append("</p>");
            Log.i("clj", buffer.toString());
        }
        news.setTexts(buffer.toString());
        return news;
    }

}
