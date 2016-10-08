package com.konsula.app.service.model;

import java.util.HashMap;
import java.util.Map;

public class HomeBaseModel extends BaseModel {
    private Map<String, Object> map = new HashMap<String,Object>();

    public Map<String, Object> getMap() {
        return map;
    }

    public void setProperty(String key, Object value){
        map.put(key,value);
    }

    public void setProperty(String key, String value){
        map.put(key,value);
    }

    public void setProperty(String key, int value){
        map.put(key, value);
    }

    public Object getObject(String key){
        return map.get(key);
    }

    public String getProperty(String key){
        return (String)map.get(key);
    }

    public Integer getPropertyInt(String key){
        return (Integer)map.get(key);
    }

    public Boolean getPropertyBoolean(String key){
        return (Boolean)map.get(key);
    }
}
