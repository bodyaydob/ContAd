package client.model;

import java.io.Serializable;

public class History implements Serializable {
    private int id;
    private String user;
    private String ad;
    private String url1;
    private String url2;

    public History(int id, String user, String ad, String url1,String url2){
        this.id = id;
        this.user = user;
        this.ad = ad;
        this.url1 = url1;
        this.url2 = url2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getUrl1() {
        return url1;
    }

    public void setUrl1(String url1) {
        this.url1 = url1;
    }

    public String getUrl2() {
        return url2;
    }

    public void setUrl2(String url2) {
        this.url2 = url2;
    }
}
