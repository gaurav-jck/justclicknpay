package com.justclick.clicknbook.Fragment.flights.responseModel;

import java.io.Serializable;
import java.util.ArrayList;

public class FlightSearchRoundDomesticResponseModel extends CommonFlightResponse implements Serializable {
    public response response;
    public String sessionId;

    public class response implements Serializable {
        public ArrayList<ArrayList<FlightSearchResponseModel.response.flights>> flights;

        public class flights implements Serializable {
            public String id, source, baggage, mealType, priority, origin, destination;
            public int journeyType, segmentSource;
            public ArrayList<ArrayList<segments>> segments;
            public ArrayList<fareRules> fareRules;
            public ArrayList<fareBreakDowns> fareBreakDowns;
            public fare fare;
            public boolean isGSTMandatory, isGSTAllowed;

            public class segments implements Serializable {
               /* public ArrayList<segmentsData> segmentsData;
                public class segmentsData implements Serializable{*/
                    public String id, traceId, origin, destination, departureTime, arrivalTime,
                            source, equipment, remark;
                    public int segmentSource, duration, segmentIndicator, noOfSeatAvailable;
                    public boolean isETicketEligible;
                    public originAirport originAirport;
                    public destinationAirport destinationAirport;
                    public airline airline;
                    public flight flight;

                    public class originAirport implements Serializable {
                        public String code, airportName, terminal;
                    }

                    public class destinationAirport implements Serializable {
                        public String code, airportName, terminal;
                    }

                    public class airline implements Serializable {
                        public String code, name, remark, fareClass;
                    }

                    public class flight implements Serializable {
                        public String flightStatus, flightNumber;
                    }
//                }
            }

            public class fare implements Serializable {
                public String id, currency;
                public Float baseFare, publishedFare, totalTaxes, yqTax;
            }

            public class fareRules implements Serializable {

            }

            public class fareBreakDowns implements Serializable {
                public String id, passengerType, currency, baggageAllowance, cabinBaggage;
                public Number totalFare, baseFare, totalTax, yqTax, reschedulePenalty, cancelPenalty;
                public boolean isRefundable;
            }
        }
    }
}
