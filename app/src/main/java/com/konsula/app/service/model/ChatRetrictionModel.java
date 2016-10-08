package com.konsula.app.service.model;

/**
 * Created by Konsula on 20/04/2016.
 */
public class ChatRetrictionModel {


    public class Promocode
    {
        public String promo_code ;
        public String promo_type ;
        public String currency ;
        public int nominal ;
        public Object minimum_payment ;
        public String start_date ;
        public String end_date ;
        public String start_message ;
        public String end_message ;
        public int quota ;
        public Object used ;
        public String status ;
    }

    public class Results
    {
        public String status ;
        public String category ;
        public String message ;
        public Promocode promocode ;
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