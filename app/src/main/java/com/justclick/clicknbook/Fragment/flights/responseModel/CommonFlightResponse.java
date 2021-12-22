package com.justclick.clicknbook.Fragment.flights.responseModel;

import java.io.Serializable;

public class CommonFlightResponse implements Serializable {
    public int status;
    public error error;

    public class error implements Serializable {
        public int errorCode;
        public String errorMessage;
    }
}
