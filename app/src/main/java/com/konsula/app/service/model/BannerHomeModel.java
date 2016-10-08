package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by Konsula on 13/05/2016.
 */
public class BannerHomeModel {


    public class Result
    {
        public String title ;
        public String description ;
        public String link ;
        public String start_date ;
        public String end_date ;
        public int expired_timestamp ;
        public String image_url ;
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
