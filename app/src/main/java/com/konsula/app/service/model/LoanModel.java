package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by fadli on 2/15/16.
 */
public class LoanModel {
    public class Messages
    {
        public int response_code;
        public String response_text;
        public List<Object> response_error;
        public String response_http_status;
    }

    public class Results
    {
        public String provider;
        public int loan;
        public int tenor;
        public int installment;
        public String installment_format;
        public String guarantee;
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
