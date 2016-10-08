package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by konsula on 3/14/2016.
 */
public class PembiayaanModel {

    public class Data
    {
        public String loan_uuid;
        public String member_uuid;
        public String fullname;
        public String email;
        public String contact;
        public Object address;
        public String provider;
        public String loan;
        public String tenor;
        public String guarantee;
        public String installment;
        public String response;
        public String followup_uuid;
        public String status;
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

    public Messages messages;
    public Results results;
    public Meta _meta;
    public Server _server;
    public Links _links;
    public String language;
}
