package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refRequests;
import static com.example.projectfinal_alpha.FBref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Calendar;

public class request_screen extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ArrayList<String> teacherIDs = new ArrayList<String>();
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    EditText reason;
    Spinner day;
    Spinner hour;
    int selectedDay, selectedHour;
    CheckBox repeat;
    Boolean isSpecific;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_screen);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        reason = (EditText) findViewById(R.id.editText_reason);
        day = (Spinner) findViewById(R.id.day_spinner);
        hour = (Spinner) findViewById(R.id.hour_spinner);
        repeat = (CheckBox) findViewById(R.id.check_repeat);

        day = (Spinner) findViewById(R.id.day_spinner);
        hour = (Spinner) findViewById(R.id.hour_spinner);

        day.setOnItemSelectedListener(this);
        hour.setOnItemSelectedListener(this);


    }

    protected void onResume() {
        super.onResume();
        currentUser = mAuth.getCurrentUser();
        radio_clicked_now(null);
    }

    public void send_request(View view) {


            // Create an AlertDialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Set the message and title
            builder.setMessage("את/ה בטוח/ה שאת/ה רוצה להמשיך? אין חרטות")
                    .setTitle("אישור");

            // Set the positive and negative buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    ArrayList<Integer> hours = new ArrayList<>();
                    String timeNow = Helper.getCurrentDateString();

                    if (reason.getText().toString() == null) {
                        Toast.makeText(request_screen.this, "Please enter a reason", Toast.LENGTH_LONG);
                    } else {
                        Request currentRequest;
                        if (isSpecific) {
                            if (selectedHour == 0) {
                                hours.add(1);
                                hours.add(2);
                                hours.add(3);
                                hours.add(4);
                                hours.add(5);
                                hours.add(6);
                                currentRequest = new Request(currentUser.getUid(),
                                        timeNow, reason.getText().toString(), false,
                                        selectedDay, hours, true, false);
                            } else {
                                hours.add(selectedHour);
                                currentRequest = new Request(currentUser.getUid(),
                                        timeNow, reason.getText().toString(), !repeat.isChecked(),
                                        selectedDay, hours, true, false);
                            }
                        } else {
                            Calendar calendar = Calendar.getInstance();
                            hours.add(Helper.getClassNumber(Helper.getCurrentDateString()));
                            currentRequest = new Request(currentUser.getUid(),
                                    timeNow, reason.getText().toString(), false,
                                    calendar.get(Calendar.DAY_OF_WEEK), hours, true, false);
                        }
                        refRequests.push().setValue(currentRequest);
                        hours.clear();
                    }

                    refUsers.child(currentUser.getUid()).child("lastRequest").setValue(timeNow);
                    Toast.makeText(request_screen.this, "success", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked "Cancel" button, dismiss the dialog
                    dialog.dismiss();
                }
            });

            // Create and show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();


        }

//
//
//
//
//
//
//


    public void radio_clicked_now(View view) {
        isSpecific = false;
        day.setEnabled(false);
        hour.setEnabled(false);
        repeat.setEnabled(false);
    }

    public void radio_clicked_specific(View view) {
        isSpecific = true;
        day.setEnabled(true);
        hour.setEnabled(true);
        repeat.setEnabled(true);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView == day) {
            selectedDay = i + 1;
        }
        if (adapterView == hour) {
            selectedHour = i;
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}