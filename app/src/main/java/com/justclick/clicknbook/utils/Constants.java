package com.justclick.clicknbook.utils;

public class Constants {
  public static class FlightJourneyType{
    public static final String ONE_WAY="ONE_WAY", RETURN="RETURN", MULTI_CITY="MULTI_CITY";
  }
  public static class Salutation{
    public static final int MR=1, MRS=2, MASTER=3, MS=4, MISS=4,  Other=3;
  }
  public static class Gender{
    public static final String MALE="MALE", FEMALE="FEMALE";
  }
  public static class ModuleType{
    public static final int FLIGHT=1, HOTEL=2, PACKAGE=3, RECHARGE=4;
  }
  public static class QueryStatus{
    public static final int ALL=-1, OPEN=1, CLOSE=2, REOPEN=3;
  }
}
