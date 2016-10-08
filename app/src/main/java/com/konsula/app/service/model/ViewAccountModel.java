package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by Konsula on 03/02/2016.
 */
public class ViewAccountModel {
    public class Primary
    {
        public Object photo_id ;
        public String caption ;
        public String large_image ;
        public String medium_image ;
        public String thumb_image ;
    }

    public class Photo
    {
        public List<Object> others ;
        public Primary primary ;
    }

    public class Results
    {
        public int member_id ;
        public String email ;
        public String fullname ;
        public String api_key ;
        public String gender ;
        public String birth_date ;
        public String secondary_contact ;
        public String address ;
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
        public String join_time ;
        public Object modify_time ;
        public Object ip_address ;
        public Object last_login ;
        public Photo photo ;
        public String phone_number ;
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
        public String CONTENT_TYPE  ;
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
