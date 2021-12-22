package com.justclick.clicknbook.Fragment.flights.responseModel;

public class AddToCartResponse extends CommonFlightResponse{
        public String sessionId;
        public response response;
        public class response{
            public String flightId;
        }
//       {"status":200,"error":{"errorCode":0,"errorMessage":""},"response":{"flightId":"gMK+ai3R2BKA+aDXCAAAAA=="},"sessionId":"62f0c9cf-47de-4ac0-a2ef-25f9d25289af"}
    }