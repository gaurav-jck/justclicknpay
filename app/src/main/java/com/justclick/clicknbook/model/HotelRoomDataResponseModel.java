package com.justclick.clicknbook.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by gaurav.singhal on 8/31/2017.
 */


public class HotelRoomDataResponseModel implements Serializable{
    public String NumberOfRooms, RoomDataFile, RoomResponse, RoomResponseStatus;
    public ArrayList<Rooms> Rooms;

    public class Rooms implements Serializable{
        public String BoardBasis, CancellationPolicy, CurrencyCode, DailyRoomRate, DataFileName,
                HotelCode, LastCancellationDate, RommAmenity, RoomCode, RoomIndex, RoomName,
                RoomRate, RoomRateCode, RoomSequence;
    }

}
