package com.justclick.clicknbook.Fragment.flights.responseModel;

import java.io.Serializable;

public class ApplyPromoCodeResponse implements Serializable {
    public int status;
    public String error,message;
    public response response;

    public class response implements Serializable{
            public float redeemAmount;
            public int redeemMiles,promocodeId;
            public String promoCode,message;
    }

}
