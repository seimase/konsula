package com.konsula.app.service.model;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.konsula.app.R;
import com.konsula.app.ui.adapter.SearchResultDoctorAdapter;
import com.konsula.app.ui.custom.CustomtextView;
import com.konsula.app.util.Converter;

import java.util.List;

/**
 * Created by SamuelSonny on 30-Jan-16.
 */
public class SearchDoctorModel {

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
        public Primary primary;
    }

    public class Specialization
    {
        public String specialization_english;
        public String specialization_bahasa;
        public String degree;
        public String identity_uri;
    }

    public class Speciality
    {
        public Specialization specialization;
        public Subspecialization subspecialization;
    }

    public class Subspecialization
    {
        public String subspecialization_english;
        public String subspecialization_bahasa;
        public String degree;
        public String identity_uri;
    }

    public class Primary2
    {
        public String caption;
        public String large_image;
        public String medium_image;
        public String thumb_image;
        public Object photo_id;
    }

    public class Photos2
    {
        public Primary2 primary;
        public List<Object> others;
    }

    public class Practice
    {
        public int practice_id;
        public String practice_name;
        public String identity_uri;
        public String place_english;
        public String place_bahasa;
        public String latitude;
        public String longitude;
        public String address;
        public String zipcode;
        public String bpjs;
        public String insurance;
        public String approval;
        public int area_id;
        public String location;
        public Object distance;
        public int establish_since;
        public int years_operation;
        public String operation_total;
        public String currency;
        public String rate;
        public String summary_schedule;
        public Photos2 photos;
        public String practice_url;
        public int total_review;
        public double star_rating;
        public int first_shown;
    }

    public class Doctor
    {
        public int doctor_id;
        public int job_id;
        public String doctor_name;
        public String identity_uri;
        public String identity_card;
        public String reservation_contact;
        public String registration_number;
        public int registration_year;
        public int regulator_id;
        public int working_since;
        public String recommendation_link;
        public int desc_trans_id;
        public String profile_score;
        public String rating_score;
        public String approval;
        public String online;
        public String review;
        public String status;
        public String gender;
        public String job_english;
        public String job_bahasa;
        public String salutation_id;
        public int practice_id;
        public String currency;
        public String rate;
        public String category;
        public String latitude;
        public String longitude;
        public String area_name;
        public int city_id;
        public String city_name;
        public int state_id;
        public String state_name;
        public int country_id;
        public String country_name;
        public String country_code;
        public int specialization_id;
        public Object subspecialization_id;
        public String filename;
        public int min_rate;
        public int max_rate;
        public int monday;
        public int tuesday;
        public int wednesday;
        public int thrusday;
        public int friday;
        public int saturday;
        public int sunday;
        public String min_hour;
        public String max_hour;
        public int years_experience;
        public double distance;
        public String experience_total;
        public int total_review;
        public double star_rating;
        public Photos photos;
        public String doctor_url;
        public List<Speciality> specialities;
        public int total_practices;
        public List<Practice> practices;
    }

    public class Primary3
    {
        public String caption;
        public String large_image;
        public String medium_image;
        public String thumb_image;
    }

    public class Photos3
    {
        public Primary3 primary;
    }

    public class Sponsored
    {
        public int practice_id;
        public String practice_name;
        public String identity_uri;
        public String place_english;
        public String place_bahasa;
        public String latitude;
        public String longitude;
        public String address;
        public String zipcode;
        public String bpjs;
        public String insurance;
        public String approval;
        public int area_id;
        public String location;
        public Object distance;
        public Object establish_since;
        public int years_operation;
        public String operation_total;
        public String currency;
        public String rate;
        public String summary_schedule;
        public Photos3 photos;
        public String practice_url;
        public int total_review;
        public double star_rating;
        public int first_shown;
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
        public List<Doctor> doctors;
        public List<Object> locations;
        public List<Sponsored> sponsored;
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
