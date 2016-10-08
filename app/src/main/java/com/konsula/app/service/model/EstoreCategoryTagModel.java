package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by hiantohendry on 5/19/16.
 */
public class EstoreCategoryTagModel {
    public class Messages
    {
        public int response_code;
        public String response_text;
        public List<String> response_error;
        public String response_http_status;
    }

    public class Result
    {
        public int tag_id;
        public String tag_name;
        public String identity_uri;
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
        public List<Result> results;
        public Meta _meta;
        public Server _server;
        public Links _links;
        public String language;
    
}
