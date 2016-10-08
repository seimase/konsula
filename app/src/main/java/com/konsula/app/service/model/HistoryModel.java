package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by Konsula on 06/02/2016.
 */
public class HistoryModel {

    public class Result
    {
        public int appointment_id ;
        public String unique_key ;
        public int member_id ;
        public int practice_id ;
        public String practice_name ;
        public String practice_uri ;
        public int doctor_id ;
        public String doctor_name ;
        public String doctor_uri ;
        public String schedule_date ;
        public String hour_start ;
        public String hour_end ;
        public String reason ;
        public String appointment_status ;
        public String acknowledge_status ;
        public String attendance_status ;
        public String phone_number ;
        public String media ;
        public String ip_address ;
        public String appointment_code ;
        public String appointment_timestamp ;
        public Object payment_timestamp ;
        public Object review_id ;
        public boolean ready_to_review ;
        public boolean allow_to_review;
    }

    public class Meta
    {
        public double benchmark ;
        public int current_page ;
        public int total_page ;
        public int limit ;
        public int total_row ;
    }

    public class Server
    {
        public String REQUEST_METHOD ;
        public String CONTENT_TYPE ;
        public String REMOTE_ADDR ;
        public String HTTP_USER_AGENT ;
    }

    public class Links
    {
        public String self ;
        public String prev ;
        public String next ;
        public String first ;
        public String last ;
    }


        public Messages messages ;
        public List<Result> results ;
        public Meta _meta ;
        public Server _server ;
        public Links _links ;
        public String language ;


}
