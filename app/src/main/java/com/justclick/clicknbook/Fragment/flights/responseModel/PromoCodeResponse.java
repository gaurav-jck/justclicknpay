package com.justclick.clicknbook.Fragment.flights.responseModel;

import java.io.Serializable;
import java.util.ArrayList;

public class PromoCodeResponse implements Serializable {
    public int status;
    public String error;
    public response response;

    public class response {
        public ArrayList<promoCodeDetails> promoCodeDetails;

        public class promoCodeDetails{
            public int promocodeId,percentAmt,flatAmt,percentMiles,flatMiles,serviceId,serviceCatId;
            public String promoCodeName,description;

    }}

}
