package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by SamuelSonny on 28-Jan-16.
 */
public class LocationSearchModel {

    public class Result
    {
        public String location_type;
        public int id;
        public String location_name;
        public String country_uri;
        public String state_uri;
        public String locality_uri;
        public String zipcode;
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

    public class GET
    {
        public String keyword;
    }

    public class Request
    {
        public GET GET;
        public List<Object> POST;
        public List<Object> PUT;
        public List<Object> DELETE;
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
    public Request _request;
    public Links _links;
    public String language;
}
