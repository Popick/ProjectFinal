package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refGroups;
import static com.example.projectfinal_alpha.FBref.refStudents;
import static com.example.projectfinal_alpha.FBref.refTeachers;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
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
import java.util.Random;

/**
 * The Helper class provides utility methods for date and time operations, as well as other helper functions.
 */
public class Helper {
    /**
     * Returns the current date and time as a string in the format "yyyyMMddHHmm".
     *
     * @return The current date and time as a string.
     */
    public static String getCurrentDateString() {
        // Get the current date and time
        Date date = new Date();

        // Use a SimpleDateFormat to convert the Date object to a string in the desired format
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        return dateFormat.format(date);
    }

    /**
     * Returns the date and time for the next month from the current date and time, as a string in the format "yyyyMMddHHmm".
     *
     * @return The next month's date and time as a string.
     */
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

    /**
     * Returns the date and time for the next week from the current date and time, as a string in the format "yyyyMMddHHmm".
     *
     * @return The next week's date and time as a string.
     */
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

    /**
     * Converts a date string in the format "yyyyMMddHHmm" to a full text representation in the format "dd/MM/yyyy HH:mm".
     *
     * @param dateString The date string to convert.
     * @return The full text representation of the date and time.
     */
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

    /**
     * Converts a date string in the format "yyyyMMddHHmm" to a time representation in the format "HH:mm".
     *
     * @param dateString The date string to convert.
     * @return The time representation of the date.
     */
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

    /**
     * Returns the class number based on the given date string in the format "yyyyMMddHHmm".
     *
     * @param dateString The date string to determine the class number.
     * @return The class number (1-6) or -1 if the date string is invalid.
     */
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
            } else if ((hour == 10 && minute >= 0 && minute < 50) || (hour == 9 && minute > 45)) {
                return 3;
            } else if (hour == 10 && minute >= 50 || hour == 11 && minute < 35) {
                return 4;
            } else if ((hour == 12 && minute >= 0 && minute < 45) || hour == 11 && minute > 35) {
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


    /**
     * Converts a grade level (7-12) to its corresponding Hebrew representation.
     *
     * @param grade The grade level (7-12) to convert.
     * @return The Hebrew representation of the grade, or "error" if the grade is invalid.
     */
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

    /**
     * Returns the current day of the week as an integer (1-7).
     *
     * @return The current day of the week.
     */
    public static int getDayOfWeekNow() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Converts the given day of the week (1-7) to its corresponding Hebrew representation.
     *
     * @param dayOfWeek The day of the week (1-7) to convert.
     * @return The Hebrew representation of the day of the week.
     * @throws IllegalArgumentException If the day of the week is invalid.
     */
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

    /**
     * Checks if the time difference between the current time and the given input date string is more than 30 minutes.
     *
     * @param inputDate The input date string in the format "yyyyMMddHHmm".
     * @return {@code true} if the time difference is more than 30 minutes, {@code false} otherwise.
     */
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

    /**
     * Adds the student with the given ID to the group with the given ID.
     *
     * @param stuID            The ID of the student to add.
     * @param groupID          The ID of the group to add the student to.
     * @param deleteFromWaiting {@code true} to delete the student from the waiting list if present, {@code false} otherwise.
     */
    public static void addToGroup(String stuID, String groupID, boolean deleteFromWaiting) {
        refGroups.child(groupID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Group tempGroup = dataSnapshot.getValue(Group.class);
                    ArrayList<String> studentsIDs;
                    if (tempGroup.getStudentsIDs() != null) {
                        studentsIDs = tempGroup.getStudentsIDs();
                    } else {
                        studentsIDs = new ArrayList<String>();
                    }
                    studentsIDs.add(stuID);
                    tempGroup.setStudentsIDs(studentsIDs);

                    if(deleteFromWaiting) {
                        ArrayList<String> waitingIDs;
                        if (tempGroup.getWaitingStudentsIDs() != null) {
                            waitingIDs = tempGroup.getWaitingStudentsIDs();
                        } else {
                            waitingIDs = new ArrayList<String>();
                        }
                        waitingIDs.remove(stuID);
                        tempGroup.setWaitingStudentsIDs(waitingIDs);
                    }



                    refGroups.child(groupID).setValue(tempGroup);

                } else {
                    Log.d("addToGroup", "group doesn't exist");
                }
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

    /**
     * Removes the student with the given ID from the group with the given ID.
     *
     * @param stuID   The ID of the student to remove.
     * @param groupID The ID of the group to remove the student from.
     */
    public static void removeFromGroup(String stuID, String groupID) {
        refGroups.child(groupID).child("studentsIDs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<String> studentsIDs = (ArrayList<String>) dataSnapshot.getValue();
                    studentsIDs.remove(stuID);
                    refGroups.child(groupID).child("studentsIDs").setValue(studentsIDs);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that may occur during the operation
            }
        });

        refStudents.child(stuID).child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<String> groups = (ArrayList<String>) dataSnapshot.getValue();
                    groups.remove(groupID);
                    refStudents.child(stuID).child("groups").setValue(groups);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that may occur during the operation
            }
        });


    }

    /**
     * Writes a new group to the Firebase database.
     *
     * @param groupName   The name of the group.
     * @param currentUser The FirebaseUser object representing the current user.
     */
    public static void writeGroup(String groupName, FirebaseUser currentUser) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(5);
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 5; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        String joinCode = sb.toString();

        refGroups.orderByChild("joinCode").equalTo(joinCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    writeGroup(groupName, currentUser);
                } else {
                    Group newGroup = new Group(currentUser.getDisplayName(), currentUser.getUid(), groupName, joinCode, false);
                    String groupID = refGroups.push().getKey();
                    refGroups.child(groupID).setValue(newGroup);

                    refTeachers.child(currentUser.getUid()).child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<String> groups;
                            if (dataSnapshot.exists()) {
                                groups = (ArrayList<String>) dataSnapshot.getValue();
                            } else {
                                groups = new ArrayList<String>();
                            }
                            groups.add(groupID);
                            refTeachers.child(currentUser.getUid()).child("groups").setValue(groups);


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle any errors that may occur during the operation
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

    }

    /**
     * Deletes a group from the Firebase database.
     *
     * @param groupID   The ID of the group to delete.
     * @param teacherID The ID of the teacher who owns the group.
     */
    public static void deleteGroup(String groupID, String teacherID) {
        refGroups.child(groupID).removeValue();

        refTeachers.child(teacherID).child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<String> groups = (ArrayList<String>) dataSnapshot.getValue();
                    groups.remove(groupID);
                    refTeachers.child(teacherID).child("groups").setValue(groups);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that may occur during the operation
            }
        });


    }

}
