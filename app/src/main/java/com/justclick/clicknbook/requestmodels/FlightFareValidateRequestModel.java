package com.justclick.clicknbook.requestmodels;

import java.io.Serializable;
import java.util.ArrayList;

public class FlightFareValidateRequestModel implements Serializable{

   public String AgentID,AdultCount,Cache,ChildCount,FromDate,FromSector,InfantCount,
           SplRound,SplRoundCarrierCode,ToDate,ToSector,Trip,TripType;

    public  ArrayList<AirLineList> AirLineList;


    public class AirLineList implements Serializable {

        public String AdultFare,FareKey,Supplier,TotalFare,Yq;

        public  ArrayList<Segments> Segments;



    }
    public class Segments implements Serializable {

        public String ADate,ATime,CFareKey,Carrier,Class,DCode,DDate,DTime,FName,FNumber,FareBasis,Group,SCode;
    }

}


