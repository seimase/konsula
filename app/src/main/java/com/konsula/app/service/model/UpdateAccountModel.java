package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by Konsula on 04/02/2016.
 */
public class UpdateAccountModel {

    public class Data
    {
        public String fullname ;
        public String gender ;
        public String birth_date ;
        public String phone_number ;
        public String secondary_contact ;
        public String newsletter ;
        public String address ;
        public String zipcode ;
        public String height ;
        public String weight ;
        public String blood_type ;
        public String blood_rhesus ;
        public String authentication ;
    }

    public class Results
    {
        public String status ;
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
