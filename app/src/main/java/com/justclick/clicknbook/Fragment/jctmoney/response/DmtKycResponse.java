package com.justclick.clicknbook.Fragment.jctmoney.response;

import java.io.Serializable;

public class DmtKycResponse implements Serializable {
    public String statusCode, statusMessage;
    public data data;

    public class data{
        public String fname, lname, stateresp, ekyc_id, mobile;
    }

}
