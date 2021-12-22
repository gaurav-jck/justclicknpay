package com.justclick.clicknbook.Fragment.flights.responseModel;

import java.io.Serializable;
import java.util.ArrayList;

public class FlightSearchResponseModel extends CommonFlightResponse implements Serializable{
    public response response;
    public String sessionId;

    public class response implements Serializable {
        public ArrayList<flights> flights, flightsInbound;

        public metaData metaData;

        public class metaData implements Serializable {
            public boolean isSplitView;
            public String departureDate, arrivalDate;
            public OriginDestination origin, destination;

            public class OriginDestination implements Serializable {
                public String code, name, cityCode, cityName, terminal;
            }
        }

        public class flights implements Serializable{
            public String id, source;
            public int journeyType, segmentSource;
            public ArrayList<ArrayList<segments>> segments;
            public ArrayList<fareBreakDowns> fareBreakDowns;
            public ArrayList<fareRules> fareRules;
            public fare fare;
            public boolean isGSTMandatory, isGSTAllowed;
            public boolean isClick=false;

            public class segments implements Serializable{
               /* public ArrayList<segmentsData> segmentsData;
                public class segmentsData implements Serializable{*/
                    public String id, traceId, origin, destination, departureTime, arrivalTime,
                            source, equipment, remark;
                    public int segmentSource, duration, segmentIndicator, noOfSeatAvailable;
                    public boolean isETicketEligible, isRefundable;
                    public Airport originAirport;
                    public Airport destinationAirport;
                    public airline airline;
                    public flight flight;
                    public ArrayList<baggageInfo> baggageInfo;

                    public class baggageInfo implements Serializable{
                        public String paxType, text;
                    }

                    public class Airport implements Serializable{
                        public String code, cityName, cityCode, name, terminal;
                    }

                    public class airline implements Serializable{
                        public String code, name, remark, fareClass;
                    }

                    public class flight implements Serializable{
                        public String flightNumber;
                    }
//                }
            }

            public class fare implements Serializable{
                public String id, currency;
                public Float baseFare, publishedFare, totalTaxes, yqTax;
            }

            public class fareBreakDowns implements Serializable{
                public String id, passengerType, currency;
                public Float totalFare, baseFare, totalTax, yqTax;
            }

            public class fareRules implements Serializable{
                public String passengerType, currency, jckFees, changePenaltyType, cancelPenaltyType;
                public String changePenalty, cancelPenalty;
            }
        }
    }
}
