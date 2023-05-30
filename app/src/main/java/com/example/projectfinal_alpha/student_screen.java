package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refApprovals;
import static com.example.projectfinal_alpha.FBref.refGroups;
import static com.example.projectfinal_alpha.FBref.refStudents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * This activity displays information and functionality for the student user.
 * It allows the student to view their approvals, request new approvals, and join groups.
 *
 * @author Etay Sabag <itay45520@gmail.com>
 * @version 1.2
 * @since 15/10/2022
 */
//todo: add a listview that shows coming approvals for the student
public class student_screen extends AppCompatActivity {
    ImageView iVQrCode;
    TextView tVStatusBtn, name_tv, status_tv;
    Button requestBtn;
    ValueEventListener usrListener, grpListener;
    Student currentStudent;
    CountDownLatch done = new CountDownLatch(1);
    ProgressBar loader;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference().child("uploads");
    Intent siRequests;
    boolean isAllowedTemp = false;
    boolean isAllowedPer = false;
    boolean isAllowedGroup = false;
    private boolean atLeastOneGroup;
    private boolean atLeastOneTemp;
    private boolean atLeastOnePer;

    //TODO: FIX THE NEED TO RELOAD SCREEN TO UPDATE NOT HEAVING A PERMIT TO GO OUT
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_screen);
        iVQrCode = (ImageView) findViewById(R.id.QrcodeImageView);
        tVStatusBtn = (TextView) findViewById(R.id.QrStatusBtn);
        status_tv = (TextView) findViewById(R.id.status_tv);
        name_tv = (TextView) findViewById(R.id.name_tv);
        loader = (ProgressBar) findViewById(R.id.progress_loader);
        requestBtn = (Button) findViewById(R.id.request_btn);

        siRequests = new Intent(this, request_screen.class);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

    }

    protected void onResume() {
        super.onResume();
        currentUser = mAuth.getCurrentUser();
        readStudent();
    }

    /**
     * Handles the visibility switch for the QR code image.
     * @param view The view that was clicked.
     */
    public void qr_visibility_switch(View view) {
        if (iVQrCode.getVisibility() == View.VISIBLE) {
            iVQrCode.setVisibility(View.GONE);
            tVStatusBtn.setText("הצג ברקוד");
        } else {
            iVQrCode.setVisibility(View.VISIBLE);
            tVStatusBtn.setText("הסתר ברקוד");

        }
    }

    /**
     * Creates a QR code based on the given content.
     * @param content The content to encode into the QR code.
     */
    public void create_qr_code(String content) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ((ImageView) findViewById(R.id.QrcodeImageView)).setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }
        loader.setVisibility(View.GONE);

    }

    /**
     * Reads the student data from the database.
     * If the student has an approval, it will be displayed.
     * it also checks if the student has any groups, and if so, it will check for approvals in those groups.
     */
    public void readStudent() {
        Query queryStudent = refStudents.child(currentUser.getUid());
        usrListener = new ValueEventListener() {

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
                                                fillUI(currentStudent);
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
                                                fillUI(currentStudent);
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


                fillUI(currentStudent);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };


        queryStudent.addValueEventListener(usrListener);
        Query queryGroups = refGroups;
        grpListener = new ValueEventListener() {


            /**
             * This method will be called with a snapshot of the data at this location. It will also be called
             * each time that data changes.
             *
             * @param dS The current data at the location
             */
            @Override
            public void onDataChange(DataSnapshot dS) {
                if (currentStudent!=null && (currentStudent.getApprovalID() == null || (!isAllowedTemp && !isAllowedPer))) {
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
                                                        fillUI(currentStudent);
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
                                        fillUI(currentStudent);
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
        };
        queryGroups.addValueEventListener(grpListener);

    }




    /**
     * Sends a request to the request screen activity.
     * @param view The view that was clicked.
     */
    public void sendRequest(View view) {
        startActivity(siRequests);
    }

    /**
     * Fills the UI elements with the student's data.
     * @param currentStudent The current student object.
     */
    public void fillUI(Student currentStudent) {
        Log.d("diff", "the request btn");
        if (currentStudent.getLastRequest() == null || Helper.isMoreThan30Minutes(currentStudent.getLastRequest())) {
            requestBtn.setEnabled(true);
        } else {
            requestBtn.setEnabled(false);
        }


        name_tv.setText("שלום " + currentStudent.getName());
        name_tv.setVisibility(View.VISIBLE);
        Log.d("caman", isAllowedTemp + "  ???");
        loader.setVisibility(View.GONE);

        if (atLeastOnePer || atLeastOneTemp || atLeastOneGroup) {
            status_tv.setText("יש אישור");
            status_tv.setTextColor(Color.GREEN);
            tVStatusBtn.setVisibility(View.VISIBLE);
            requestBtn.setVisibility(View.GONE);
            iVQrCode.setVisibility(View.VISIBLE);
            create_qr_code(currentUser.getUid());
        } else {
            status_tv.setText("אין אישור");
            status_tv.setTextColor(Color.RED);
            tVStatusBtn.setVisibility(View.GONE);
            requestBtn.setVisibility(View.VISIBLE);
            iVQrCode.setVisibility(View.GONE);

        }
        status_tv.setVisibility(View.VISIBLE);

    }

    /**
     * Joins a group by entering a join code.
     * @param view The view that was clicked.
     */
    public void joinGroup(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("הכנס קוד קבוצה");

        final EditText input = new EditText(this);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String joinCode = input.getText().toString();
                refGroups.orderByChild("joinCode").equalTo(joinCode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Group tmpGroup = snapshot.getValue(Group.class);
                                if (tmpGroup.waitingStudentsIDs != null) {
                                    if (tmpGroup.waitingStudentsIDs.contains(currentUser.getUid())) {
                                        Toast.makeText(student_screen.this, "כבר ביקשת להצטרף ל" + tmpGroup.getGroupName(), Toast.LENGTH_LONG).show();
                                    } else if (tmpGroup.getStudentsIDs() != null && tmpGroup.getStudentsIDs().contains(currentUser.getUid())) {
                                        Toast.makeText(student_screen.this, "אתה כבר נמצא בקבוצה " + tmpGroup.getGroupName(), Toast.LENGTH_LONG).show();
                                    } else {
                                        tmpGroup.waitingStudentsIDs.add(currentUser.getUid());
                                        Toast.makeText(student_screen.this, "בקשה להצטרף ל" + tmpGroup.getGroupName() + " נשלחה בהצלחה", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    if (tmpGroup.getStudentsIDs() != null && tmpGroup.getStudentsIDs().contains(currentUser.getUid())) {
                                        Toast.makeText(student_screen.this, "אתה כבר נמצא בקבוצה " + tmpGroup.getGroupName(), Toast.LENGTH_LONG).show();
                                    } else {
                                        ArrayList<String> wStu = new ArrayList<String>();
                                        wStu.add(currentUser.getUid());
                                        snapshot.getRef().child("waitingStudentsIDs").setValue(wStu);
                                        Toast.makeText(student_screen.this, "בקשה להצטרף ל" + tmpGroup.getGroupName() + " נשלחה בהצלחה", Toast.LENGTH_LONG).show();
                                    }
                                }


                            }
                        } else {
                            Toast.makeText(student_screen.this, "קוד לא תקין. קבוצה לא נמצאה!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        String menuTitle = item.getTitle().toString();
        if (menuTitle.equals("Logout")) {
            mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(student_screen.this, "Signed out successfully!", Toast.LENGTH_SHORT).show();
                    Intent siMainScreen = new Intent(student_screen.this, MainActivity.class);
                    siMainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(siMainScreen);
                }
            });

        } else if (menuTitle.equals("Join Group")) {
            joinGroup(null);
        }

        return true;
    }

}


