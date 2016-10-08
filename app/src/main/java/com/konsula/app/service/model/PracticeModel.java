package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by hiantohendry on 1/30/16.
 */
public class PracticeModel {

    public class Category
    {
        public String id;
        public String en;
    }

    public class ServicesOfDoctor
    {
        public String service_bahasa;
        public String service_english;
        public String identity_uri;
    }

    public class Others
    {
        public int photo_id;
        public String caption;
        public String large_image;
        public String medium_image;
        public String thumb_image;
    }

    public class Primary extends Others
    {

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

    public class Photos
    {
        public List<Others> others;
        public Primary primary;
    }

    public class JobCategory
    {
        public String id;
        public String en;
    }

    public class Specialization
    {
        public int specialization_id;
        public String specialization_english;
        public String specialization_bahasa;
        public String degree;
        public String identity_uri;
        public String description;
    }

    public class Speciality
    {
        public Specialization specialization;
        public Subspecialization subspecialization;
    }

    public class Primary2
    {
        public String caption;
        public String large_image;
        public String medium_image;
        public String thumb_image;
        public int photo_id;
    }

    public class Photos2
    {
        public Primary2 primary;
        public List<Others2> others;
    }

    public class Others2
    {
        public int photo_id;
        public String caption;
        public String large_image;
        public String medium_image;
        public String thumb_image;
    }


    public class Doctor
    {
        public int doctor_id;
        public String doctor_name;
        public JobCategory job_category;
        public List<Speciality> specialities;
        public String identity_uri;
        public String doctor_url;
        public String experience_total;
        public String currency;
        public String rate;
        public String online;
        public String review;
        public float star_rating;
        public int total_review;
        public String summary;
        public Photos2 photos;
    }

    public class Service
    {
        public Integer service_id;
        public String service_english;
        public String service_bahasa;
        public String identity_uri;
        public String category;
        public String timestamp;
    }

    public class KonsulaCampaign
    {
        public String id;
        public String en;
    }

    public class Description
    {
        public String id;
        public String en;
        public KonsulaCampaign konsula_campaign;
    }

    public class Breadcrumb
    {
        public String label;
        public String link;
    }

    public class Meta
    {
        public String title;
        public String description;
        public String image;
    }

    public class Results
    {
        public int practice_id;
        public String practice_name;
        public Category category;
        public String identity_uri;
        public String practice_url;
        public String latitude;
        public String longitude;
        public String maps_url;
        public String location;
        public String non_full_location;
        public int establish_since;
        public String years_of_operation;
        public String currency;
        public String rate;
        public List<ServicesOfDoctor> services_of_doctor;
        public String online;
        public String review;
        public float star_rating;
        public String summary_schedule;
        public List<SummaryScheduleArr> summary_schedule_arr;
        public int total_review;
        public String summary;
        public Photos photos;
        public List<Doctor> doctors;
        public List<Service> services;
        public List<Insurance> insurances;
        public Description description;
        public List<Breadcrumb> breadcrumbs;
        public Meta meta;
    }

    public class Insurance
    {
        public Integer insurance_id ;
        public String insurance_name ;
        public String country_code ;
    }

    public class SummaryScheduleArr
    {
        public String day ;
        public String hour;
    }

    public class Meta2
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
        public List<String> GET;
        public List<String> POST;
        public List<String> PUT;
        public List<String> DELETE;
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
    public Meta2 _meta;
    public Server _server;
    public Request _request;
    public Links _links;
    public String language;
}
