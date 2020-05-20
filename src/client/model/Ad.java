package client.model;

import java.io.Serializable;

public class Ad implements Serializable {
    private int id;
    private String path;
    private int priority;
    private String category;
    private double avRate;
    private int displCnt;

    public Ad(int id, String path, int priority, String category, int rate, int displCnt) {
        this.id = id;
        this.path = path;
        this.priority = priority;
        this.category = category;
        try {
            this.avRate = rate / displCnt;
        } catch (ArithmeticException e){
            this.avRate = rate;
        }
        this.displCnt = displCnt;
    }

    public int getId() { return id; }
    public String getPath() { return path; }
    public int getPriority() { return priority; }
    public String getCategory() { return category; }
    public double getAvRate() { return avRate; }
    public int getDisplCnt() { return displCnt; }

    public void setId(int id) { this.id = id; }
    public void setPath(String path) { this.path = path; }
    public void setPriority(int priority) { this.priority = priority; }
    public void setCategory(String category) { this.category = category; }
    public void setAvRate(double avRate) { this.avRate = avRate; }
    public void setDisplCnt(int displCnt) { this.displCnt = displCnt; }

}
