package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by hiantohendry on 2/4/16.
 */
public class DoctorScheduleModel {

    public class Slot
    {
        public String start;
        public String end;
        public boolean available;
    }

    public class Result
    {
        public String label;
        public String extra_label;
        public String day_name;
        public String date;
        public String interval;
        public String time_zone;
        public String doctor_id;
        public String practice_id;
        public List<Slot> slots;
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
