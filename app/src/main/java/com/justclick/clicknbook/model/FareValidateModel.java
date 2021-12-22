package com.justclick.clicknbook.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Lenovo on 04/13/2017.
 */

public class FareValidateModel implements Serializable
{
    public String ReferenceNumber,SplRound,Status;

    public ArrayList<Fare> Fare;

    public class Fare implements Serializable{
        public ArrayList<PaxFares> PaxFares;
    }

    public class PaxFares implements Serializable{
        public String Basic,OTax,PaxType,Tax,Yq;
    }

}
