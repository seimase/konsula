package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by Konsula on 27/05/2016.
 */
public class MyVoucherModel {

    public class Valid {
        public String payment_uuid;
        public int order_id;
        public String practice_name;
        public String practice_identity_uri;
        public List<String> practice_contact;
        public String product_identity_uri;
        public String order_name;
        public String order_name_detail;
        public String currency;
        public String price;
        public String valid_until;
        public String voucher_code;
        public String order_timestamp;
        public String photo;
    }

    public class Used {
        public String payment_uuid;
        public int order_id;
        public String practice_name;
        public String practice_identity_uri;
        public List<String> practice_contact;
        public String product_identity_uri;
        public String order_name;
        public String order_name_detail;
        public String currency;
        public String price;
        public String valid_until;
        public String voucher_code;
        public String order_timestamp;
        public String photo;
    }

    public class Results {
        public List<Valid> valid;
        public List<Used> used;
    }

    public class Meta {
        public double benchmark;
        public int current_page;
        public int total_page;
        public int limit;
        public int total_row;
    }

    public class Server {
        public String REQUEST_METHOD;
        public String CONTENT_TYPE;
        public String REMOTE_ADDR;
        public String HTTP_USER_AGENT;
    }

    public class Links {
        public String self;
        public String prev;
        public String next;
        public String first;
        public String last;
    }


    public Messages messages;
    public Results results;
    public Meta _meta;
    public Server _server;
    public Links _links;
    public String language;


}
