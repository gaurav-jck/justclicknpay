package com.justclick.clicknbook.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by gaurav.singhal on 8/31/2017.
 */

public class HotelAvailabilityResponseModel implements Serializable{
    public String CheckIn, CheckOut, Status, StatusCode;
    public ArrayList<Hotels> hotels;
    public class Hotels implements Serializable{
        public String Address, Currency, DataFileName, DestinationName, HotelCode,
                HotelImage, HotelName, HotelStarRating, Latitude, Longitude,
                MaxRate, MinRate, RoomIndex, TripRating, TripReviewURL;
    }

}
