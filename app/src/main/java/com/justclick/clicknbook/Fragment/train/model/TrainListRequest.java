package com.justclick.clicknbook.Fragment.train.model;

import com.justclick.clicknbook.ApiConstants;

public class TrainListRequest {
    public String bookuserid, merchantid= ApiConstants.MerchantId, usertype, fromdate, todate;

//    filter
    public String pnr, reservationid, trainnumber, trainsource, traindestination, status, agentcode;


    /*{"bookuserid":"j}~uIs~|}lurlttj{x7lxv","fromdate":"20250711","merchantid":"JUSTCLICKTRAVELS","todate":"20250711","usertype":"OOU", "pnr":"2719048931"}


{"bookuserid":"mjn7{jq~uABIpvjru7lxv","fromdate":"20250714","merchantid":"JUSTCLICKTRAVELS","todate":"20250714","usertype":"A"}

     **/

    /*{
        public int pageIndex { get; set; } = 0;
        [Required(ErrorMessage = "From Date Required")]
        public string fromdate { get; set; }
        [Required(ErrorMessage = "Upto Date Required")]
        public string todate { get; set; }
        public string doj { get; set; }
        public string reservationid { get; set; }
        public string trainnumber { get; set; }
        public string trainname { get; set; }
        public string trainsource { get; set; }
        public string traindestination { get; set; }
        public string @class { get; set; }
        public string paxmobile { get; set; }
        public string pnr { get; set; }
        public string paxname { get; set; }
        public string agentcode { get; set; }

        [Required(ErrorMessage = "Book User ID Required")]
        public string bookuserid { get; set; }
        public string status { get; set; }
        [Required(ErrorMessage = "User Type Required")]
        public string usertype { get; set; }
        public string userid { get; set; }
        [Required(ErrorMessage = "Merchantid Required")]
        public string merchantid { get; set; }
        public string creationid { get; set; }
        public string bookingtype { get; set; }
        public string quota { get; set; }
        public string state { get; set; }
        public string branch { get; set; }
        public string bookingmode { get; set; }
    }*/
}
