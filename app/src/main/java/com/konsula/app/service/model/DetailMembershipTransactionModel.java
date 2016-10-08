package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by Konsula on 09/05/2016.
 */
public class DetailMembershipTransactionModel {
    public class OrderTransaction {
        public int order_id;
        public String order_uuid;
        public int member_id;
        public int practice_id;
        public String order_status;
        public String order_timestamp;
    }

    public class Detail {
        public int detail_id;
        public int item_id;
        public int quantity;
        public int sell_price;
        public int max_redeem;
        public String valid_until;
        public String voucher_code;
    }

    public class Item {
        public String order_type;
        public String order_name;
        public String order_name_detail;
        public String currency;
        public Long price;
        public Detail detail;
    }

    public class Data {
        public String payment_uuid;
        public int invoice_number;
        public String currency;
        public int amount;
        public int convenience_fee;
        public String promo_code;
        public int promotion_fee;
        public int total_payment;
        public String processing_status;
        public String payment_status;
        public String created_timestamp;
        public String expiry_timestamp;
        public String category;
        public OrderTransaction order_transaction;
        public String subcategory;
        public String user_lang;
        public List<Item> items;
        public String signature_key;
        public int expiry_countdown;
    }

    public class Results {
        public Boolean status;
        public Data data;
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
