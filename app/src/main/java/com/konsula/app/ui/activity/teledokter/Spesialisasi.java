package com.konsula.app.ui.activity.teledokter;

/**
 * Created by konsula on 3/4/2016.
 */
public class Spesialisasi {

    String name = null;
    int id;
    boolean selected = false;

    public Spesialisasi(int id, String name, boolean selected) {
        super();
        this.name = name;
        this.id = id;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}