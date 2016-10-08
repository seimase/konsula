package com.konsula.app.service.model;

/**
 * Created by Konsula on 26/04/2016.
 */
public class PromoCodeModel {

    public class Data {
        public String promo_code;
        public String promo_type;
        public String currency;
        public Double nominal;
        public Float minimum_payment;
        public String restrict;
        public String start_date;
        public String end_date;
        public String start_time;
        public String end_time;
        public String description;
        public Integer quota;
        public Integer used;
        public String status;
    }

    public class Results {
        public Boolean status;
        public Data data;
    }

    public class Meta {
        public Double benchmark;
        public Integer current_page;
        public Integer total_page;
        public Integer limit;
        public Integer total_row;
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
