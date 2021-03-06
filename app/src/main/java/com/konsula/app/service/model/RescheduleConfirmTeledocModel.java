package com.konsula.app.service.model;

/**
 * Created by Konsula on 28/04/2016.
 */
public class RescheduleConfirmTeledocModel {

    public class Primary {
        public int photo_id;
        public String caption;
        public String large_image;
        public String medium_image;
        public String thumb_image;
    }

    public class DoctorPhoto {
        public Primary primary;
    }

    public class Data {
        public int tele_id;
        public String tele_uuid;
        public int doctor_id;
        public String doctor_name;
        public String doctor_specialization;
        public DoctorPhoto doctor_photo;
        public int practice_id;
        public String practice_name;
        public String member_uuid;
        public String member_name;
        public String member_gender;
        public String member_email;
        public String member_birth_date;
        public String reason;
        public String contact;
        public String schedule;
        public String duration;
        public String rate;
        public String penalty;
        public long star_review;
        public String text_review;
        public String tele_status;
        public String payment_status;
        public String billing_status;
        public String modified_time;
    }

    public class Results {
        public Boolean success;
        public Data data;
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
