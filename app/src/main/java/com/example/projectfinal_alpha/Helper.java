package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refGroups;
import static com.example.projectfinal_alpha.FBref.refStudents;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class Helper {

    public static String getCurrentDateString() {
        // Get the current date and time
        Date date = new Date();

        // Use a SimpleDateFormat to convert the Date object to a string in the desired format
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        return dateFormat.format(date);
    }

    public static String getNextMonthDateString() {
        // Get the current date and time
        Date date = new Date();

        // Use a SimpleDateFormat to convert the Date object to a string in the desired format
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, 1);
        return dateFormat.format(c.getTime());
    }
    public static String getNextWeekDateString() {
        // Get the current date and time
        Date date = new Date();

        // Use a SimpleDateFormat to convert the Date object to a string in the desired format
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, 7);
        c.set(Calendar.HOUR_OF_DAY, 0);

        return dateFormat.format(c.getTime());
    }

    public static String stringToDateFull(String dateString) {
        // Parse the input string into a Date object
        DateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmm");
        Date date = null;
        try {
            date = inputFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Use a DateFormat to convert the Date object to a full text representation
        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return outputFormat.format(date);
    }

    public static String stringToDateTime(String dateString) {
        // Parse the input string into a Date object
        DateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmm");
        Date date = null;
        try {
            date = inputFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

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
        } catch (ParseException e) {
            // Return false if the input date string is invalid
            return -1;
        }
    }

    public static String getGrade(String grade) {

        if (grade.equals("7")) {
            return "ז'";
        }
        if (grade.equals("8")) {
            return "ח'";
        }
        if (grade.equals("9")) {
            return "ט'";
        }
        if (grade.equals("10")) {
            return "י'";
        }
        if (grade.equals("11")) {
            return "י\"א";
        }
        if (grade.equals("12")) {
            return "י\"ב";
        } else {
            return "error";
        }
    }

    public static int getDayOfWeekNow(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
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
            Log.d("diff", diffInMinutes + " minutes");
            // Return true if the difference is more than 30 minutes
            return diffInMinutes >= 5;


        } catch (ParseException e) {
            // Return false if the input date string is invalid
            return false;
        }
    }

    public static void addToGroup(String stuID, String groupID){
        refGroups.child(groupID).child("studentsIDs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> studentsIDs;
                if (dataSnapshot.exists()) {
                    studentsIDs = (ArrayList<String>) dataSnapshot.getValue();
                } else {
                    studentsIDs = new ArrayList<String>();
                }
                studentsIDs.add(stuID);
                refGroups.child(groupID).child("studentsIDs").setValue(studentsIDs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that may occur during the operation
            }
        });

        refStudents.child(stuID).child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> groups;
                if (dataSnapshot.exists()) {
                    groups = (ArrayList<String>) dataSnapshot.getValue();
                } else {
                    groups = new ArrayList<String>();
                }
                groups.add(groupID);
                refStudents.child(stuID).child("groups").setValue(groups);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that may occur during the operation
            }
        });

    }


}
