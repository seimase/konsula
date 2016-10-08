package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by hiantohendry on 2/8/16.
 */
public class RegisterModel {

    public class Data {
        public int member_id;
        public String email;
        public String fullname;
        public String gender;
        public String birth_date;
        public String phone_number;
    }

    public class Results {
        public String status;
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