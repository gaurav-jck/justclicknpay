package com.justclick.clicknbook.Fragment.bus.busResponseModel;

import java.io.Serializable;

/**
 * Created by gaurav.singhal on 9/12/2017.
 */

public class BusCancelResponse implements Serializable{
    public String Description,ReservationID;
    public int Status;
    public Data Data;

    public class Data implements Serializable
    {
        public String Response;
    }

}
