package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by SamuelSonny on 29-May-16.
 */
public class SummaryEstoreModel {
    public class Item
    {
        public String item_uuid;
        public String sku;
        public String item_name;
        public double discount;
        public String currency;
        public int gimmick_price;
        public Double sell_price;
        public String quantity;
        public Double total_price;
        public int quota;
        public int sold;
        public int max_redeem;
        public String description;
    }

    public class Results
    {
        public String product_uuid;
        public String product_name;
        public List<Item> items;
        public int convenience_fee;
        public Double total_price;
        public String signature_key;
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
