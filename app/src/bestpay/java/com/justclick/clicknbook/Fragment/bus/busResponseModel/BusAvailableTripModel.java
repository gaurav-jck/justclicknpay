package com.justclick.clicknbook.Fragment.bus.busResponseModel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by gaurav.singhal on 10/14/2017.
 */

public class BusAvailableTripModel implements Serializable {
//    public ArrayList<availableTrips> availableTrips;
//    public class availableTrips{
        public String AC, arrivalTime, availCatCard, availableSeats,
                bookable, bpDpSeatLayout, busServiceId, busType, busTypeId,
                cancellationPolicy, departureTime, destination, doj, dropPointMandatory,
        id, idProofRequired, liveTrackingAvailable, nonAC, operator,
                partialCancellationAllowed, routeId, seater, selfInventory, sleeper,
                source, tatkalTime, travels, vehicleType, zeroCancellationTime, mTicketEnabled;

      public ArrayList<boardingTimes> boardingTimes;
          public ArrayList<fareDetails> fareDetails;
        public ArrayList<droppingTimes> droppingTimes;
    public String[] fares;

    public class boardingTimes{
            public String address, bpId, bpName, contactNumber, landmark, location,
                    prime, time;
        }

        public class droppingTimes{
            public String address, bpId, bpName, contactNumber, landmark, location,
                    prime, time;
        }

        public class fareDetails{
            public String baseFare, bookingFee, markupFareAbsolute, markupFarePercentage,
                    operatorServiceChargeAbsolute, operatorServiceChargePercentage,
                    serviceTaxAbsolute, serviceTaxPercentage, totalFare;
        }
//    }

}
