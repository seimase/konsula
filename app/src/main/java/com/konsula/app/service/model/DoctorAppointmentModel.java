package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by hiantohendry on 2/6/16.
 */
public class DoctorAppointmentModel {

    public class Primary
    {
        public int photo_id;
        public String caption;
        public String large_image;
        public String medium_image;
        public String thumb_image;
    }

    public class Others
    {
        public int photo_id;
        public String caption;
        public String large_image;
        public String medium_image;
        public String thumb_image;
    }

    public class Photos
    {
        public Primary primary;
        public List<Others> others;
    }

    public class Specialization
    {
        public int specialization_id;
        public String specialization_english;
        public String specialization_bahasa;
        public String degree;
        public String identity_uri;
        public String description;
        public int job_id;
        public String timestamp;
    }

    public class Speciality
    {
        public Specialization specialization;
        public Subspecialization subspecialization;
    }

    public class Description
    {
        public String id;
        public String en;
    }

    public class Doctor
    {
        public int id;
        public String url;
        public String name;
        public Photos photos;
        public int recommendation_total;
        public int review_total;
        public String working_experience;
        public String job_category;
        public List<Speciality> specialities;
        public Description description;
    }
    public class Subspecialization
    {
        public int subspecialization_id;
        public String subspecialization_english;
        public String subspecialization_bahasa;
        public String degree;
        public String identity_uri;
        public String description;
    }


    public class Practice
    {
        public int id;
        public String url;
        public String map_url;
        public String name;
        public String address;
    }

    public class Schedule
    {
        public String date;
        public String day;
        public String hour_start;
        public String hour_end;
    }

    public class Results
    {
        public Doctor doctor;
        public Practice practice;
        public Schedule schedule;
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
