package com.justclick.clicknbook.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Words  {

      private static final String[] tensNames = {
        "",
        " ten",
        " twenty",
        " thirty",
        " forty",
        " fifty",
        " sixty",
        " seventy",
        " eighty",
        " ninety"
      };

      private static final String[] numNames = {
        "",
        " one",
        " two",
        " three",
        " four",
        " five",
        " six",
        " seven",
        " eight",
        " nine",
        " ten",
        " eleven",
        " twelve",
        " thirteen",
        " fourteen",
        " fifteen",
        " sixteen",
        " seventeen",
        " eighteen",
        " nineteen"
      };

      private Words() {}


    public static String convertLessThanOneThousand(int number) {
        String soFar;

        if (number % 100 < 20){
          soFar = numNames[number % 100];
          number /= 100;
        }
        else {
          soFar = numNames[number % 10];
          number /= 10;

          soFar = tensNames[number % 10] + soFar;
          number /= 10;
        }
        if (number == 0) return soFar;
        return numNames[number] + " hundred" + soFar;
      }


      public static String convert(long number) {
        // 0 to 999 999 999 999
        if (number == 0) { return "zero"; }

        String snumber = Long.toString(number);

        // pad with "0"
        String mask = "000000000000";
        DecimalFormat df = new DecimalFormat(mask);
        snumber = df.format(number);

        // XXXnnnnnnnnn
        int billions = Integer.parseInt(snumber.substring(0,3));
        // nnnXXXnnnnnn
        int millions  = Integer.parseInt(snumber.substring(3,6));
        // nnnnnnXXXnnn
        int hundredThousands = Integer.parseInt(snumber.substring(6,9));
        // nnnnnnnnnXXX
        int thousands = Integer.parseInt(snumber.substring(9,12));

        String tradBillions;
        switch (billions) {
        case 0:
          tradBillions = "";
          break;
        case 1 :
          tradBillions = convertLessThanOneThousand(billions)
          + " billion ";
          break;
        default :
          tradBillions = convertLessThanOneThousand(billions)
          + " billion ";
        }
        String result =  tradBillions;

        String tradMillions;
        switch (millions) {
        case 0:
          tradMillions = "";
          break;
        case 1 :
          tradMillions = convertLessThanOneThousand(millions)
             + " million ";
          break;
        default :
          tradMillions = convertLessThanOneThousand(millions)
             + " million ";
        }
        result =  result + tradMillions;

        String tradHundredThousands;
        switch (hundredThousands) {
        case 0:
          tradHundredThousands = "";
          break;
        case 1 :
          tradHundredThousands = "one thousand ";
          break;
        default :
          tradHundredThousands = convertLessThanOneThousand(hundredThousands)
             + " thousand ";
        }
        result =  result + tradHundredThousands;

        String tradThousand;
        tradThousand = convertLessThanOneThousand(thousands);
        result =  result + tradThousand;

        // remove extra spaces!
        return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");}

  public static String convertToIndianCurrency(String num) {

    BigDecimal bd = new BigDecimal(num);
    long number = bd.longValue();
    long no = bd.longValue();
    int decimal = (int) (bd.remainder(BigDecimal.ONE).doubleValue() * 100);
    int digits_length = String.valueOf(no).length();
    int i = 0;
    String finalResult = "";

    ArrayList<String> str = new ArrayList<>();
    HashMap<Integer, String> words = new HashMap<>();
    words.put(0, "");
    words.put(1, "One");
    words.put(2, "Two");
    words.put(3, "Three");
    words.put(4, "Four");
    words.put(5, "Five");
    words.put(6, "Six");
    words.put(7, "Seven");
    words.put(8, "Eight");
    words.put(9, "Nine");
    words.put(10, "Ten");
    words.put(11, "Eleven");
    words.put(12, "Twelve");
    words.put(13, "Thirteen");
    words.put(14, "Fourteen");
    words.put(15, "Fifteen");
    words.put(16, "Sixteen");
    words.put(17, "Seventeen");
    words.put(18, "Eighteen");
    words.put(19, "Nineteen");
    words.put(20, "Twenty");
    words.put(30, "Thirty");
    words.put(40, "Forty");
    words.put(50, "Fifty");
    words.put(60, "Sixty");
    words.put(70, "Seventy");
    words.put(80, "Eighty");
    words.put(90, "Ninety");

//    String digits[] = {"", "Hundred", "Thousand", "Lakh", "Crore","Arab","Kharab","Nil","Padma","Shankh"};
    String digits[] = {"", "Hundred", "Thousand", "Lakh", "Crore"};

    while (i < digits_length) {
      int divider = (i == 2) ? 10 : 100;
      number = no % divider;
      no = no / divider;
      i += divider == 10 ? 1 : 2;
      if (number > 0) {
        int counter = str.size();
        String plural = (counter > 0 && number > 9) ? "s" : "";
        String tmp = (number < 21) ? words.get(Integer.valueOf((int) number)) + " " +
                digits[counter] + plural :
                words.get(Integer.valueOf((int) Math.floor(number / 10) * 10))
                        + " " + words.get(Integer.valueOf((int) (number % 10)))
                        + " " + digits[counter] + plural;
        str.add(tmp);
      } else {
        str.add("");
      }
    }

    Collections.reverse(str);
    String Rupees = null;

    Rupees = TextUtils.join(" ", str).trim();


    String paise = (decimal) > 0 ? " And " + words.get(
            Integer.valueOf((int) (decimal - decimal % 10))) + " " +
            words.get(Integer.valueOf((int) (decimal % 10))) : "";
    // AND FORTNY NINE PAISA
    if (!paise.isEmpty()) {

      paise = paise.concat(" Paise");
    }

    finalResult = "Rupees " + Rupees + paise + " Only";
    return finalResult.replace("  " ," ")
            .replace("   "," ");
  }



      }