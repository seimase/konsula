package com.konsula.app.service.model;

import java.util.List;
import java.util.Objects;

/**
 * Created by Konsula on 04/02/2016.
 */
public class PhotoModel {


    public class Primary
    {
        public String photo_id ;
        public String caption ;
        public String large_image ;
        public String medium_image ;
        public String thumb_image ;
    }

    public class Results
    {
        public List<Object> others ;
        public Primary primary ;
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
