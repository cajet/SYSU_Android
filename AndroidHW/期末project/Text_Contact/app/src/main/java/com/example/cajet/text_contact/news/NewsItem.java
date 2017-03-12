package com.example.cajet.text_contact.news;


class NewsItem {

    private int id;
    //标题
    private String title;
    //链接
    private String link;
    //图片连接
    private String imgLink;
    //内容
    private String Content;
    //发布时间
    private String date;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    String getTitle() {
        return title;
    }
    void setTitle(String title) {
        this.title = title;
    }
    String getLink() {
        return link;
    }
    void setLink(String link) {
        this.link = link;
    }
    String getImgLink() {
        return imgLink;
    }
    void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }
    public String getContent() {
        return Content;
    }
    public void setContent(String content) {
        Content = content;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "newsItem[id="+id+",title="+title+",link="+link+",imgLink="+imgLink
                +",content="+Content+",date="+date;
    }
}
