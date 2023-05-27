package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refApprovals;
import static com.example.projectfinal_alpha.FBref.refGroups;
import static com.example.projectfinal_alpha.FBref.refStudents;
import static com.example.projectfinal_alpha.FBref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.squareup.picasso.Picasso;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 * @author Etay Sabag <itay45520@gmail.com>
 * @version 1.0
 * @since 23/10/2022
 */
public class guard_screen extends AppCompatActivity {
    private CodeScanner codeScanner;
    TextView tv_result, tv_datetime;
    CodeScannerView scannerView;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ImageView iVQrCode;
    Student currentStudent;
    boolean isAllowedTemp = false;
    boolean isAllowedPer = false;
    boolean isAllowedGroup = false;
    private boolean atLeastOneGroup;
    private boolean atLeastOneTemp;
    private boolean atLeastOnePer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard_screen);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }
        Toast.makeText(guard_screen.this, "לחץ על המסך בשביל לסרוק", Toast.LENGTH_LONG).show();
        scannerView = findViewById(R.id.scanner_view);
        tv_result = findViewById(R.id.tv_result);
        tv_datetime = findViewById(R.id.tv_view_time);
        iVQrCode = (ImageView) findViewById(R.id.iVQrCode);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        codeScanner = new CodeScanner(this, scannerView);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Format dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateTimeString = dateFormat.format(new Date());
                        Toast.makeText(guard_screen.this, result.getText(), Toast.LENGTH_SHORT).show();
                        tv_datetime.setText(dateTimeString);
                        loadUser(result.getText());
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });
    }

    public void loadUser(String Uid) {
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/project-final-ishorim.appspot.com/o/uploads%2F" + Uid + ".jpg?alt=media").into(iVQrCode);
        Log.d("camann", "fuck yes");

        refStudents.child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dS) {


//                String uID = (String) dS.getKey();
                currentStudent = dS.getValue(Student.class);
                atLeastOnePer = false;
                atLeastOneTemp = false;

                if (currentStudent.getApprovalID() != null) {

//                    isAllowed = currentStudent.checkApproval();
                    for (String approvalID : currentStudent.getApprovalID()) {
                        if (!atLeastOneTemp) {
                            refApprovals.child(approvalID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Approval appTemp = dataSnapshot.getValue(Approval.class);
                                    if (appTemp != null) {
//todo: now that i keep the groups in the user need to update the reading what groups the user in
                                        Log.d("boolean", !Helper.isMoreThan30Minutes(appTemp.getTimeStampApproval()) + " --> isMoreThan30Minutes");
                                        Log.d("boolean", (appTemp.getHour().contains(Helper.getClassNumber(Helper.getCurrentDateString()))) + " --> getClassNumber");
                                        // האישור תקף לחצי שעה
                                        // או האישור תקף למשך השיעור, כלומר אם השיעור הנוכחי שווה לשיעור באישור
                                        // אם השיעור שווה ל-1 משמע האישור ניתן לאחר שעות הלימודים ולכן תקף תמיד כל עוד לא עברה חצי שעה
                                        // לצורכי דיבוג בלבד, בפרודקשן אחרי שעות הלימודים לתלמיד תמיד יהיה מותר לצאת

                                        Log.d("caman2", "so??? " + Helper.getDayOfWeekNow() + "but is it equal? " + (appTemp.getDay() == Helper.getDayOfWeekNow()));


                                        if (appTemp.getExpirationDate().compareTo(Helper.getCurrentDateString()) < 0) {
                                            currentStudent.getApprovalID().remove(approvalID);
                                            refStudents.child(currentUser.getUid()).child("approvalID").setValue(currentStudent.getApprovalID());
                                            refApprovals.child(approvalID).child("isValid").setValue(false);

                                            isAllowedTemp = false;

                                        } else {
                                            isAllowedTemp = (appTemp.getHour().contains(Helper.getClassNumber(Helper.getCurrentDateString()))
                                                    && Helper.getClassNumber(Helper.getCurrentDateString()) != -1) && appTemp.getDay() == Helper.getDayOfWeekNow();

                                            if (isAllowedTemp) {
                                                atLeastOneTemp = true;
                                                updateApproval();
                                            }
                                        }
                                        Log.d("caman", isAllowedTemp + " no");

                                    } else {
                                        Log.d("caman", isAllowedTemp + " jkm");
                                        isAllowedTemp = false;

                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    Log.w("failed", "Failed to read value.", error.toException());
                                    isAllowedTemp = false;

                                }

                            });

                        }
                        Log.d("boolean", "is allowed on data change " + isAllowedTemp);
                    }

                }
                if (currentStudent.getPermanentApprovalID() != null) {
                    Log.d("oh boy", isAllowedTemp + " " + currentStudent.getPermanentApprovalID().size());

                    for (String perApprovalID : currentStudent.getPermanentApprovalID()) {
                        if (!atLeastOnePer) {
                            refApprovals.child(perApprovalID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Approval appTemp = dataSnapshot.getValue(Approval.class);
                                    Log.d("oh boy", "idk");

                                    if (appTemp != null) {
                                        Log.d("isitvalid?", appTemp.getExpirationDate());

                                        Log.d("boolean", !Helper.isMoreThan30Minutes(appTemp.getTimeStampApproval()) + " --> isMoreThan30Minutes");
                                        Log.d("boolean", (appTemp.getHour().contains(Helper.getClassNumber(Helper.getCurrentDateString()))) + " --> getClassNumber");

                                        if (appTemp.getExpirationDate().compareTo(Helper.getCurrentDateString()) < 0) {
                                            Log.d("isitvalid?", "nope it isn't");
                                            Log.d("caman2", "so??? " + Helper.getDayOfWeekNow() + "but is it equal? " + (appTemp.getDay() == Helper.getDayOfWeekNow()));
                                            currentStudent.getPermanentApprovalID().remove(perApprovalID);
                                            refStudents.child(currentUser.getUid()).child("permanentApprovalID").setValue(currentStudent.getPermanentApprovalID());
                                            refApprovals.child(perApprovalID).child("isValid").setValue(false);

                                            isAllowedPer = false;
                                        } else {
                                            isAllowedPer = (appTemp.getHour().contains(Helper.getClassNumber(Helper.getCurrentDateString())) &&
                                                    Helper.getClassNumber(Helper.getCurrentDateString()) != -1) && appTemp.getDay() == Helper.getDayOfWeekNow();
                                            if (isAllowedPer) {
                                                atLeastOnePer = true;
                                                updateApproval();
                                            }
                                        }
                                    } else {
                                        isAllowedPer = false;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }
                }


                updateApproval();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }


//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value.
//                Student stuTemp = dataSnapshot.getValue(Student.class);
//
//                approvalID = stuTemp.getApprovalID();
//                if(approvalID==null) {
//                    updateApproval(false);
//                }else{
//                    refApprovals.child("approvalID").addListenerForSingleValueEvent(new ValueEventListener() {
//                        boolean isAllowed = true;
//
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Approval appTemp = dataSnapshot.getValue(Approval.class);
//                            if (appTemp != null) {
//
//                                Log.d("boolboolean",!Helper.isMoreThan30Minutes(appTemp.getTimeStampApproval())+" --> isMoreThan30Minutes");
//                                Log.d("boolboolean",(Helper.getClassNumber(appTemp.getTimeStampApproval()) == Helper.getClassNumber(Helper.getCurrentDateString()))+" --> getClassNumber");
//
//                                isAllowed = (!Helper.isMoreThan30Minutes(appTemp.getTimeStampApproval()) || ((Helper.getClassNumber(appTemp.getTimeStampApproval())
//                                        == Helper.getClassNumber(Helper.getCurrentDateString()) && Helper.getClassNumber(Helper.getCurrentDateString()) != -1)));
//                                Log.d("camann", isAllowed +" fuck no");
//
//                            } else {
//
//                                isAllowed = false;
//                            }
//
//                            updateApproval(isAllowed);
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError error) {
//                            // Failed to read value
//                            Log.w("failed", "Failed to read value.", error.toException());
//                            isAllowed = false;
//
//                        }
//
//                    });
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w("failed", "Failed to read value.", error.toException());
//            }

        });

        refGroups.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dS) {
                if (currentStudent != null && (currentStudent.getApprovalID() == null || (!isAllowedTemp && !isAllowedPer))) {
                    atLeastOneGroup = false;
                    Log.d("caman", "step 0 ");
                    int i = 0;
                    for (DataSnapshot data : dS.getChildren()) {
                        Group grpTemp = data.getValue(Group.class);
                        String grpkey = data.getKey();
                        if (grpTemp != null || !atLeastOneGroup) {
                            Log.d("caman", "step 1 " + grpTemp.toString());
                            Log.d("caman", "step 1.5 " + grpkey);

                            if (grpTemp.getStudentsIDs() != null) {
                                Log.d("caman", "step 2 " + grpTemp.getStudentsIDs().toString());

                                if (grpTemp.getStudentsIDs().contains(currentUser.getUid())) {

                                    //possible bug: maybe approval ID is never being set, check it if doesnt work
                                    Log.d("caman", "is it null? " + grpTemp.getApprovalID());
                                    if (grpTemp.getApprovalID() != null) {
                                        refApprovals.child(grpTemp.getApprovalID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                Approval appTemp = dataSnapshot.getValue(Approval.class);
                                                if (appTemp != null) {
                                                    // האישור תקף לחצי שעה
                                                    // או האישור תקף למשך השיעור, כלומר אם השיעור הנוכחי שווה לשיעור באישור
                                                    // אם השיעור שווה ל-1 משמע האישור ניתן לאחר שעות הלימודים ולכן תקף תמיד כל עוד לא עברה חצי שעה
                                                    // לצורכי דיבוג בלבד, בפרודקשן אחרי שעות הלימודים לתלמיד תמיד יהיה מותר לצאת
                                                    isAllowedGroup = ((appTemp.getHour().contains(Helper.getClassNumber(Helper.getCurrentDateString())) &&
                                                            Helper.getClassNumber(Helper.getCurrentDateString()) != -1) && appTemp.getDay() == Helper.getDayOfWeekNow());

                                                    refApprovals.child(grpTemp.getApprovalID()).child("isValid").setValue(false);

                                                    if (isAllowedGroup) {
                                                        atLeastOneGroup = true;
                                                        updateApproval();
                                                    }

                                                } else {
                                                    Log.d("caman", "hoowww");
                                                    isAllowedGroup = false;
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError error) {
                                                // Failed to read value
                                                Log.w("failed", "Failed to read value.", error.toException());
                                                isAllowedGroup = false;


                                            }

                                        });
                                    }
                                } else {
                                    if (i == grpTemp.getStudentsIDs().size())
                                        updateApproval();
                                    isAllowedGroup = false;
                                }
                            }
                        }
                        i++;
                    }


                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }


        });


    }

    public void updateApproval() {
        if (atLeastOnePer || atLeastOneTemp || atLeastOneGroup) {
            tv_result.setText("יש אישור");
        } else {
            tv_result.setText("אין אישור");
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        String st = item.getTitle().toString();
        if (st.equals("Logout")) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(guard_screen.this, "Signed out successfully!", Toast.LENGTH_SHORT).show();
                            Intent siMainScreen = new Intent(guard_screen.this, MainActivity.class);
                            siMainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(siMainScreen);
                        }
                    });

        }

        return true;
    }
}
