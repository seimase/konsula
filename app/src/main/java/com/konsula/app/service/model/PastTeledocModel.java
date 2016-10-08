package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by Konsula on 18/04/2016.
 */
public class PastTeledocModel {

    public class Datum
    {
        public int tele_id ;
        public String tele_uuid ;
        public int doctor_id ;
        public String doctor_name ;
        public int practice_id ;
        public String practice_name ;
        public String reason ;
        public String contact ;
        public String schedule ;
        public String currency ;
        public String rate ;
        public String penalty_status ;
        public Object duration ;
        public Object star_review ;
        public Object text_review ;
        public String tele_status ;
        public String payment_status ;
        public String created_time ;
    }

    public class Results
    {
        public Boolean success ;
        public List<Datum> data ;
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
        public Results results ;
        public Meta _meta ;
        public Server _server ;
        public Links _links ;
        public String language ;

}
