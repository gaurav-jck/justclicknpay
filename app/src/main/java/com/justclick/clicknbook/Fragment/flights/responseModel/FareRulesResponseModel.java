package com.justclick.clicknbook.Fragment.flights.responseModel;

import java.io.Serializable;
import java.util.ArrayList;

public class FareRulesResponseModel extends CommonFlightResponse implements Serializable {
    public String sessionId;
    public ArrayList<response> response;

    public class response implements Serializable {
        public String paxType;
        public ArrayList<rules> cancelRules, changeRules;

        public class rules implements Serializable {
            public String feeAmount, feeCurrency, airlineCode, text, departureType;
        }

//        segment source==1
        public String origin, destination;
        public passengerType passengerType;

        public class passengerType implements Serializable {
            public String paxType;
            public ArrayList<fareRules> fareRules;

            public class fareRules implements Serializable {
                public String fareRule;
            }
        }

    }
}


