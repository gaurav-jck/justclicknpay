package com.justclick.clicknbook.utils.enums;

public enum TrainQuotaEnum {
    GN("General"),
    LD("Ladies"),
    TQ("Tatkal"),
    SS("Sr. Citizen"),
    HP("Physically Handicapped");

    private String quotaName;

    TrainQuotaEnum(String name) {
        quotaName=name;
    }

    public String value(){
        return quotaName;
    }

    @Override public String toString(){
        return quotaName;
    }

    /*'GN~General Quota~LD~Ladies Quota~HO~Head quarters/high official Quota
    ~DF~Defence Quota~PH~Parliament house Quota~FT~Foreign Tourist Quota~
    DP~Duty Pass Quota~TQ~Tatkal Quota~PT~Premium Tatkal Quota
    ~SS~Female(above 45 Year)/Senior Citizen/Travelling alone~
    HP~Physically Handicapped Quota~RE~Railway Employee Staff on Duty for the train~
    GNRS~General Quota Road Side~OS~Out Station~PQ~Pooled Quota~
    RC(RAC)~Reservation Against Cancellation~RS~Road Side~YU~Yuva~LB~Lower Berth*/
}
