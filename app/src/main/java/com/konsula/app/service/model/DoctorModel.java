package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by hiantohendry on 1/28/16.
 */


public class DoctorModel
{
    public class Primary
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
    }

    public class Regulator
    {
        public int regulator_id;
        public String regulator_name;
        public String country_code;
        public String country_name;
    }

    public class Primary2 extends Others
    {
    }

    public class Others
    {
        public int photo_id;
        public String caption;
        public String large_image;
        public String medium_image;
        public String thumb_image;
    }

    public class Photos2
    {
        public Primary2 primary;
        public List<Others> others;
    }

    public class Schedule
    {
        public int practice_id;
        public int doctor_id;
        public String interval;
        public int max_person;
        public String currency;
        public String rate;
        public String practice_license;
        public String reservation_contact;
        public String time_zone;
        public String mon;
        public String tue;
        public String wed;
        public String thu;
        public String fri;
        public String sat;
        public String sun;
        public String summary;
        public String created_time;
        public String modified_time;
        public List<SummaryScheduleArr> summary_schedule_arr;
    }

    public class SummaryScheduleArr
    {
        public String day ;
        public String hour;
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
        public String establish_since;
        public String address;
        public int area_id;
        public String zipcode;
        public String time_zone;
        public String mon;
        public String tue;
        public String wed;
        public String thu;
        public String fri;
        public String sat;
        public String sun;
        public String min_hour;
        public String max_hour;
        public String currency;
        public String min_rate;
        public String max_rate;
        public String summary_schedule;
        public int desc_trans_id;
        public String bpjs;
        public String insurance;
        public String profile_score;
        public String rating_score;
        public String approval;
        public String online;
        public String review;
        public String status;
        public String created_time;
        public String modified_time;
        public String category_place;
        public Photos2 photos;
        public String non_full_location;
        public String location;
        public String practice_url;
        public Schedule schedule;
        public String maps_url;
        public int recommendation_total;
        public int review_total;
    }

    public class Service
    {
        public int service_id;
        public String service_english;
        public String service_bahasa;
        public String identity_uri;
        public String category;
    }

    public class Membership
    {
        public int organization_id;
        public String organization_name;
        public String organization_code;
        public String country_code;
    }

    public class Education
    {
        public int education_id;
        public int university_id;
        public String university_name;
        public String country_name;
        public String faculty;
        public String title;
        public String description;
        public String start;
        public int end;
        public String education;
    }

    public class Experience
    {
        public int experience_id;
        public String occupation;
        public String practice;
        public String start;
        public String end;
        public String experience;
    }

    public class Award
    {
        public int award_id;
        public String award_name;
        public String year;
        public String award;
    }

    public class Specialization
    {
        public int specialization_id;
        public String specialization_english;
        public String specialization_bahasa;
        public String specialization_url;
        public String degree;
        public String identity_uri;
        public String description;
    }

    public class Speciality
    {
        public Specialization specialization;
        public Subspecialization subspecialization;
    }

    public class Subspecialization{
        public int subspecialization_id;
        public String subspecialization_english;
        public String subspecialization_bahasa;
        public String degree;
        public String subspecialization_url;
        public String identity_uri;
        public String description;
    }

    public class Meta
    {
        public String title;
        public String description;
        public String image;
    }

    public class Breadcrumb
    {
        public String label;
        public String link;
    }

    public class Description
    {
        public String id;
        public String en;
        public String static_id;
        public String static_en;
    }

    public class Results
    {
        public int doctor_id;
        public int job_id;
        public String doctor_name;
        public String identity_uri;
        public String identity_card;
        public String reservation_contact;
        public String registration_number;
        public int registration_year;
        public int working_since;
        public String recommendation_link;
        public int desc_trans_id;
        public String profile_score;
        public String rating_score;
        public String approval;
        public String online;
        public String review;
        public String status;
        public String created_time;
        public String modified_time;
        public String gender;
        public String fullname;
        public String birth_date;
        public String job_bahasa;
        public String job_english;
        public String salutation_id;
        public String doctor_short_name;
        public String doctor_url;
        public String working_experience;
        public Photos photos;
        public Regulator regulator;
        public List<Practice> practices;
        public List<Service> services;
        public List<Membership> memberships;
        public List<Education> educations;
        public List<Experience> experiences;
        public List<Award> awards;
        public List<Speciality> specialities;
        public Meta meta;
        public float star_rating;
        public int total_review;
        public List<Breadcrumb> breadcrumbs;
        public Description description;
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
