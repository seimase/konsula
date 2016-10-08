package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by SamuelSonny on 30-Jan-16.
 */
public class SearchKlinikModel {
    public static final String NA = "N/A";

    public class Primary
    {
        public Object photo_id;
        public String caption;
        public String large_image;
        public String medium_image;
        public String thumb_image;
    }

    public class Photos
    {
        public List<Object> others;
        public Primary primary;
    }

    public class Practice
    {
        public int practice_id;
        public String practice_name;
        public String identity_uri;
        public int category_id;
        public String latitude;
        public String longitude;
        public String reservation_contact;
        public String operational_license;
        public int establish_since;
        public String address;
        public int area_id;
        public String zipcode;
        public Object time_zone;
        public Object mon;
        public Object tue;
        public Object wed;
        public Object thu;
        public Object fri;
        public Object sat;
        public Object sun;
        public Object min_hour;
        public Object max_hour;
        public String currency;
        public String min_rate;
        public String max_rate;
        public Object summary_schedule;
        public int desc_trans_id;
        public String bpjs;
        public String insurance;
        public String profile_score;
        public String rating_score;
        public String approval;
        public String online;
        public String review;
        public String status;
        public String place_english;
        public String place_bahasa;
        public String area_name;
        public int city_id;
        public String city_name;
        public int state_id;
        public String state_name;
        public int country_id;
        public String country_name;
        public String country_code;
        public int years_operation;
        public Object distance;
        public String operation_total;
        public String location;
        public String practice_url;
        public Photos photos;
        public int total_review;
        public double star_rating;
    }

    public class Keyword
    {
        public String id;
        public String en;
    }

    public class Label
    {
        public String state_uri;
        public String locality_uri;
        public String keyword_uri;
        public Keyword keyword;
    }

    public class Results
    {
        public List<Practice> practices;
        public List<Object> locations;
        public Label label;
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
        public String country;
        public String location_state;
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
    public Results results;
    public Meta _meta;
    public Server _server;
    public Request _request;
    public Links _links;
    public String language;
}
