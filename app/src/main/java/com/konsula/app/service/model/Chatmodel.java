package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by Konsula on 03/03/2016.
 */
public class Chatmodel extends BaseApiModel{

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
        public String REQUEST_METHOD;
        public String CONTENT_TYPE;
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


        public Boolean results ;
        public Meta _meta ;
        public Server _server ;
        public Links _links ;
        public String language ;


}
