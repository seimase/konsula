package com.konsula.app.ui.activity.pembiayaan;

/**
 * Created by konsula on 3/4/2016.
 */
public class Jaminan {

    String name = null;
    boolean selected = false;

    public Jaminan(String name, boolean selected) {
        super();
        this.name = name;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}