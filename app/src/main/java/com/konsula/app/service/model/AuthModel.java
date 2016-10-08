package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by hiantohendry on 1/27/16.
 */

public class AuthModel {



    public class Results
    {
        public String session_type ;
        public String uuid ;
        public int member_id ;
        public String phone_number ;
        public String email ;
        public String fullname ;
        public String gender ;
        public String birth_date ;
        public String address ;
        public String photo ;
        public String country ;
        public String zipcode ;
        public int height ;
        public int weight ;
        public String blood_type ;
        public String blood_rhesus ;
        public String company_code ;
        public String source ;
        public String newsletter ;
        public String authentication ;
        public String status ;
        public String token ;
        public String token_expireat ;
        public String refresh_token ;
        public String refresh_token_expireat ;
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