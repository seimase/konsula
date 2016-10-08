package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by SamuelSonny on 13-Feb-16.
 */
public class AppointmentListModel {

    public class Result
    {
        public int appointment_id;
        public int practice_id;
        public String practice_name;
        public String practice_address;
        public String practice_url;
        public String practice_location;
        public int doctor_id;
        public String doctor_name;
        public String doctor_url;
        public String schedule_date;
        public String hour_start;
        public String hour_end;
        public String time_abbreviation;
        public String reason;
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
    public List<Result> results;
    public Meta _meta;
    public Server _server;
    public Request _request;
    public Links _links;
    public String language;
}
