package com.justclick.clicknbook.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by gaurav.singhal on 8/31/2017.
 */


public class HotelMoreInfoResponseModel implements Serializable{
    public String DataFileName, HotelAddress, HotelCode, HotelDescription,
            HotelContactNo, HotelEmail, HotelFaxNumber, HotelIndex, HotelLatitude,
            HotelLongitude, HotelName, HotelPinCode, HotelStarRating;

    public ArrayList<HotelFacilities> HotelFacilities;
    public ArrayList<HotelImages> HotelImages;

    public class HotelFacilities implements Serializable{

    }
    public class HotelImages implements Serializable{
        public String ImagePath;
    }
}