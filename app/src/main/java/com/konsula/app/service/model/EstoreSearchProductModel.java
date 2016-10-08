package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by hiantohendry on 4/28/16.
 */
public class EstoreSearchProductModel {

    public class Map
    {
        public Double longitude;
        public Double latitude;
        public String practice_name;
    }

    public class Primary
    {
        public int photo_id;
        public String caption;
        public String large_image;
        public String medium_image;
        public String thumb_image;
    }

    public class Others
    {
        public int photo_id;
        public String caption;
        public String large_image;
        public String medium_image;
        public String thumb_image;
    }

    public class Photos
    {
        public Primary primary;
        public List<Others> others;
    }

    public class Practice
    {
        public String practice_name;
        public String practice_uuid;
        public int total_review;
        public float star_rating;
        public Double latitude;
        public Double longitude;
        public double distance;
    }

    public class Item
    {
        public String product_uuid;
        public String product_name;
        public String identity_uri;
        public int category_id;
        public int practice_id;
        public String expiry_date;
        public String created_time;
        public Photos photos;
        public Practice practice;
        public int product_sold;
        public Double product_min_price;
        public Double product_max_price;
    }

    public class Results
    {
        public List<Map> maps;
        public List<Item> items;
    }

    public class Meta
    {
        public double benchmark;
        public int current_page;
        public int total_page;
        public int limit;
        public int total_row;
    }

    public class Server
    {
        public String REQUEST_METHOD;
        public String CONTENT_TYPE;
        public String REMOTE_ADDR;
        public String HTTP_USER_AGENT;
    }

    public class Links
    {
        public String self;
        public String prev;
        public String next;
        public String first;
        public String last;
    }

        public Messages messages;
        public Results results;
        public Meta _meta;
        public Server _server;
        public Links _links;
        public String language;
    

}
