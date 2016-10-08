package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by hiantohendry on 2/2/16.
 */
public class DoctorReviewModel {

    public class Summary
    {
        public int doctor_id;
        public String total_point_friendly;
        public String total_point_facility;
        public String total_point_timing;
        public String total_point_overall;
        public String review_total;
    }

    public class ReviewList
    {
        public int review_id;
        public String fullname;
        public String total_point_overall;
        public String timestamp;
        public String date_label;
        public String feedback;
        public String practice_name;
        public boolean verified_patient;
        public int total_like;
        public boolean reviewed;
    }

    public class Results
    {
        public Summary summary;
        public List<ReviewList> review_list;
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
