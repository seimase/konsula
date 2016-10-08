package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by Konsula on 26/04/2016.
 */
public class BankListModel {



    public class Result
    {
        public int bank_id ;
        public String bank_name ;
        public String image;
        public String branch_name ;
        public String account_holder_name ;
        public String account_number ;
        public String status ;
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
