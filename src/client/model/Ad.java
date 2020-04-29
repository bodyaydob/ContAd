package client.model;

import java.io.Serializable;

public class Ad implements Serializable {
    private int id;
    private String path;
    private int priority;
    private String category;

    public Ad(int id, String path, int priority, String category) {
        this.id = id;
        this.path = path;
        this.priority = priority;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
