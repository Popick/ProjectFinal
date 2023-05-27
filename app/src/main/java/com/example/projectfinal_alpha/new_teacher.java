package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refGroups;
import static com.example.projectfinal_alpha.FBref.refStudents;
import static com.example.projectfinal_alpha.FBref.refTeachers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class new_teacher extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    RadioGroup radioGroup;
    Spinner gradeSpinner;
    Spinner classSpinner;

    String[] grades = {"בחר:", "ז", "ח", "ט", "י", "יא", "יב"};
    int[] gradesnums = {0, 7, 8, 9, 10, 11, 12};
    String[] classes = {"בחר:", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};

    int teacherGrade = -1;
    int teacherClass = -1;

    int teacherType = -1;

    ArrayAdapter<String> adp;

    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    //TODO: fix invalid input
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_teacher);

        radioGroup = findViewById(R.id.radioGroup);
        gradeSpinner = findViewById(R.id.grade_spinner);
        classSpinner = findViewById(R.id.class_spinner);

        adp = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, grades);
        gradeSpinner.setAdapter(adp);

        adp = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, classes);
        classSpinner.setAdapter(adp);

        gradeSpinner.setOnItemSelectedListener(this);
        classSpinner.setOnItemSelectedListener(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.teacherRadioA) {
                    gradeSpinner.setEnabled(false);
                    classSpinner.setEnabled(false);
                    teacherType = 1;
                    //מקצועי
                } else if (checkedId == R.id.teacherRadioB) {
                    gradeSpinner.setEnabled(true);
                    classSpinner.setEnabled(true);
                    teacherType = 2;
                    //כיתתי
                } else if (checkedId == R.id.teacherRadioC) {
                    gradeSpinner.setEnabled(true);
                    classSpinner.setEnabled(true);
                    teacherType = 3;
                    //רכז
                }
            }
        });
    }

    public void onStart() {
        super.onStart();
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        currentUser = mAuth.getCurrentUser();
    }


    public void go_to_new_teacher(View view) {
        Log.d("Tinfo", "class: " + teacherClass + " grade: " + teacherGrade + " type: " + teacherType);
        if (teacherClass == -1 || teacherGrade == -1 || teacherType == -1) {
            Toast.makeText(this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
        } else {
            if (teacherType == 1) {
                Teacher newuser = new Teacher(currentUser.getDisplayName(), "Teacher");
                refTeachers.child(currentUser.getUid()).setValue(newuser);
                finish();
            } else if (teacherType == 2) {
                refGroups.orderByChild("groupName").equalTo(Helper.getGrade(teacherGrade + "") + teacherClass).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(new_teacher.this, "כבר קיים מחנך לכיתה זו", Toast.LENGTH_SHORT).show();
                        } else {
                            Teacher newuser = new Teacher(currentUser.getDisplayName(), "Teacher");
                            refTeachers.child(currentUser.getUid()).setValue(newuser);
                            Helper.writeGroup(Helper.getGrade(teacherGrade + "") + teacherClass, currentUser);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });

            } else if (teacherType == 3) {
                refGroups.orderByChild("groupName").equalTo(Helper.getGrade(teacherGrade + "") + teacherClass).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(new_teacher.this, "כבר קיים מחנך לכיתה זו", Toast.LENGTH_SHORT).show();
                        } else {
                            refGroups.orderByChild("groupName").equalTo(Helper.getGrade(teacherGrade + "")).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Toast.makeText(new_teacher.this, "כבר קיים רכז לשכבה זו", Toast.LENGTH_SHORT).show();
                                    } else {

                                        Teacher newuser = new Teacher(currentUser.getDisplayName(), "Teacher");
                                        refTeachers.child(currentUser.getUid()).setValue(newuser);

                                        Helper.writeGroup(Helper.getGrade(teacherGrade + "") + teacherClass, currentUser);

                                        AlertDialog.Builder builder = new AlertDialog.Builder(new_teacher.this);
                                        builder.setCancelable(false);

                                        ProgressBar progressBar = new ProgressBar(new_teacher.this);
                                        progressBar.setIndeterminate(true);

                                        builder.setView(progressBar);

                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();

                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Helper.writeGroup(Helper.getGrade(teacherGrade + ""), currentUser);
                                                finish();
                                            }
                                        }, 2000); // 2000 milliseconds = 2 seconds
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Handle error
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
        }


    }


    public void go_to_main_activity(View view) {
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0) {
            teacherGrade = -1;
            teacherClass = -1;

        } else {
            if (adapterView == gradeSpinner) {
                teacherGrade = gradesnums[i];
            } else if (adapterView == classSpinner) {
                teacherClass = Integer.parseInt(classes[i]);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}
