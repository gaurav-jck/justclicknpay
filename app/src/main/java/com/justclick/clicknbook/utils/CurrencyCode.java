package com.justclick.clicknbook.utils;

import android.content.Context;

import com.justclick.clicknbook.R;

import java.util.Comparator;
import java.util.Currency;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

public class CurrencyCode {
  public static SortedMap<Currency, Locale> currencyLocaleMap;

  static {
    currencyLocaleMap = new TreeMap<Currency, Locale>(new Comparator<Currency>() {
      public int compare(Currency c1, Currency c2) {
        return c1.getCurrencyCode().compareTo(c2.getCurrencyCode());
      }
    });
    for (Locale locale : Locale.getAvailableLocales()) {
      try {
        Currency currency = Currency.getInstance(locale);
        currencyLocaleMap.put(currency, locale);
      } catch (Exception e) {
      }
    }
  }

  public static String getCurrencySymbol(String currencyCode, Context context) {
    try {
      Currency currency = Currency.getInstance(currencyCode);
      System.out.println(currencyCode + ":-" + currency.getSymbol(currencyLocaleMap.get(currency)));
      if(currencyCode.equalsIgnoreCase("INR")){
        return context.getResources().getString(R.string.rupeeSymbolRs);
      }else {
        return currency.getSymbol(currencyLocaleMap.get(currency));

      }
//                return currency.getSymbol(Locale.UK);
    }catch (Exception e){
      return context.getResources().getString(R.string.rupeeSymbolRs);
    }
//            return "";
  }

}