package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by hiantohendry on 3/7/16.
 */
public class AppointmentDataModel {


    public class Specialization {
        public int specialization_id;
        public String specialization_english;
        public String specialization_bahasa;
        public String degree;
        public String identity_uri;
        public Object description;
        public int job_id;
        public String timestamp;
    }

    public class Subspecialization {
        public int subspecialization_id;
        public String subspecialization_english;
        public String subspecialization_bahasa;
        public String degree;
        public String identity_uri;
        public Object description;
        public int specialization_id;
        public String timestamp;
    }

    public class Speciality {
        public Specialization specialization;
        public Subspecialization subspecialization;
    }

    public class Photo {
        public int photo_id;
        public String caption;
        public String large_image;
        public String medium_image;
        public String thumb_image;
    }

    public class ProfileDoctor {
        public int doctor_id;
        public String uuid;
        public String doctor_name;
        public String doctor_url;
        public String job_category;
        public String working_experience;
        public String identity_uri;
        public List<Speciality> specialities;
        public Photo photo;
    }

    public class Photo2 {
        public int photo_id;
        public String caption;
        public String large_image;
        public String medium_image;
        public String thumb_image;
    }

    public class ProfilePractice {
        public int practice_id;
        public String practice_name;
        public String practice_url;
        public String address;
        public String category;
        public String identity_uri;
        public Photo2 photo;
    }

    public class Schedule {
        public int appointment_id;
        public int member_id;
        public int practice_id;
        public int doctor_id;
        public String day;
        public String schedule_date;
        public String hour_start;
        public String hour_end;
        public String time_zone;
        public String time_abbreviation;
        public String phone_number;
        public String reason;
        public String unique_key;
        public String appointment_code;
    }

    public class Results {
        public String type;
        public ProfileDoctor profile_doctor;
        public ProfilePractice profile_practice;
        public Schedule schedule;
        public Object reviews;
        public String allow_get_reward;
        public List<Object> rewards;
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
    public Results results;
    public Meta _meta;
    public Server _server;
    public Links _links;
    public String language;

}
