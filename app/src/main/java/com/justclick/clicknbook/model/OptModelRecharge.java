package com.justclick.clicknbook.model;

import java.util.ArrayList;


public class OptModelRecharge {
    public String Status, StatusCode;
    public ArrayList<operatorDetail> operatorDetail;
    public class operatorDetail {
        public String operatorCode, operatorName;
    }
}
