package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by SamuelSonny on 29-Jan-16.
 */
public class MultiSearchModel {

    public static final String SEARCH_TYPE_DOCTOR = "doctor";
    public static final String SEARCH_TYPE_PRACTICE = "practice";
    public static final String TARGET_SEARCH_PAGE = "search-page";
    public static final String TARGET_PROFILE_PAGE = "profile-page";

    public class Result
    {
        public String search_type;
        public String category;
        public int id;
        public String english_name;
        public String bahasa_name;
        public String identity_uri;
        public String target;
        public String search_uri;
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
        public String search_type;
        public String keyword;
        public String location;
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
