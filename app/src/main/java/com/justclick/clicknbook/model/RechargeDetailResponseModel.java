package com.justclick.clicknbook.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Lenovo on 04/13/2017.
 */

public class RechargeDetailResponseModel implements Serializable
{
    public String Status,StatusCode;
    public Detail Data;

    public class Detail implements Serializable{
        public String Commision, MarkUp;

    }
}
