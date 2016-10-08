package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by hiantohendry on 5/2/16.
 */
public class EstoreProductItemModel {

    public class ProductsItem
    {
        public String item_uuid;
        public String sku;
        public String item_name;
        public String currency;
        public Float sell_price;
        public Double gimmick_price;
        public float discount;
        public String description;
        public int quota;
        public int available_quota;
        public int sold;
        public int max_redeem;
        public int amount;
    }


    public class Data
    {
        public String product_uuid;
        public String product_name;
        public List<ProductsItem> items;
    }

    public class Results
    {
        public Boolean success;
        public Data data;
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
    public class PracticeReview
    {
        public String reviewer_name;
        public String feedback;
        public String point_total;
        public String timestamp;
    }
        public Messages messages;
        public Results results;
        public Meta _meta;
        public Server _server;
        public Links _links;
        public String language;

}
