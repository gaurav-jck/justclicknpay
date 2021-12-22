package com.justclick.clicknbook.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.jar.Attributes;

/**
 * Created by Lenovo on 04/13/2017.
 */

public class RblGetSenderResponse implements Serializable
{
    public String description, Scharge;
    public int status;
    public RBLRemitterDetails RBLRemitterDetails;
    public BenDetails BenDetails;


    public class RBLRemitterDetails implements Serializable{
        public String ALimit, B_Flag, City, MobileNo, Rid, PinCode, SName, S_Flag, Sid, State;
    }

    public class BenDetails implements Serializable{
        public ArrayList<BenDetail> BenDetail;

        public class BenDetail implements Serializable{
            public String AccountNo, Bank, Ben_flag, BeneLimit, IFSC, MerchantID, Mobile,
                    Name,NameAsPerBank, Val_flag, Status ;
            public int IMPSFlag, NEFTFlag;
        }
    }
}

