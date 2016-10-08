package com.konsula.app.service.model;

/**
 * Created by Konsula on 09/05/2016.
 */
public class MembershipCreateTransactionModel {


    public class Data {
        public String payment_uuid;
        public String item;
        public int price;
        public int convenience_fee;
        public int promotion_fee;
        public int total_price;
    }

    public class Results {
        public Boolean status;
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
