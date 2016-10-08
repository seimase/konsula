package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by hiantohendry on 4/22/16.
 */
public class EstoreProductCatalogModel {

    public class Primary
    {
        public int photo_id;
        public String caption;
        public String large_image;
        public String medium_image;
        public String thumb_image;
    }

    public class Others
    {
        public int photo_id;
        public String caption;
        public String large_image;
        public String medium_image;
        public String thumb_image;
    }

    public class Photos
    {
        public Primary primary;
        public List<Others> others;
    }

    public class Practice
    {
        public int practice_id;
        public String uuid;
        public String practice_name;
        public int total_review;
        public float star_rating;
        public String identity_uri;
    }

    public class Description
    {
        public int desc_trans_id;
        public String translate_type;
        public String output_id;
        public String output_en;
    }

    public class Howto
    {
        public int howto_trans_id;
        public String translate_type;
        public String output_id;
        public String output_en;
    }

    public class ProductsCheapestPrice
    {
        public String item_uuid;
        public String item_name;
        public String currency;
        public String sell_price;
        public String net_price;
        public String gimmick_price;
    }

    public class Product
    {
        public int product_id;
        public int category_id;
        public String product_uuid;
        public String product_name;
        public String identity_uri;
        public String status;
        public String expiry_date;
        public int created_by;
        public Photos photos;
        public Practice practice;
        public Description description;
        public Howto howto;
        public ProductsCheapestPrice products_cheapest_price;
    }

    public class Item
    {
        public int category_id;
        public String label;
        public String category_english;
        public String category_bahasa;
        public String identity_uri;
        public String category_url;
        public String keyword;
        public List<Product> products;
    }

    public class Results
    {
        public List<Banner> banners;
        public List<Item> items;
    }

    public class Banner
    {
        public String title;
        public String description;
        public String link;
        public String image_url;
    }


    public class Meta
    {
        public double benchmark;
        public int current_page;
        public int total_page;
        public int limit;
        public int total_row;
    }

    public class Server
    {
        public String REQUEST_METHOD;
        public String CONTENT_TYPE;
        public String REMOTE_ADDR;
        public String HTTP_USER_AGENT;
    }

    public class Links
    {
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
