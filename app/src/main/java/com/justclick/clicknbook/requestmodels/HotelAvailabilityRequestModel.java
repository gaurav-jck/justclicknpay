package com.justclick.clicknbook.requestmodels;

import java.util.ArrayList;

/**
 * Created by gaurav.singhal on 8/31/2017.
 */

public class HotelAvailabilityRequestModel extends CommonRequestModel {
    public String CheckInDate, CheckOutDate, CountryName, DestnationCode, DestnationName, NumberOfAdult,
            NumberOfChild, NumberOfDays, NumberOfRooms, Supplier, StaRating;
    public ArrayList<RoomOccupancy> RoomOccupancy;

    public class RoomOccupancy{
        public String Ages;
        public int Adults, Children, TotalRoom;
    }
}
