package com.justclick.clicknbook.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Lenovo on 04/13/2017.
 */

public class AgentDetails implements Serializable
{
    public String Status,StatusCode;
    public Detail Data;
    public ArrayList<GetTransactionTypes> GetTransactionTypes;

    public class Detail implements Serializable{
        public String AgencyName, Credit,
                ActualBalance,SalesPerson, Distributor, Active,
                CreditExpiery,Sid, LastTrancactionDate, BookBalance,
                DistMobNo, SalesMobNo;
    }

    public class GetTransactionTypes implements Serializable{
        public String TransactionType;
    }

}
