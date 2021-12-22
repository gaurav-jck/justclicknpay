package com.justclick.clicknbook.model;

import java.util.ArrayList;

/**
 * Created by pc1 on 4/12/2017.
 */

public class OptModel {
    public String Status, StatusCode;
    public ArrayList<OptData> Data;
    public class OptData {
        public String OptId, OptName;
    }
}
