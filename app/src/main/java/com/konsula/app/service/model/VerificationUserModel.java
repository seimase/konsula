package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by Konsula on 21/03/2016.
 */
public class VerificationUserModel {

//    public class Messages
//    {
//        public int response_code ;
//        public String response_text ;
//        public List<Object> response_error ;
//        public String response_http_status ;
//    }

    public class Data
    {
        public int member_id ;
        public String email ;
        public String fullname ;
        public String uuid ;
        public String phone_number ;
        public String authentication ;
        public String status ;
        public String join_time ;
        public Object modify_time ;
        public String ip_address ;
        public String last_login ;
    }

    public class Results
    {
        public Boolean success ;
        public Data data ;
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
