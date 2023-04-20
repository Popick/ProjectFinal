package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refGuards;
import static com.example.projectfinal_alpha.FBref.refTeachers;
import static com.example.projectfinal_alpha.FBref.refUsers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * @author Etay Sabag <itay45520@gmail.com>
 * @version 2.0
 * @since 28/10/2022
 */
public class new_user1 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner grade_spinner, class_spinner, groupA_spinner, groupB_spinner;
    String[] grades = {"", "ז", "ח", "ט", "י", "יא", "יב"};
    int[] gradesnums = {0, 7, 8, 9, 10, 11, 12};
    String[] classes = {"", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    String[] groupA = {"", "פסיכולוגיה", "אומנות", "כימיה", "תקשורת", "מחשבים", "אלקטרוניקה", "הנדסת בניין", "לאי", "מדח", "מחשבת ישראל"};
    String[] groupB = {"", "פיסיקה", "ביוטכנולוגיה", "ביולוגיה"};
    int studentGrade = -1;
    int studentClass = -1;
//    ArrayList<Integer> groups = new ArrayList<Integer>();
    String studentGroupA;
    String studentGroupB;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Intent siNextSignUp;
    ArrayAdapter<String> adp;
    Student newUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user1);
        grade_spinner = (Spinner) findViewById(R.id.grade_spinner);
        class_spinner = (Spinner) findViewById(R.id.class_spinner);
        groupA_spinner = (Spinner) findViewById(R.id.groupA_spinner);
        groupB_spinner = (Spinner) findViewById(R.id.groupB_spinner);
        groupA_spinner.setEnabled(false);
        groupA_spinner.setClickable(false);
        groupB_spinner.setEnabled(false);
        groupB_spinner.setClickable(false);
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        adp = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, grades);
        grade_spinner.setAdapter(adp);

        adp = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, classes);
        class_spinner.setAdapter(adp);

        adp = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, groupA);
        groupA_spinner.setAdapter(adp);

        adp = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, groupB);
        groupB_spinner.setAdapter(adp);

        grade_spinner.setOnItemSelectedListener(this);
        class_spinner.setOnItemSelectedListener(this);
        groupA_spinner.setOnItemSelectedListener(this);
        groupB_spinner.setOnItemSelectedListener(this);

        siNextSignUp = new Intent(this, new_user2.class);

    }

    public boolean check_input() {
        if (studentGrade == -1 || studentClass == -1) {
            Toast.makeText(this, "please select class and grade", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (studentGrade > 9) {
                if (studentGroupA != null) {
                    newUser = new Student(currentUser.getDisplayName(), studentGrade + "", studentClass + "", "Student", null, null, null, null);
                    return true;
                } else {
                    Toast.makeText(this, "please select Groups", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            newUser = new Student(currentUser.getDisplayName(), studentGrade + "", studentClass + "", "Student", null, null, null, null);
            Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public void go_to_activity_guard(View view) {
        Guard newuser = new Guard(currentUser.getDisplayName(), "Guard");
        refGuards.child(currentUser.getUid()).setValue(newuser);
        Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void go_to_activity_teacher(View view) {
        if (studentGrade == -1 || studentClass == -1) {

            Toast.makeText(this, "please select class and grade", Toast.LENGTH_SHORT).show();

        } else {
            Teacher newuser = new Teacher(currentUser.getDisplayName(), "Teacher", "0");
            refTeachers.child(currentUser.getUid()).setValue(newuser);
            Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    public void go_to_new_user2(View view) {
        if (check_input()) {
            siNextSignUp.putExtra("userObject", newUser);
            startActivity(siNextSignUp);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0) {
            studentGrade = -1;
            studentClass = -1;
            studentGroupA = null;
            studentGroupB = null;
        } else {
            if (adapterView == grade_spinner) {
                studentGrade = gradesnums[i];
                if (studentGrade > 9) {
                    groupA_spinner.setEnabled(true);
                    groupA_spinner.setClickable(true);
                    groupB_spinner.setEnabled(true);
                    groupB_spinner.setClickable(true);
                } else {
                    groupA_spinner.setEnabled(false);
                    groupA_spinner.setClickable(false);
                    groupB_spinner.setEnabled(false);
                    groupB_spinner.setClickable(false);
                }
            } else if (adapterView == class_spinner) {
                studentClass = Integer.parseInt(classes[i]);
            } else if (adapterView == groupA_spinner) {
                studentGroupA = groupA[i];
            } else if (adapterView == groupB_spinner) {
                studentGroupB = groupB[i];
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public void sign_out(View view) {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(new_user1.this, "Signed out successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }


}

