package client.model;

import java.io.Serializable;

public class Word implements Serializable {
    private int id;
    private String value;
    private String category;


    public Word(int id, String value, String category) {
        this.id = id;
        this.value = value;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
