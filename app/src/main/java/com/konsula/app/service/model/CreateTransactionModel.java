package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by SamuelSonny on 29-May-16.
 */
public class CreateTransactionModel {
    public class Item {
        public String item_id;
        public String item_name;
        public int quantity;
        public String currency;
        public int sell_price;
        public int max_redeem;
    }

    public class Items {
        public String order_name;
        public String currency;
        public String total_item_price;
        public List<Item> item;
    }

    public class Data {
        public String payment_uuid;
        public Items items;
        public int convenience_fee;
        public int promotion_fee;
        public int total_price;
    }

    public class Results {
        public boolean status;
        public Data data;
    }

    public class Meta {
        public double benchmark;
        public int current_page;
        public int total_page;
        public int limit;
        public int total_row;
    }

    public class Server {
        public String REQUEST_METHOD;
        public String CONTENT_TYPE;
        public String REMOTE_ADDR;
        public String HTTP_USER_AGENT;
    }

    public class Links {
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
