package com.justclick.clicknbook.Fragment.flights.responseModel;

import java.util.ArrayList;

public class CoPassengerListDataModel {
    public int status;
    public ArrayList<response> response;
    public class response{
        public int id;
        public String firstName, lastName, dateOfBirth, passportNumber, title,
                passengerType, gender, relation;
        public boolean coTravller;
    }
}
