package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by Konsula on 26/04/2016.
 */
public class HistoryTransactionModel {



    public class Result
    {
        public String item_name ;
        public String item_description ;
        public String item_category ;
        public String next_action ;
        public String payment_status_label ;
        public String payment_status ;
        public String payment_uuid ;
        public String currency ;
        public int amount ;
        public int convenience_fee ;
        public int promotion_fee ;
        public int total_payment ;
        public String promo_code ;
        public String processing_status ;
        public int invoice_number ;
        public String created_timestamp ;
        public String settlement_timestamp ;
        public long expiry_countdown ;
        public  String expired_status;
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
