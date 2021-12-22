package com.justclick.clicknbook.Fragment.flights.responseModel;

import java.util.ArrayList;

public class FareQuotesResponseModel extends CommonFlightResponse{
    public String sessionId;
    public response response;

    public class response {
        public String id, flightSource;
        public int journeyType, segmentSource;
        public boolean isPriceChanged, isPriceUp;
        public fare fare;
        public netPayableAmount netPayableAmount;
        public ArrayList<fareBreakDowns> fareBreakDowns;

        public class netPayableAmount{
            public String currency;
            public float baseFare, publishedFare, totalTaxes, netPayable,
                    promoCodeDiscount, convenienceFee;
        }

        public class fareBreakDowns{
            public String id, passengerType, currency, baggageAllowance, cabinBaggage;
            public float totalFare, baseFare, totalTax, yqTax, reschedulePenalty, cancelPenalty;
            public boolean isRefundable;
        }

        public class fare {
            public String id, currency;
            public float baseFare, publishedFare, totalTaxes, yqTax;
        }
    }
}


