package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by SamuelSonny on 28-Jan-16.
 */
public class InitSearchModel {
    public class LocationDropdown {
        public String location_type;
        public int id;
        public String location_name;
        public String country_uri;
        public String state_uri;
        public String locality_uri;
        public String zipcode;
    }

//    public class SpecialityDropdown {
//        public String search_type;
//        public String category;
//        public int id;
//        public String english_name;
//        public String bahasa_name;
//        public String identity_uri;
//        public String target;
//        public String search_uri;
//    }
//
//    public class PracticeDropdown {
//        public String search_type;
//        public String category;
//        public int id;
//        public String english_name;
//        public String bahasa_name;
//        public String identity_uri;
//        public String target;
//        public String search_uri;
//    }

    public class Results {
        public List<LocationDropdown> location_dropdown;
        public List<MultiSearchModel.Result> speciality_dropdown;
        public List<MultiSearchModel.Result> practice_dropdown;
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

    public class Request {
        public List<Object> GET;
        public List<Object> POST;
        public List<Object> PUT;
        public List<Object> DELETE;
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
    public Request _request;
    public Links _links;
    public String language;
}
