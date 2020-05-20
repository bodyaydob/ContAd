package client.model;

import java.io.Serializable;

public class History implements Serializable {
    private int id;
    private String user;
    private String userGroup;
    private String ad;
    private String urls;
    private int rate;
    private boolean click;

    public History(int id, String user, String userGroup, String ad, String[] urls, int rate, boolean click){
        this.id = id;
        this.user = user;
        this.userGroup = userGroup;
        this.ad = ad;
        this.urls = "";
        for(String url: urls)
            this.urls = this.urls + url + "; ";
        this.rate = rate;
        this.click = click;
    }

    public int getId() {
        return id;
    }
    public String getUser() {
        return user;
    }
    public String getAd() {
        return ad;
    }
    public String getUrls() {
        return urls;
    }
    public int getRate() { return rate; }
    public boolean getClick() { return  click; }
    public String getUserGroup() {return userGroup;}

    public void setId(int id) {
        this.id = id;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public void setAd(String ad) {
        this.ad = ad;
    }
    public void setUrls(String urls) {
        this.urls = urls;
    }
    public void setRate(int rate) {this.rate = rate; }
    public void setClick(boolean click) {this.click = click; }
    public void setUserGroup(String userGroup) {this.userGroup = userGroup; }
}
