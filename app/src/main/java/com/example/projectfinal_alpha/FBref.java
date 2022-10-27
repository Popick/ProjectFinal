package com.example.projectfinal_alpha;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * @author Etay Sabag <itay45520@gmail.com>
 * @version 1
 * @since 21/10/2022
 * Helper to the data base class.
 */

public class FBref {
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance("https://project-final-ishorim-default-rtdb.europe-west1.firebasedatabase.app/");
    public static DatabaseReference refUsers=FBDB.getReference("Users");
    public static DatabaseReference refCalendar=FBDB.getReference("Calendar");
    public static DatabaseReference refTeachers=FBDB.getReference("Users/Teachers");
    public static DatabaseReference refStudents=FBDB.getReference("Users/Students");
    public static DatabaseReference refGuards=FBDB.getReference("Users/Guards");

}
