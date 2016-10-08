package com.konsula.app.service.model;

import java.util.List;

public class InitSpecialityModel {


    public class Specialization {
        public int specialization_id;
        public String specialization_name;
    }

    public class Doctor {
        public Integer doctor_id;
        public Integer practice_id;
        public String doctor_name;
        public String profile_link;
        public String identity_uri;
        public String currency;
        public Integer rate;
    }

    public class Result {
        public Specialization specialization;
        public List<Doctor> doctors;
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
    public List<Result> results;
    public Meta _meta;
    public Server _server;
    public Links _links;
    public String language;
}
