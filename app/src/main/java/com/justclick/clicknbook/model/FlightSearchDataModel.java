package com.justclick.clicknbook.model;

import java.io.Serializable;
import java.util.ArrayList;

public class FlightSearchDataModel implements Serializable{
//    Common
  /*  public String Session, IsRefundable, currancycode, Policy;

//    OutBound
    public String FlightDuration, TimeOfDepartuere, TimeOfArrival, Source, Destination,
            Via, MarketingCarrier, Image, FareAmt;

//    InBound
    public String FlightDuration_in, TimeOfDepartuere_in, TimeOfArrival_in, Source_in,
        Destination_in, Via_in, MarketingCarrier_in, Image_in, FareAmt_in;
*/
//    new json model

    public  ArrayList<OneWay> OneWay;
    public  ArrayList<OneWay> TwoWay;
    public  ArrayList<OneWay> RoundTrip;

    public class OneWay implements Serializable{
        public boolean isClick=false;
        public String  IsIntl, SCode, DCode,SAirportName,DAirportName, SCity, DCity, FlightName, DDate, ADate, DTime,
                ATime, Duration,Refundable, Seats, Stop, Supplier, FareKey;

        public int FlightId,Sequence, AdtPrice, AdtTax, AdtOT, AdtYq, ChdPrice, ChdTax, ChdOT, ChdYq, InfPrice,
                InfTax, InfOT, InfYq, FinalPrice;

        //        international
        public String FlightId_r, IsIntl_r, SCode_r, DCode_r, SCity_r, DCity_r, FlightName_r, DDate_r, ADate_r,
                DTime_r, ATime_r, Duration_r, Refundable_r, Seats_r, Stop_r, Supplier_r, FareKey_r;
        public int Sequence_r, AdtPrice_r, AdtTax_r, AdtOT_r, AdtYq_r, ChdPrice_r, ChdTax_r,
                ChdOT_r, ChdYq_r, InfPrice_r, InfTax_r, InfOT_r, InfYq_r, FinalPrice_r;

        public ArrayList<Flights> Flights;
        public ArrayList<Flights> Flights_r;

        public class Flights implements Serializable{
            public String SCode, DCode, SCity, DCity, STerminal, DTerminal,SAirportName,DAirportName, DDate, ADate, DTime, ATime,
                    FNumber, FName, HBag, CBag, Duration, Class, AirLineClass, RefNumber, Image, Carrier,
                    FareBasis, CFareKey, Group;
        }

    }


}
