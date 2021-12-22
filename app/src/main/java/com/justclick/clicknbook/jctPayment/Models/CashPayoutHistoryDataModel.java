package com.justclick.clicknbook.jctPayment.Models;

import java.util.ArrayList;

public class CashPayoutHistoryDataModel {
    public String first_page_url, last_page_url, next_page_url, path, prev_page_url;
    public int current_page, from, last_page, per_page=10, to, total;
    public ArrayList<Data> data;

    public class Data{
        public int id, FK_userMasterId, totalPageCount;
        public Double amount, charges;
        public String deleted_at, created_at, updated_at, isActive, response, reference_id;
    }
}
/*"": 183,
      "": 1071,
      "": 494.1,
      "": null,
      "": "2018-09-25 11:48:32",
      "": "2018-09-25 11:48:40",
      "": "true",
      "": 5.9,
      "": "{\"status\":1,\"message\":\"Successfully Cash Out with ReservationID - CO24098BYZXJC0A13387\",\"reference_id\":\"CO24098BYZXJC0A13387\"}",
      "": "CO24098BYZXJC0A13387"*/
/*
 "first_page_url": "http://13.126.22.122:8000/api/cashpayoutrequest/cashpayout?page=1",
         "from": 1,
         "last_page": 1,
         "last_page_url": "http://13.126.22.122:8000/api/cashpayoutrequest/cashpayout?page=1",
         "next_page_url": null,
         "path": "http://13.126.22.122:8000/api/cashpayoutrequest/cashpayout",
         "per_page": 10,
         "prev_page_url": null,
         "to": 9,
         "total": 9*/
