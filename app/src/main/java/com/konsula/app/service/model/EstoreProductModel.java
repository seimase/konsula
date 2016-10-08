package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by hiantohendry on 5/2/16.
 */
public class EstoreProductModel {

    public class Practice {
        public int practice_id;
        public String uuid;
        public String practice_name;
        public String identity_uri;
        public String practice_url;
        public String latitude;
        public String longitude;
        public List<String> contact;
        public String address;
        public String zipcode;
        public String rating_score;
    }


    public class ProductPhoto {
        public int photo_id;
        public int owner_id;
        public String category;
        public String filename;
        public String caption;
        public int primary;
        public int sequence;
        public String status;
        public String timestamp;
        public String url;
    }

    public class ProductsItem {
        public int item_id;
        public String item_uuid;
        public int product_id;
        public int amount;
        public String sku;
        public String item_name;
        public String currency;
        public Double net_price;
        public Float sell_price;
        public Double gimmick_price;
        public int commision;
        public float discount;
        public int desc_trans_id;
        public String description;
        public int quota;
        public int available_quota;
        public int sold;
        public int max_redeem;
        public int created_by;
        public String status;
        public String created_time;
        public String modified_time;
    }

    public class SimilarProduct {
        public int product_id;
        public String product_uuid;
        public String product_name;
        public String identity_uri;
        public Double price;
        public String rating_score;
        public String primary_photo;
    }

    public class Data {
        public int product_id;
        public String product_uuid;
        public String product_name;
        public String identity_uri;
        public int category_id;
        public String description;
        public String howto;
        public int valid_days;
        public String expiry_date;
        public int created_by;
        public String status;
        public String created_time;
        public String modified_time;
        public int gimmick_price;
        public Double sell_price;
        public Practice practice;
        public List<ProductPhoto> product_photo;
        public List<PracticeReview> practice_review;
        public List<ProductsItem> products_items;
        public List<SimilarProduct> similar_product;
    }

    public class Results {
        public Boolean success;
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

    public class PracticeReview {
        public String reviewer_name;
        public String feedback;
        public float point_total;
        public String timestamp;
    }

    public Messages messages;
    public Results results;
    public Meta _meta;
    public Server _server;
    public Links _links;
    public String language;

}
