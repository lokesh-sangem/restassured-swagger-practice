package com.tac.apitesting.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestDataGenerator {
    public static Map<String,String> generateValidLoginData(){
        Map<String,String>loginData = new HashMap<>();
        loginData.put("username","jsmith");
        loginData.put("password","demo1234");
        return loginData;
    }

    public static Map<String,String>generateInvalidLoginData(){
        Map<String,String>loginData = new HashMap<>();
        loginData.put("username","invaliduser");
        loginData.put("password","wrongpassword");
        return loginData;
    }

    public static String getCurrentDate(){
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    public static String getPastDate(int daysBack){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,-daysBack);
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }


    public static Map<String,String>generateDateRange(int startDaysBack,int endDaysBack){
       Map<String,String>dates = new HashMap<>();
       dates.put("startDate",getPastDate(startDaysBack));
       dates.put("endDate",getPastDate(endDaysBack));
       return dates;
    }
}
