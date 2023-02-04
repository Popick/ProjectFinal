package com.example.projectfinal_alpha;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Helper {

    public static String getCurrentDateString() {
        // Get the current date and time
        Date date = new Date();

        // Use a SimpleDateFormat to convert the Date object to a string in the desired format
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        return dateFormat.format(date);
    }

    public static String stringToDateFull(String dateString) throws ParseException {
        // Parse the input string into a Date object
        DateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmm");
        Date date = inputFormat.parse(dateString);

        // Use a DateFormat to convert the Date object to a full text representation
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
        return outputFormat.format(date);
    }

    public static String stringToDateTime(String dateString) throws ParseException {
        // Parse the input string into a Date object
        DateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmm");
        Date date = inputFormat.parse(dateString);

        // Use a DateFormat to convert the Date object to a full text representation
        DateFormat outputFormat = new SimpleDateFormat("HH:mm");
        return outputFormat.format(date);
    }

    public static int getClassNumber(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            Date input = dateFormat.parse(dateString);

//        Calendar calendar = Calendar.getInstance();
            int hour = input.getHours();
            int minute = input.getMinutes();

            if (hour == 8 && minute >= 10 && minute < 55) {
                return 1;
            } else if (hour == 8 && minute >= 55 || hour == 9 && minute < 45) {
                return 2;
            } else if (hour == 10 && minute >= 0 && minute < 50) {
                return 3;
            } else if (hour == 10 && minute >= 50 || hour == 11 && minute < 35) {
                return 4;
            } else if (hour == 12 && minute >= 0 && minute < 45) {
                return 5;
            } else if (hour == 12 && minute >= 45 || hour == 13 && minute < 30) {
                return 6;
            } else {
                return -1;
            }
        }
        catch (ParseException e) {
            // Return false if the input date string is invalid
            return -1;
        }
    }
    public static String getGrade(String grade) {

        if (grade.equals("7")) {
            return "ז'";
        } if (grade.equals("8")) {
            return "ח'";
        }if (grade.equals("9")) {
            return "ט'";
        }if (grade.equals("10")) {
            return "י'";
        }if (grade.equals("11")) {
            return "י\"א";
        }if (grade.equals("12")){
            return "י\"ב";
        }else {
            return "error";
        }
    }

    public static String getDayOfWeekInHebrew(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return "ראשון";
            case 2:
                return "שני";
            case 3:
                return "שלישי";
            case 4:
                return "רביעי";
            case 5:
                return "חמישי";
            case 6:
                return "שישי";
            case 7:
                return "שבת";
            default:
                throw new IllegalArgumentException("Invalid day of week: " + dayOfWeek);
        }
    }

    public static boolean isMoreThan30Minutes(String inputDate) {
        try {
            // Parse the input date string into a Date object
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            Date input = dateFormat.parse(inputDate);

            // Get the current time
            Date now = new Date();

            // Calculate the difference between the current time and the input date in milliseconds
            long diffInMilliseconds = now.getTime() - input.getTime();

            // Convert the difference to minutes
            long diffInMinutes = diffInMilliseconds / (60 * 1000);
            Log.d("diff",diffInMinutes+" minutes");
            // Return true if the difference is more than 30 minutes
            return diffInMinutes >= 5;



        } catch (ParseException e) {
            // Return false if the input date string is invalid
            return false;
        }
    }


}
