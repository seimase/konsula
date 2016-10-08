package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by Fadli Febriansyah on 5-Feb-16.
 */
public class HomeModel {

    public class Doctor
    {
        public String label;
        public String location_state;
        public String keyword;
        public String locality;
        public String latitude;
        public String longitude;
        public String img_android;
        public String img_ios;
        public String category;
    }

    public class Practices
    {
        public String label;
        public String location_state;
        public String keyword;
        public String locality;
        public String latitude;
        public String longitude;
        public String img_android;
        public String img_ios;
        public String category;
    }

    public class Results
    {
        public List<Doctor> doctors;
        public List<Practices> practices;
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

    public class Request
    {
        public List<Object> GET;
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
    public Results results;
    public Meta _meta;
    public Server _server;
    public Request _request;
    public Links _links;
    public String language;
}
