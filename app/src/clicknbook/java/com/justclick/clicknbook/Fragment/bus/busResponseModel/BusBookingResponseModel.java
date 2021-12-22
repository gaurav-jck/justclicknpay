package com.justclick.clicknbook.Fragment.bus.busResponseModel;

import java.io.Serializable;

/**
 * Created by pc1 on 5/22/2017.
 */

public class BusBookingResponseModel implements Serializable{
    public String Description, PNR, ReservationID;
    public int Status;

    public class Data implements Serializable{
//        public String Message;
    }

}
