package com.konsula.app.service.model;

import java.util.List;

/**
 * Created by Konsula on 12/05/2016.
 */
public class MembershipVerifyingModel {



    public class BillingTransaction
    {
        public int billing_id ;
        public String billing_uuid ;
        public int member_id ;
        public String billing_type ;
        public String billing_status ;
        public String created_timestamp ;
    }

    public class Detail
    {
        public String start_date ;
        public String end_date ;
        public int plan_id ;
        public int subplan_id ;
    }

    public class Item
    {
        public String order_type ;
        public String order_name ;
        public String order_name_detail ;
        public String currency ;
        public int price ;
        public Detail detail ;
    }

    public class BankTransferInfo
    {
        public int bank_id ;
        public String bank_name ;
        public String branch_name ;
        public String account_holder_name ;
        public String account_number ;
        public Object note ;
    }

    public class Data
    {
        public Boolean status ;
        public String payment_uuid ;
        public int method_id ;
        public String category ;
        public String subcategory ;
        public String method_name ;
        public String method_identity ;
        public String currency ;
        public int amount ;
        public int convenience_fee ;
        public int promotion_fee ;
        public int total_payment ;
        public String promo_code ;
        public Object approval_id ;
        public String processing_status ;
        public String payment_status ;
        public Object settlement_code ;
        public int invoice_number ;
        public String created_timestamp ;
        public String expiry_timestamp ;
        public int expiry_countdown ;
        public String settlement_timestamp ;
        public BillingTransaction billing_transaction ;
        public List<Item> items ;
        public BankTransferInfo bank_transfer_info ;
        public int member_id ;
        public String member_uuid ;
        public String fullname ;
        public String email ;
        public String phone_number ;
        public String user_lang ;
    }

    public class Results
    {
        public Boolean status ;
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
